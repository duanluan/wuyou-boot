package top.zhjh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 对照 README 中“多租户 + 数据权限”章节的集成测试。
 * <p>
 * 本类逐个验证以下登录用户的实际行为是否与 README 文案一致：
 * <p>
 * 1. superAdmin（超级管理员）
 * 2. tenant1Admin（租户管理员）
 * 3. tenant1AllUser（全部权限）
 * 4. tenant1CustomUser（自定义权限：查部门1+2，改仅部门2）
 * 5. tenant1CurrentDeptAndChildrenUser（本部门及以下，所在部门：部门2）
 * 6. tenant1CurrentDeptUser（本部门，所在部门：部门2）
 * 7. tenant1OnlySelfUser（仅本人，所在部门：部门1）
 * <p>
 * 同时对照 README 中这 5 条基础测试数据：
 * <p>
 * 1. 数据-总部
 * 2. 数据-部门1-管理员创建
 * 3. 数据-部门1-仅本人创建
 * 4. 数据-部门2
 * 5. 数据-部门2子部门
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class, properties = "wuyou.security.login.captcha-enabled=false")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadmeMultiTenantDataScopeTest {

  private static final String PASSWORD = "123";
  /**
   * 测试期间临时创建的数据统一使用此前缀，便于每次执行前后清理。
   */
  private static final String TEMP_PREFIX = "IT-README-MULTI-TENANT-";
  /**
   * README 中固定使用的租户名：租户1。
   */
  private static final String TENANT1_NAME = "租户1";

  /**
   * README 中“测试数据管理”里的 5 条基础测试数据。
   */
  private static final String DATA_HEADQUARTERS = "数据-总部";
  private static final String DATA_DEPT1_ADMIN = "数据-部门1-管理员创建";
  private static final String DATA_DEPT1_ONLY_SELF = "数据-部门1-仅本人创建";
  private static final String DATA_DEPT2 = "数据-部门2";
  private static final String DATA_DEPT2_CHILD = "数据-部门2子部门";

  /**
   * 用于校验“查询结果”时的基础数据全集。
   */
  private static final Set<String> BASE_VALUES = Set.of(
    DATA_HEADQUARTERS,
    DATA_DEPT1_ADMIN,
    DATA_DEPT1_ONLY_SELF,
    DATA_DEPT2,
    DATA_DEPT2_CHILD
  );

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private Long tenant1Id;
  private Long hqDeptId;
  private Long dept1Id;
  private Long dept2Id;
  private Long dept2ChildId;

  private Long dept1AdminDataId;
  private Long dept1OnlySelfDataId;
  private Long dept2DataId;
  private Long dept2ChildDataId;
  private String queryFixturePrefix;

  /**
   * 按 README 文案准备测试夹具：
   * <p>
   * 1. 读取“租户1”和各部门 ID。
    * 2. 校验 README 中列出的 5 条基础测试数据存在。
   * 3. 构造一组仅供“查询结果”断言使用的独立夹具数据。
   * 4. 缓存后续更新/删除断言要用到的数据 ID。
   */
  @BeforeAll
  void loadFixture() throws Exception {
    tenant1Id = findTenant1Id();
    hqDeptId = findReadmeDeptId("租户1-总部");
    dept1Id = findReadmeDeptId("租户1-部门1");
    dept2Id = findReadmeDeptId("租户1-部门2");
    dept2ChildId = findReadmeDeptId("租户1-部门2子部门");

    List<String> existingValues = listTenant1DataValues();

    dept1AdminDataId = findTenant1BaseDataId(DATA_DEPT1_ADMIN);
    dept1OnlySelfDataId = findTenant1BaseDataId(DATA_DEPT1_ONLY_SELF);
    dept2DataId = findTenant1BaseDataId(DATA_DEPT2);
    dept2ChildDataId = findTenant1BaseDataId(DATA_DEPT2_CHILD);

    assertThat(findTenant1BaseDataId(DATA_HEADQUARTERS)).withFailMessage("missing base data: %s, existing=%s", DATA_HEADQUARTERS, existingValues).isNotNull();
    assertThat(dept1AdminDataId).withFailMessage("missing base data: %s, existing=%s", DATA_DEPT1_ADMIN, existingValues).isNotNull();
    assertThat(dept1OnlySelfDataId).withFailMessage("missing base data: %s, existing=%s", DATA_DEPT1_ONLY_SELF, existingValues).isNotNull();
    assertThat(dept2DataId).withFailMessage("missing base data: %s, existing=%s", DATA_DEPT2, existingValues).isNotNull();
    assertThat(dept2ChildDataId).withFailMessage("missing base data: %s, existing=%s", DATA_DEPT2_CHILD, existingValues).isNotNull();

    queryFixturePrefix = "IT-README-QUERY-FIXTURE-" + UUID.randomUUID().toString().replace("-", "") + "-";
    createDataAs("tenant1Admin", tenant1Id, queryFixtureValue(DATA_HEADQUARTERS), hqDeptId);
    createDataAs("tenant1Admin", tenant1Id, queryFixtureValue(DATA_DEPT1_ADMIN), dept1Id);
    createDataAs("tenant1OnlySelfUser", tenant1Id, queryFixtureValue(DATA_DEPT1_ONLY_SELF), dept1Id);
    createDataAs("tenant1Admin", tenant1Id, queryFixtureValue(DATA_DEPT2), dept2Id);
    createDataAs("tenant1Admin", tenant1Id, queryFixtureValue(DATA_DEPT2_CHILD), dept2ChildId);
  }

  @AfterAll
  void cleanupQueryFixtures() {
    if (queryFixturePrefix != null) {
      jdbcTemplate.update("UPDATE sys_test_data SET deleted = 1 WHERE deleted = 0 AND value LIKE ?", queryFixturePrefix + "%");
    }
  }

  /**
   * 清理本测试类创建的临时数据，并把 README 中会被更新的关键基础数据恢复原值。
   */
  @BeforeEach
  @AfterEach
  void cleanupTempData() {
    jdbcTemplate.update("UPDATE sys_test_data SET deleted = 1 WHERE deleted = 0 AND value LIKE ?", TEMP_PREFIX + "%");
    restoreValue(dept1OnlySelfDataId, DATA_DEPT1_ONLY_SELF);
    restoreValue(dept2DataId, DATA_DEPT2);
    restoreValue(dept2ChildDataId, DATA_DEPT2_CHILD);
  }

  /**
   * README 文案：
   * <p>
   * 登录 superAdmin（超级管理员）：
   * <p>
   * 1. 查询结果：查看所有 5 条数据。
   * 2. 新增结果：可以在任意部门创建数据。
   * 3. 增删改结果：可以修改/删除所有 5 条数据。
   */
  @Test
  void superAdminMatchesReadme() throws Exception {
    Cookie[] cookies = login("superAdmin", null);

    assertVisibleBaseValues(cookies, BASE_VALUES);

    String saveValue = tempValue("super-admin-save");
    assertOk(save(cookies, saveValue, dept2ChildId));
    Long savedId = findUniqueDataId(saveValue);
    assertThat(savedId).isNotNull();
    assertThat(findDeptIdByDataId(savedId)).isEqualTo(dept2ChildId);

    String updatedValue = tempValue("super-admin-update");
    assertOk(update(cookies, dept1OnlySelfDataId, updatedValue));
    assertThat(findValue(dept1OnlySelfDataId)).isEqualTo(updatedValue);

    Long deleteTargetId = createDataAs("tenant1OnlySelfUser", tenant1Id, tempValue("super-admin-delete"), dept1Id);
    assertOk(removeById(cookies, deleteTargetId));
    assertThat(isDeleted(deleteTargetId)).isTrue();
  }

  /**
   * README 文案：
   * <p>
   * 登录 tenant1Admin（租户管理员）：
   * <p>
   * 1. 查询结果：查看所有 5 条数据。
   * 2. 新增结果：可以在任意部门创建数据。
   * 3. 增删改结果：可以修改/删除所有 5 条数据。
   */
  @Test
  void tenant1AdminMatchesReadme() throws Exception {
    Cookie[] cookies = login("tenant1Admin", tenant1Id);

    assertVisibleBaseValues(cookies, BASE_VALUES);

    String saveValue = tempValue("tenant-admin-save");
    assertOk(save(cookies, saveValue, dept2ChildId));
    Long savedId = findUniqueDataId(saveValue);
    assertThat(savedId).isNotNull();
    assertThat(findDeptIdByDataId(savedId)).isEqualTo(dept2ChildId);

    String updatedValue = tempValue("tenant-admin-update");
    assertOk(update(cookies, dept1OnlySelfDataId, updatedValue));
    assertThat(findValue(dept1OnlySelfDataId)).isEqualTo(updatedValue);

    Long deleteTargetId = createDataAs("tenant1OnlySelfUser", tenant1Id, tempValue("tenant-admin-delete"), dept1Id);
    assertOk(removeById(cookies, deleteTargetId));
    assertThat(isDeleted(deleteTargetId)).isTrue();
  }

  /**
   * README 文案：
   * <p>
   * 登录 tenant1AllUser（全部权限）：
   * <p>
   * 1. 查询结果：查看所有 5 条数据。
   * 2. 新增结果：可以在任意部门创建数据。
   * 3. 增删改结果：可以修改/删除所有 5 条数据。
   */
  @Test
  void tenant1AllUserMatchesReadme() throws Exception {
    Cookie[] cookies = login("tenant1AllUser", tenant1Id);

    assertVisibleBaseValues(cookies, BASE_VALUES);

    String saveValue = tempValue("tenant-all-save");
    assertOk(save(cookies, saveValue, hqDeptId));
    Long savedId = findUniqueDataId(saveValue);
    assertThat(savedId).isNotNull();
    assertThat(findDeptIdByDataId(savedId)).isEqualTo(hqDeptId);

    String updatedValue = tempValue("tenant-all-update");
    assertOk(update(cookies, dept1OnlySelfDataId, updatedValue));
    assertThat(findValue(dept1OnlySelfDataId)).isEqualTo(updatedValue);

    Long deleteTargetId = createDataAs("tenant1OnlySelfUser", tenant1Id, tempValue("tenant-all-delete"), dept1Id);
    assertOk(removeById(cookies, deleteTargetId));
    assertThat(isDeleted(deleteTargetId)).isTrue();
  }

  /**
   * README 文案：
   * <p>
   * 登录 tenant1CustomUser（自定义权限：查部门1+2，改仅部门2）：
   * <p>
   * 1. 查询结果：查看 3 条数据
   * （数据-部门1-管理员创建、数据-部门1-仅本人创建、数据-部门2）。
   * 2. 新增结果：
   * - 在 租户1-部门2 新增：成功。
   * - 在 租户1-部门1 新增：失败（抛出异常：“无权在所选部门操作数据”）。
   * 3. 增删改结果：
   * - 修改 数据-部门2：成功。
   * - 修改 数据-部门1-管理员创建：失败（提示“无增删改权限”）。
   */
  @Test
  void tenant1CustomUserMatchesReadme() throws Exception {
    Cookie[] cookies = login("tenant1CustomUser", tenant1Id);

    assertVisibleBaseValues(cookies, Set.of(DATA_DEPT1_ADMIN, DATA_DEPT1_ONLY_SELF, DATA_DEPT2));

    String saveOkValue = tempValue("custom-save-ok");
    assertOk(save(cookies, saveOkValue, dept2Id));
    Long saveOkId = findUniqueDataId(saveOkValue);
    assertThat(saveOkId).isNotNull();
    assertThat(findDeptIdByDataId(saveOkId)).isEqualTo(dept2Id);

    assertServerError(save(cookies, tempValue("custom-save-fail"), dept1Id), "无权在所选部门创建数据");

    String updatedValue = tempValue("custom-update-ok");
    assertOk(update(cookies, dept2DataId, updatedValue));
    assertThat(findValue(dept2DataId)).isEqualTo(updatedValue);

    assertServerError(update(cookies, dept1AdminDataId, tempValue("custom-update-fail")), "无增删改权限");

    assertOk(removeById(cookies, saveOkId));
    assertThat(isDeleted(saveOkId)).isTrue();
    assertServerError(removeById(cookies, dept1AdminDataId), "无删除权限或数据不存在");
  }

  /**
   * README 文案：
   * <p>
   * 登录 tenant1CurrentDeptAndChildrenUser（本部门及以下，所在部门：部门2）：
   * <p>
   * 1. 查询结果：查看 2 条数据（数据-部门2、数据-部门2子部门）。
   * 2. 新增结果：可以在 租户1-部门2 和 租户1-部门2子部门 新增；在其他部门新增失败。
   * 3. 增删改结果：可以修改/删除这 2 条数据。
   */
  @Test
  void tenant1CurrentDeptAndChildrenUserMatchesReadme() throws Exception {
    Cookie[] cookies = login("tenant1CurrentDeptAndChildrenUser", tenant1Id);

    assertVisibleBaseValues(cookies, Set.of(DATA_DEPT2, DATA_DEPT2_CHILD));

    String dept2Value = tempValue("current-dept-children-save-dept2");
    assertOk(save(cookies, dept2Value, dept2Id));
    Long dept2SaveId = findUniqueDataId(dept2Value);
    assertThat(dept2SaveId).isNotNull();

    String childValue = tempValue("current-dept-children-save-child");
    assertOk(save(cookies, childValue, dept2ChildId));
    Long childSaveId = findUniqueDataId(childValue);
    assertThat(childSaveId).isNotNull();

    assertServerError(save(cookies, tempValue("current-dept-children-save-fail"), dept1Id), "无权在所选部门创建数据");

    String updatedValue = tempValue("current-dept-children-update-ok");
    assertOk(update(cookies, dept2ChildDataId, updatedValue));
    assertThat(findValue(dept2ChildDataId)).isEqualTo(updatedValue);

    assertOk(removeById(cookies, dept2SaveId));
    assertThat(isDeleted(dept2SaveId)).isTrue();
    assertOk(removeById(cookies, childSaveId));
    assertThat(isDeleted(childSaveId)).isTrue();
  }

  /**
   * README 文案：
   * <p>
   * 登录 tenant1CurrentDeptUser（本部门，所在部门：部门2）：
   * <p>
   * 1. 查询结果：仅查看 1 条数据（数据-部门2）。
   * 2. 新增结果：仅可以在 租户1-部门2 新增；在子部门或其他部门新增失败。
   * 3. 增删改结果：仅可以修改/删除这 1 条数据。
   */
  @Test
  void tenant1CurrentDeptUserMatchesReadme() throws Exception {
    Cookie[] cookies = login("tenant1CurrentDeptUser", tenant1Id);

    assertVisibleBaseValues(cookies, Set.of(DATA_DEPT2));

    String saveOkValue = tempValue("current-dept-save-ok");
    assertOk(save(cookies, saveOkValue, dept2Id));
    Long saveOkId = findUniqueDataId(saveOkValue);
    assertThat(saveOkId).isNotNull();
    assertThat(findDeptIdByDataId(saveOkId)).isEqualTo(dept2Id);

    assertServerError(save(cookies, tempValue("current-dept-save-child-fail"), dept2ChildId), "无权在所选部门创建数据");
    assertServerError(save(cookies, tempValue("current-dept-save-fail"), dept1Id), "无权在所选部门创建数据");

    String updatedValue = tempValue("current-dept-update-ok");
    assertOk(update(cookies, dept2DataId, updatedValue));
    assertThat(findValue(dept2DataId)).isEqualTo(updatedValue);

    assertServerError(update(cookies, dept2ChildDataId, tempValue("current-dept-update-fail")), "无增删改权限");

    assertOk(removeById(cookies, saveOkId));
    assertThat(isDeleted(saveOkId)).isTrue();
  }

  /**
   * README 文案：
   * <p>
   * 登录 tenant1OnlySelfUser（仅本人，所在部门：部门1）：
   * <p>
   * 1. 查询结果：仅查看 1 条数据（数据-部门1-仅本人创建）。
   * 2. 新增结果：可以在 租户1-部门1 新增；在其他部门新增失败。
   * 3. 增删改结果：
   * - 修改 数据-部门1-仅本人创建：成功。
   * - 修改 数据-部门1-管理员创建：失败。
   */
  @Test
  void tenant1OnlySelfUserMatchesReadme() throws Exception {
    Cookie[] cookies = login("tenant1OnlySelfUser", tenant1Id);

    assertVisibleBaseValues(cookies, Set.of(DATA_DEPT1_ONLY_SELF));

    String saveOkValue = tempValue("only-self-save-ok");
    assertOk(save(cookies, saveOkValue, dept1Id));
    Long saveOkId = findUniqueDataId(saveOkValue);
    assertThat(saveOkId).isNotNull();
    assertThat(findDeptIdByDataId(saveOkId)).isEqualTo(dept1Id);

    assertServerError(save(cookies, tempValue("only-self-save-fail"), dept2Id), "无权在所选部门创建数据");

    String updatedValue = tempValue("only-self-update-ok");
    assertOk(update(cookies, dept1OnlySelfDataId, updatedValue));
    assertThat(findValue(dept1OnlySelfDataId)).isEqualTo(updatedValue);

    assertServerError(update(cookies, dept1AdminDataId, tempValue("only-self-update-fail")), "无增删改权限");

    assertOk(removeById(cookies, saveOkId));
    assertThat(isDeleted(saveOkId)).isTrue();
  }

  /**
   * 登录辅助方法。
   * <p>
   * README 里说明“超级管理员登录是无需租户的”，
   * 所以 superAdmin 传 null，其余租户用户传 tenant1Id。
   */
  private Cookie[] login(String username, Long tenantId) throws Exception {
    var builder = post("/login")
      .param("username", username)
      .param("password", PASSWORD);
    if (tenantId != null) {
      builder.param("tenantId", tenantId.toString());
    }
    var result = mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andReturn();
    JsonNode response = readJson(result.getResponse().getContentAsString());
    assertThat(response.path("code").asInt()).isEqualTo(200);
    Cookie[] cookies = result.getResponse().getCookies();
    assertThat(cookies).isNotEmpty();
    return cookies;
  }

  /**
   * 查询“测试数据列表”接口。
   * <p>
   * 当传入 value 时，使用 value like 过滤，便于只观察本次测试构造的夹具数据。
   */
  private JsonNode list(Cookie[] cookies, String value) throws Exception {
    var builder = get("/sys/testData")
      .cookie(cookies)
      .param("current", "1")
      .param("size", "200");
    if (value != null) {
      builder.param("value", value);
    }
    return readJson(mockMvc.perform(builder)
      .andReturn()
      .getResponse()
      .getContentAsString());
  }

  /**
   * 对应 README 中“新增结果”的接口调用。
   */
  private JsonNode save(Cookie[] cookies, String value, Long deptId) throws Exception {
    return readJson(mockMvc.perform(post("/sys/testData")
        .cookie(cookies)
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("value", value, "deptId", deptId))))
      .andReturn()
      .getResponse()
      .getContentAsString());
  }

  /**
   * 对应 README 中“修改”场景的接口调用。
   */
  private JsonNode update(Cookie[] cookies, Long id, String value) throws Exception {
    return readJson(mockMvc.perform(put("/sys/testData/{id}", id)
        .cookie(cookies)
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("id", id, "value", value))))
      .andReturn()
      .getResponse()
      .getContentAsString());
  }

  /**
   * 对应 README 中“删除”场景的接口调用。
   */
  private JsonNode removeById(Cookie[] cookies, Long id) throws Exception {
    return readJson(mockMvc.perform(delete("/sys/testData/{ids}", id).cookie(cookies))
      .andReturn()
      .getResponse()
      .getContentAsString());
  }

  /**
   * 断言接口返回成功。
   */
  private void assertOk(JsonNode response) {
    assertThat(response.path("code").asInt()).isEqualTo(200);
  }

  /**
   * 断言接口返回 500，并包含 README 对应的失败提示文案。
   */
  private void assertServerError(JsonNode response, String expectedMsg) {
    assertThat(response.path("code").asInt()).isEqualTo(500);
    assertThat(response.path("msg").asText()).contains(expectedMsg);
  }

  /**
   * 校验“查询结果”是否与 README 规定的可见范围一致。
   * <p>
   * 这里不直接扫共享环境中的全部测试数据，而是只看当前测试构造的独立夹具数据。
   */
  private void assertVisibleBaseValues(Cookie[] cookies, Set<String> expectedValues) throws Exception {
    JsonNode response = list(cookies, queryFixturePrefix);
    assertThat(response.path("code").asInt()).isEqualTo(200);
    assertThat(response.path("total").asInt()).isEqualTo(expectedValues.size());
    assertThat(response.path("data").size()).isEqualTo(expectedValues.size());
    Set<String> actualValues = StreamSupport.stream(response.path("data").spliterator(), false)
      .map(node -> node.path("value").asText())
      .collect(Collectors.toSet());
    assertThat(actualValues).isEqualTo(toQueryFixtureValues(expectedValues));
  }

  /**
   * 先以指定用户创建数据，再返回新数据 ID。
   * <p>
   * 主要用于验证“删除结果”时，先构造一条当前用例可删除的数据。
   */
  private Long createDataAs(String username, Long tenantId, String value, Long deptId) throws Exception {
    Cookie[] cookies = login(username, tenantId);
    assertOk(save(cookies, value, deptId));
    Long id = findUniqueDataId(value);
    assertThat(id).isNotNull();
    return id;
  }

  /**
   * 统一解析 JSON 响应。
   */
  private JsonNode readJson(String content) throws Exception {
    return objectMapper.readTree(content);
  }

  /**
   * 生成测试临时数据值。
   */
  private String tempValue(String key) {
    return TEMP_PREFIX + key + "-" + UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * 把 README 中的基础数据值映射为本次测试独立夹具数据值。
   */
  private String queryFixtureValue(String baseValue) {
    return queryFixturePrefix + baseValue;
  }

  /**
   * 把一组 README 基础值映射为当前测试独立夹具值集合。
   */
  private Set<String> toQueryFixtureValues(Set<String> baseValues) {
    return baseValues.stream().map(this::queryFixtureValue).collect(Collectors.toSet());
  }

  /**
   * 查询 README 中“租户1”的租户 ID。
   */
  private Long findTenant1Id() {
    return jdbcTemplate.queryForObject(
      "SELECT id FROM sys_tenant WHERE deleted = 0 AND name = ? LIMIT 1",
      Long.class,
      TENANT1_NAME
    );
  }

  /**
   * 按 README 场景查询部门 ID。
   * <p>
   * 优先命中 tenant_id = tenant1Id；
   * 仅“租户1-总部”允许回退到 tenant_id is null，
   * 其余部门必须命中 tenant_id = tenant1Id。
   */
  private Long findReadmeDeptId(String deptName) {
    if (DATA_HEADQUARTERS.equals(deptName)) {
      return jdbcTemplate.queryForObject(
        "SELECT id FROM sys_dept " +
          "WHERE deleted = 0 AND name = ? AND (tenant_id = ? OR tenant_id IS NULL) " +
          "ORDER BY CASE WHEN tenant_id = ? THEN 0 WHEN tenant_id IS NULL THEN 1 ELSE 2 END, id DESC LIMIT 1",
        Long.class,
        deptName,
        tenant1Id,
        tenant1Id
      );
    }
    return jdbcTemplate.queryForObject(
      "SELECT id FROM sys_dept WHERE deleted = 0 AND tenant_id = ? AND name = ? ORDER BY id DESC LIMIT 1",
      Long.class,
      tenant1Id,
      deptName
    );
  }

  /**
   * 列出“租户1”下当前存在的全部测试数据值，便于基线缺失时输出诊断信息。
   */
  private List<String> listTenant1DataValues() {
    return jdbcTemplate.query(
      "SELECT value FROM sys_test_data WHERE deleted = 0 AND tenant_id = ? ORDER BY id",
      (rs, rowNum) -> rs.getString("value"),
      tenant1Id
    );
  }

  /**
   * 按测试数据值查询“租户1”基线数据 ID。
   */
  private Long findTenant1BaseDataId(String value) {
    List<Long> ids = jdbcTemplate.query(
      "SELECT id FROM sys_test_data WHERE deleted = 0 AND tenant_id = ? AND value = ? ORDER BY id DESC",
      (rs, rowNum) -> rs.getLong("id"),
      tenant1Id,
      value
    );
    return ids.isEmpty() ? null : ids.get(0);
  }

  /**
   * 按唯一临时值查询最新一条未删除数据 ID。
   * <p>
   * 临时值带 UUID，理论上不会和共享环境中的其他记录冲突。
   */
  private Long findUniqueDataId(String value) {
    List<Long> ids = jdbcTemplate.query(
      "SELECT id FROM sys_test_data WHERE deleted = 0 AND value = ? ORDER BY id DESC",
      (rs, rowNum) -> rs.getLong("id"),
      value
    );
    return ids.isEmpty() ? null : ids.get(0);
  }

  /**
   * 查询某条测试数据当前所属部门。
   */
  private Long findDeptIdByDataId(Long id) {
    return jdbcTemplate.queryForObject(
      "SELECT dept_id FROM sys_test_data WHERE id = ?",
      Long.class,
      id
    );
  }

  /**
   * 查询某条测试数据当前值。
   */
  private String findValue(Long id) {
    return jdbcTemplate.queryForObject(
      "SELECT value FROM sys_test_data WHERE id = ?",
      String.class,
      id
    );
  }

  /**
   * 判断某条测试数据是否已被逻辑删除。
   */
  private boolean isDeleted(Long id) {
    int count = jdbcTemplate.queryForObject(
      "SELECT COUNT(1) FROM sys_test_data WHERE id = ? AND deleted = 1",
      Integer.class,
      id
    );
    return count > 0;
  }

  /**
   * 把被当前测试改动过的基础数据恢复为 README 中的原始值。
   */
  private void restoreValue(Long id, String value) {
    if (id != null) {
      jdbcTemplate.update("UPDATE sys_test_data SET value = ?, deleted = 0 WHERE id = ?", value, id);
    }
  }
}
