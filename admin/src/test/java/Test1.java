import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import top.zhjh.Application;
import top.zhjh.model.entity.SysRoleUser;
import top.zhjh.model.entity.SysUser;
import top.zhjh.service.SysRoleService;
import top.zhjh.service.SysRoleUserService;
import top.zhjh.service.SysUserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("local")
@SpringBootTest(classes = Application.class)
public class Test1 {

  @Resource
  private SysUserService userService;
  @Resource
  private SysRoleUserService roleUserService;
  @Resource
  private SysRoleService roleService;

  @Test
  public void test() {
    List<SysUser> userList = userService.list();
    for (SysUser user : userList) {
      SysUser updateUser = new SysUser();
      updateUser.setId(user.getId());
      updateUser.setRoleIds(new ArrayList<>());
      updateUser.setRoleNames(new ArrayList<>());
      List<SysRoleUser> roleUserList = roleUserService.lambdaQuery().eq(SysRoleUser::getUserId, user.getId()).list();
      for (SysRoleUser sysRoleUser : roleUserList) {
        Long roleId = sysRoleUser.getRoleId();
        updateUser.getRoleIds().add(roleId);
        updateUser.getRoleNames().add(roleService.getById(roleId).getName());
      }
      userService.updateById(updateUser);
    }
  }
}
