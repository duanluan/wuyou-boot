package top.zhjh.base.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应
 *
 * @param <T> 实体类型
 */
@Schema(title = "分页响应")
@Data
public class PageVO<T> implements Serializable {

  @Schema(title = "分页数据")
  private List<T> records;
  @Schema(title = "当前页码")
  private long current;
  @Schema(title = "每页数量")
  private long size;
  @Schema(title = "总条数")
  private long total;

  public PageVO() {
  }

  public PageVO(Page<T> page) {
    this.records = page.getRecords();
    this.total = page.getTotal();
    this.size = page.getSize();
    this.current = page.getCurrent();
  }
}
