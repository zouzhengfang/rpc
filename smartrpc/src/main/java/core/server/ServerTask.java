package core.server;

import model.RpcRequest;
import model.RpcResponse;
import model.Status;
import registry.ServiceMetaData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ServerTask implements Callable<Boolean> {

    protected RpcRequest request;
    protected RpcResponse response;
    protected ServiceMetaData serviceMetaData;

    public ServerTask(RpcRequest request, RpcResponse response, ServiceMetaData serviceMetaData) {
        this.request = request;
        this.response = response;
        this.serviceMetaData = serviceMetaData;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            Method method = serviceMetaData.getInterfaceclazz().getMethod(request.getMethodName(), request.getParameterTypes());
            Object returnVal = method.invoke(serviceMetaData.getServiceObj(), request.getParameters());
            boolean isInvokeSucc = returnVal != null;
            if (isInvokeSucc) {
                response = RpcResponse.builder()
                        .status(Status.SUCCESS)
                        .returnValue(returnVal)
                        .build();
            } else {
                response = RpcResponse.builder()
                        .status(Status.ERROR)
                        .build();
            }
            return Boolean.TRUE;


        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException t) {
            response = RpcResponse.builder()
                    .status(Status.ERROR)
                    .exception(t)
                    .build();
            return Boolean.FALSE;
        }
    }
}
