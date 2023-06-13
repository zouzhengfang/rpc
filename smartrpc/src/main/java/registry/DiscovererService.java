package registry;

import spi.SPI;

import java.util.List;
@SPI
public interface DiscovererService {
    List<RpcTransportData> getService(String name);
}
