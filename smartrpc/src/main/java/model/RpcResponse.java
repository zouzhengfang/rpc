package model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 响应类
 */
@Setter
@Getter
@Builder
public class RpcResponse {
    private Status status;

    private Object returnValue;

    private Exception exception;
}
