package annotation;
import java.lang.annotation.*;


/**
 * 服务消费者注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Reference {
    String value() default "";
    String version() default "";
}