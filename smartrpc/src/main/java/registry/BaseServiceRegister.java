package registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseServiceRegister implements ServiceRegister {
    private Map<String, ServiceMetaData> serviceCache = new HashMap<>();

    @Override
    public void register(List<ServiceMetaData> smdList) throws Exception {
        smdList.forEach(so -> this.serviceCache.put(so.getInterfacename(), so));
    }

    @Override
    public ServiceMetaData getServiceMetaData(String name) throws Exception {
        return this.serviceCache.get(name);
    }
}
