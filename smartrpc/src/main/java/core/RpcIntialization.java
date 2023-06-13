package core;

import com.google.common.io.CharStreams;
import core.server.ServerHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import annotation.Reference;
import annotation.RpcService;
import annotation.RpcService;
import core.client.ClientBoot;
import core.server.ServerBoot;
import registry.RegistryProperties;
import registry.ServiceMetaData;
import registry.ServiceRegister;
import spi.ExtensionLoader;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Spring容器启动或者刷新执执行
 */
public class RpcIntialization implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private RegistryProperties registryProperties = BeanFactoryUtils.getBean("registryProperties");
    @Autowired
    private ServerBoot serverBoot;

    private ServiceRegister serviceRegister;

    @Autowired
    private ClientBoot clientBoot;

    public RpcIntialization() {
        serviceRegister = ExtensionLoader.getExtensionLoader(ServiceRegister.class).getExtension(registryProperties.getRegistry());
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (Objects.isNull(applicationContext.getParent())) {
            printLogo();
            startServer(applicationContext);
            proxyService(applicationContext);
        }
    }

    /*
     * 开启服务
     * */
    private void startServer(ApplicationContext applicationContext) throws Exception {
        //寻找带有服务提供者注解bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (beans.size() != 0) {
            //遍历服务提供者，组装，注册服务
            for (Object o : beans.values()) {
                List<ServiceMetaData> soList = new ArrayList<>();
                Class<?> clazz = o.getClass();
                RpcService service = clazz.getAnnotation(RpcService.class);
                String version = service.version();
                Class<?>[] interfaces = clazz.getInterfaces();
                //支持多版本服务
                if (interfaces.length > 1) {
                    for (Class<?> aClass : interfaces) {
                        String aClassName = aClass.getSimpleName();
                        if (StringUtils.hasLength(version)) {
                            aClassName += ":" + version;
                        }
                        soList.add(new ServiceMetaData(aClassName, aClass, o));
                    }
                } else {
                    Class<?> superClass = interfaces[0];
                    String aClassName = superClass.getSimpleName();
                    if (StringUtils.hasLength(version)) {
                        aClassName += ":" + version;
                    }
                    soList.add(new ServiceMetaData(aClassName, superClass, o));
                }
                //服务注册
                this.serviceRegister.register(soList);
            }
            //启动服务
            serverBoot.start();
        }
    }

    /*
     * 服务代理
     * */
    public void proxyService(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> clazz = applicationContext.getType(beanName);
            if (Objects.isNull(clazz)) {
                continue;
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Reference reference = field.getAnnotation(Reference.class);
                if (Objects.isNull(reference)) {
                    continue;
                }
                Class<?> fieldClass = field.getType();
                Object bean = applicationContext.getBean(beanName);
                field.setAccessible(true);
                String version = reference.version();
                try {
                    field.set(bean, clientBoot.getProxy(fieldClass, version));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        serverBoot.stop();
    }

    private void printLogo() {
        Resource resource = new ClassPathResource("logo.txt");
        if (resource.exists()) {
            try {
                Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
                String text = CharStreams.toString(reader);
                System.out.println(text);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
