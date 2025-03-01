package top.zhjh.base.model;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * 分页入参，用于隐藏 Swagger 中的非分页参数
 *
 * @param <T> 实体类型
 */
public class PageQO<T> extends Page<T> implements Serializable {

  @Schema(hidden = true)
  protected List<T> records;
  @Schema(hidden = true)
  protected long total;
  @Schema(hidden = true)
  protected List<OrderItem> orders;
  @Schema(hidden = true)
  protected boolean optimizeCountSql;
  @Schema(hidden = true)
  protected boolean searchCount;
  @Schema(hidden = true)
  protected boolean optimizeJoinOfCountSql;
  @Schema(hidden = true)
  protected String countId;
  @Schema(hidden = true)
  protected Long maxLimit;
  @Schema(hidden = true)
  protected Long pages;

  @Schema(title = "当前页码")
  protected long current = 0L;
  @Schema(title = "每页数量")
  protected long size = 0L;

  @Override
  public long getCurrent() {
    return current;
  }

  /**
   * 设置当前页码，返回当前对象
   * 在列表接口中，为 0 时忽略分页
   *
   * @param current 当前页码
   * @return 分页对象
   */
  @Override
  public Page<T> setCurrent(long current) {
    this.current = current;
    return this;
  }

  @Override
  public long getSize() {
    return size;
  }

  /**
   * 设置每页数量，返回当前对象
   * 在列表接口中，为 0 时忽略分页
   *
   * @param size 每页数量
   * @return 分页对象
   */
  @Override
  public Page<T> setSize(long size) {
    this.size = size;
    return this;
  }
}
