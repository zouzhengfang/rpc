package annotation;
import org.springframework.stereotype.Service;
import java.lang.annotation.*;

/**
 * 服务提供者注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {
    String value() default "";
    String version() default "";
}
