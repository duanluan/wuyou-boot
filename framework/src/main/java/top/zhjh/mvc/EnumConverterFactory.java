package top.zhjh.mvc;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;
import top.csaf.lang.StrUtil;
import top.zhjh.exception.ServiceException;

import java.lang.reflect.Field;

/**
 * 通用的枚举转换器，根据 @EnumValue 注解进行转换
 */
@Slf4j
@Component
public class EnumConverterFactory implements ConverterFactory<String, Enum<?>> {

  @NotNull
  @Override
  public <T extends Enum<?>> Converter<String, T> getConverter(@NotNull Class<T> targetType) {
    return new StringToEnumConverter<>(targetType);
  }

  private static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {

    private final Class<T> enumType;

    public StringToEnumConverter(Class<T> enumType) {
      this.enumType = enumType;
    }

    @Override
    public T convert(@NotNull String source) {
      if (StrUtil.isBlank(source)) {
        return null;
      }

      try {
        // 遍历枚举的所有字段
        for (T enumConstant : enumType.getEnumConstants()) {
          for (Field field : enumType.getDeclaredFields()) {
            // 找到带有@EnumValue注解的字段
            if (!field.isAnnotationPresent(EnumValue.class)) {
              continue;
            }
            field.setAccessible(true);
            // 判断传入的字符串是否与该字段的值相等
            if (!source.equals(String.valueOf(field.get(enumConstant)))) {
              continue;
            }
            return enumConstant;
          }
        }
      } catch (IllegalAccessException e) {
        log.error("入参枚举转换异常：", e);
        throw new ServiceException("参数异常：" + enumType.getName() + "：" + source);
      }
      throw new ServiceException("参数异常：" + enumType.getName() + "：" + source);
    }
  }
}
