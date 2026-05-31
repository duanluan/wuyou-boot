package top.zhjh.config.dict;

import org.springframework.stereotype.Component;
import top.zhjh.util.SystemDictUtil;

import java.util.List;

/**
 * 系统字典注册表
 */
@Component
public class SystemDictRegistry {

  private final List<SystemDictDefinition> definitions = List.of(
    SystemDictDefinition.builder()
      .dictKey(SystemDictUtil.COMMON_STATUS)
      .defaultName("通用状态")
      .defaultRemarks("系统通用启用禁用状态")
      .systemBuiltIn(true)
      .codeReferenced(true)
      .allowTenantOverride(true)
      .items(List.of(
        SystemDictItemDefinition.builder()
          .itemValue("0")
          .defaultLabel("禁用")
          .defaultDescription("禁用")
          .defaultSortOrder(1)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build(),
        SystemDictItemDefinition.builder()
          .itemValue("1")
          .defaultLabel("启用")
          .defaultDescription("启用")
          .defaultSortOrder(2)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build()
      ))
      .build(),
    SystemDictDefinition.builder()
      .dictKey(SystemDictUtil.MENU_TYPE)
      .defaultName("菜单类型")
      .defaultRemarks("系统菜单类型")
      .systemBuiltIn(true)
      .codeReferenced(true)
      .allowTenantOverride(true)
      .items(List.of(
        SystemDictItemDefinition.builder()
          .itemValue("1")
          .defaultLabel("目录")
          .defaultDescription("目录")
          .defaultSortOrder(1)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build(),
        SystemDictItemDefinition.builder()
          .itemValue("2")
          .defaultLabel("菜单")
          .defaultDescription("菜单")
          .defaultSortOrder(2)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build(),
        SystemDictItemDefinition.builder()
          .itemValue("3")
          .defaultLabel("按钮")
          .defaultDescription("按钮")
          .defaultSortOrder(3)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build()
      ))
      .build(),
    SystemDictDefinition.builder()
      .dictKey(SystemDictUtil.MENU_METHOD)
      .defaultName("菜单请求方法")
      .defaultRemarks("系统菜单请求方法")
      .systemBuiltIn(true)
      .codeReferenced(true)
      .allowTenantOverride(true)
      .items(List.of(
        SystemDictItemDefinition.builder()
          .itemValue("GET")
          .defaultLabel("GET")
          .defaultDescription("GET 请求")
          .defaultSortOrder(1)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build(),
        SystemDictItemDefinition.builder()
          .itemValue("POST")
          .defaultLabel("POST")
          .defaultDescription("POST 请求")
          .defaultSortOrder(2)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build(),
        SystemDictItemDefinition.builder()
          .itemValue("PUT")
          .defaultLabel("PUT")
          .defaultDescription("PUT 请求")
          .defaultSortOrder(3)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build(),
        SystemDictItemDefinition.builder()
          .itemValue("DELETE")
          .defaultLabel("DELETE")
          .defaultDescription("DELETE 请求")
          .defaultSortOrder(4)
          .systemBuiltIn(true)
          .codeReferenced(true)
          .build()
      ))
      .build(),
    SystemDictDefinition.builder()
      .dictKey(SystemDictUtil.DATA_SCOPE_TYPE)
      .defaultName("数据权限类型")
      .defaultRemarks("角色数据权限类型")
      .systemBuiltIn(true)
      .codeReferenced(true)
      .allowTenantOverride(true)
      .items(List.of(
        SystemDictItemDefinition.builder().itemValue("1").defaultLabel("全部").defaultDescription("全部").defaultSortOrder(1).systemBuiltIn(true).codeReferenced(true).build(),
        SystemDictItemDefinition.builder().itemValue("2").defaultLabel("自定义").defaultDescription("自定义").defaultSortOrder(2).systemBuiltIn(true).codeReferenced(true).build(),
        SystemDictItemDefinition.builder().itemValue("3").defaultLabel("本部门及以下").defaultDescription("本部门及以下").defaultSortOrder(3).systemBuiltIn(true).codeReferenced(true).build(),
        SystemDictItemDefinition.builder().itemValue("4").defaultLabel("本部门").defaultDescription("本部门").defaultSortOrder(4).systemBuiltIn(true).codeReferenced(true).build(),
        SystemDictItemDefinition.builder().itemValue("5").defaultLabel("仅本人").defaultDescription("仅本人").defaultSortOrder(5).systemBuiltIn(true).codeReferenced(true).build()
      ))
      .build(),
    SystemDictDefinition.builder()
      .dictKey(SystemDictUtil.DATA_SCOPE_ACTION_TYPE)
      .defaultName("数据权限操作类型")
      .defaultRemarks("角色数据权限操作类型")
      .systemBuiltIn(true)
      .codeReferenced(true)
      .allowTenantOverride(true)
      .items(List.of(
        SystemDictItemDefinition.builder().itemValue("1").defaultLabel("查询").defaultDescription("查询").defaultSortOrder(1).systemBuiltIn(true).codeReferenced(true).build(),
        SystemDictItemDefinition.builder().itemValue("2").defaultLabel("增删改").defaultDescription("增删改").defaultSortOrder(2).systemBuiltIn(true).codeReferenced(true).build()
      ))
      .build(),
    SystemDictDefinition.builder()
      .dictKey(SystemDictUtil.YES_NO)
      .defaultName("是否")
      .defaultRemarks("通用是/否选项")
      .systemBuiltIn(true)
      .codeReferenced(true)
      .allowTenantOverride(true)
      .items(List.of(
        SystemDictItemDefinition.builder().itemValue("1").defaultLabel("是").defaultDescription("是").defaultSortOrder(1).systemBuiltIn(true).codeReferenced(true).build(),
        SystemDictItemDefinition.builder().itemValue("0").defaultLabel("否").defaultDescription("否").defaultSortOrder(2).systemBuiltIn(true).codeReferenced(true).build()
      ))
      .build()
  );

  public List<SystemDictDefinition> list() {
    return definitions;
  }
}
