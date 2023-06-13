package registry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 服务注册的元信息
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMetaData {
    /**
     * 服务接口名称
     */
    private String interfacename;
    /**
     * 服务接口Class
     */
    private Class<?> interfaceclazz;
    /**
     * 具体服务
     */
    private Object serviceObj;
}
