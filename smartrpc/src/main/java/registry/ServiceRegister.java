package registry;

import spi.SPI;

import java.util.List;
@SPI
public interface ServiceRegister {
    void register(List<ServiceMetaData> smdList) throws Exception;

    ServiceMetaData getServiceMetaData(String name) throws Exception;
}
