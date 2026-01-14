package top.zhjh.model.qo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zhjh.base.model.PageQO;
import top.zhjh.model.entity.SysTestData;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysTestDataPageQO extends PageQO<SysTestData> {
  private String value;
}