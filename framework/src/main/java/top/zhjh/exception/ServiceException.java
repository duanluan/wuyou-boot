package top.zhjh.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

/**
 * 服务异常
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ServiceException extends RuntimeException {

  private HttpStatus status;

  public ServiceException(@NonNull final HttpStatus status, @NonNull String message) {
    super(message);
    this.status = status;
  }

  public ServiceException(@NonNull String message) {
    super(message);
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
