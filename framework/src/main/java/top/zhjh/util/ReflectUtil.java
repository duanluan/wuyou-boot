package top.zhjh.util;

import java.lang.reflect.Field;

/**
 * 反射工具类 TODO 等待移植到 ZUtil 中
 */
public class ReflectUtil {

  /**
   * 获取类的属性
   *
   * @param clazz               类
   * @param isIncludeSuperClass 是否包括父类的属性
   * @return 属性数组
   */
  public static Field[] getAllFields(Class<?> clazz, boolean isIncludeSuperClass) {
    Field[] fields = clazz.getDeclaredFields();
    if (isIncludeSuperClass) {
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null) {
        Field[] superFields = getAllFields(superClass, true);
        if (superFields != null) {
          Field[] newFields = new Field[fields.length + superFields.length];
          System.arraycopy(fields, 0, newFields, 0, fields.length);
          System.arraycopy(superFields, 0, newFields, fields.length, superFields.length);
          fields = newFields;
        }
      }
    }
    return fields;
  }

  /**
   * 获取类的属性，包括父类的属性
   *
   * @param clazz 类
   * @return 属性数组
   */
  public static Field[] getAllFields(Class<?> clazz) {
    return getAllFields(clazz, true);
  }

  /**
   * 获取类的属性，包括父类的属性
   *
   * @param clazz               类
   * @param fieldName           属性名
   * @param isIncludeSuperClass 是否包括父类的属性
   * @return 属性
   */
  public static Field getField(Class<?> clazz, String fieldName, boolean isIncludeSuperClass) {
    Field[] fields = getAllFields(clazz, isIncludeSuperClass);
    for (Field field : fields) {
      if (field.getName().equals(fieldName)) {
        return field;
      }
    }
    return null;
  }

  /**
   * 获取类的属性，包括父类的属性
   *
   * @param clazz     类
   * @param fieldName 属性名
   * @return 属性
   */
  public static Field getField(Class<?> clazz, String fieldName) {
    return getField(clazz, fieldName, true);
  }
}
