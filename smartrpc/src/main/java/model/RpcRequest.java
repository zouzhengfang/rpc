package model;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求类
 */
@Setter
@Getter
public class RpcRequest {
    /**
     * 接口名称
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法参数类型列表
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数列表
     */
    private Object[] parameters;
}
