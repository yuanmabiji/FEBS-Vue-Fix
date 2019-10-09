package cc.mrbird.febs.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据范围过滤注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataFilter {
    String filterType() default "field";// field, join
    String filterFieldId() default "creator";
    String[] filterMethods() default {};
    String joinSql() default "";
}
