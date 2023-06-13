package core;

import core.server.ServerHandler;
import lombok.Data;
import core.client.ClientBoot;
import core.server.ProtocolProperties;
import core.server.ServerBoot;
import registry.DiscovererService;
import registry.RegistryProperties;
import registry.ServiceRegister;
import serialize.MessageCodec;
import serialize.MessageDecoder;
import serialize.MessageEncoder;
import spi.ExtensionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * spring boot自动装配
 */
@Configuration
@EnableConfigurationProperties({ProtocolProperties.class, RegistryProperties.class})
public class AutoConfiguration {

    @Bean
    public BeanFactoryUtils BeanFactoryUtils() {
        return new BeanFactoryUtils();
    }

    private ProtocolProperties protocolProperties;
    private RegistryProperties registryProperties;

    public AutoConfiguration(ProtocolProperties protocolProperties, RegistryProperties registryProperties) {
        this.protocolProperties = protocolProperties;
        this.registryProperties = registryProperties;
    }

    @Bean
    public RegistryProperties registryProperties() {
        RegistryProperties registryProperties = new RegistryProperties();
        return this.registryProperties;
    }

    @Bean
    public ProtocolProperties protocolProperties() {
        ProtocolProperties protocolProperties = new ProtocolProperties();
        return this.protocolProperties;
    }

    @Bean
    public ServerBoot serverBoot() {
        return new ServerBoot();
    }

    @Bean
    public ClientBoot clientBoot() {
        return new ClientBoot();
    }

    @Bean
    public RpcIntialization rpcIntialization() {
        return new RpcIntialization();
    }
}
