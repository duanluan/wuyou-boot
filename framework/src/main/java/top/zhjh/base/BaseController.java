package top.zhjh.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import top.zhjh.base.model.PageVO;
import top.zhjh.base.model.R;

import java.util.List;

@Slf4j
public class BaseController {

  private static final String SAVE_ERROR_MSG = "保存失败";
  private static final String UPDATE_ERROR_MSG = "更新失败";
  private static final String REMOVE_ERROR_MSG = "删除失败";

  private static final int OK_VAL = HttpStatus.OK.value();

  protected static R<?> ok() {
    return new R<>(OK_VAL);
  }

  protected static R<?> ok(String msg) {
    return new R<>(OK_VAL, null, msg);
  }

  protected R ok(Object data) {
    return new R<>(OK_VAL, null, data);
  }

  protected <T> R<List<T>> ok(List<T> data) {
    return new R<>(OK_VAL, null, data);
  }

  protected <T> R<List<T>> ok(Page<T> data) {
    R<List<T>> r = new R<>();
    r.setCode(OK_VAL);
    r.setCurrent(data.getCurrent());
    r.setSize(data.getSize());
    r.setTotal(data.getTotal());
    r.setData(data.getRecords());
    return r;
  }

  protected <T> R<List<T>> ok(PageVO<T> data) {
    R<List<T>> r = new R<>();
    r.setCode(OK_VAL);
    r.setCurrent(data.getCurrent());
    r.setSize(data.getSize());
    r.setTotal(data.getTotal());
    r.setData(data.getRecords());
    return r;
  }

  /**
   * 500
   *
   * @param msg 错误消息
   * @return 响应
   */
  protected static R<?> error(String msg) {
    return new R<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, null);
  }

  /**
   * 400
   *
   * @param msg 错误消息
   * @return 响应
   */
  protected static R<?> badRequest(String msg) {
    return new R<>(HttpStatus.BAD_REQUEST.value(), msg, null);
  }

  /**
   * 未授权
   *
   * @param msg 错误消息
   * @return 响应
   */
  protected static R<?> unauthorized(String msg) {
    return new R<>(HttpStatus.UNAUTHORIZED.value(), msg, null);
  }

  protected static R<?> custom(boolean flag, String failedMsg) {
    if (flag) {
      return ok();
    }
    return error(failedMsg);
  }

  protected static R<?> saveR(boolean flag) {
    if (flag) {
      return ok();
    }
    return error(SAVE_ERROR_MSG);
  }

  protected static R<?> updateR(boolean flag) {
    if (flag) {
      return ok();
    }
    return error(UPDATE_ERROR_MSG);
  }

  protected static R<?> removeR(boolean flag) {
    if (flag) {
      return ok();
    }
    return error(REMOVE_ERROR_MSG);
  }
}
