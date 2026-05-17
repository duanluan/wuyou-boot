package top.zhjh.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(title = "验证码响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {

  @Schema(title = "验证码标识")
  // 前端登录时需要原样回传该标识，后端据此读取并销毁验证码。
  private String captchaId;
  @Schema(title = "验证码图片 Base64")
  // 直接可展示的 Base64 图片数据，前端无需再单独请求图片流。
  private String imageBase64;
}
