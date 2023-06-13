package registry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RpcTransportData {
    /**
     * 服务名称
     */
    private String name;
    /**
     * 服务协议
     */
    private String protocol;
    /**
     * 服务地址
     */
    private String address;
}
