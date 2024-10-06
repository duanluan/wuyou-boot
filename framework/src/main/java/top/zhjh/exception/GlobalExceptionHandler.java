package top.zhjh.exception;

import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.csaf.lang.StrUtil;
import top.zhjh.base.model.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理：<a href="https://www.jianshu.com/p/fce0f9b88bfc">https://www.jianshu.com/p/fce0f9b88bfc</a>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ConnectException.class)
  @ResponseBody
  public ResponseEntity<R> handleConnectException(ConnectException e) {
    log.error("连接异常", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new R(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统维护中"));
  }

  @ExceptionHandler(SQLException.class)
  @ResponseBody
  public ResponseEntity<R> handleSqlExcxeption(SQLException e) {
    log.error("数据库异常", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new R(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统维护中"));
  }

  @ExceptionHandler({BindException.class})
  @ResponseBody
  public ResponseEntity<R> handleBindException(BindException e) {
    log.error("参数异常", e);
    HttpStatus status = HttpStatus.BAD_REQUEST;
    List<ObjectError> errorList = e.getAllErrors();
    List<String> msgList = new ArrayList<>();
    for (ObjectError error : errorList) {
      // 绑定失败
      if (((FieldError) error).isBindingFailure()) {
        msgList.add("参数 " + ((FieldError) error).getField() + " 绑定失败");
      } else {
        msgList.add(error.getDefaultMessage());
      }
    }
    return ResponseEntity.status(status).body(new R(status.value(), StrUtil.join(msgList, "；")));
  }

  @ExceptionHandler({ServiceException.class})
  @ResponseBody
  public ResponseEntity<R> handleServiceException(ServiceException e) {
    log.error("服务异常", e);
    HttpStatus status = e.getStatus();
    return ResponseEntity.status(status).body(new R(status.value(), e.getMessage()));
  }

  @ExceptionHandler(NotLoginException.class)
  @ResponseBody
  public ResponseEntity<R> handlerNotLoginException(NotLoginException nle, HttpServletRequest request, HttpServletResponse response) {
    log.error("登录异常", nle);
    // 判断场景值，定制化异常信息
    String message = "";
    if (nle.getType().equals(NotLoginException.NOT_TOKEN)) {
      message = "未提供Token";
    } else if (nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
      message = "Token无效";
    } else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
      message = "Token已过期";
    } else if (nle.getType().equals(NotLoginException.BE_REPLACED)) {
      message = "Token已被顶下线";
    } else if (nle.getType().equals(NotLoginException.KICK_OUT)) {
      message = "Token已被踢下线";
    } else {
      message = "当前会话未登录";
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new R(nle.getCode(), message));
  }

  @ExceptionHandler({Exception.class})
  @ResponseBody
  public ResponseEntity<R> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new R(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统异常"));
  }
}
