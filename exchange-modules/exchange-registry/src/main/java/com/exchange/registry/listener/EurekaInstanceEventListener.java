package com.exchange.registry.listener;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/20
 * Time: 23:12
 * Description: EurekaInstanceEventListener
 */
@EnableScheduling
@Slf4j
@Component
public class EurekaInstanceEventListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        //服务down掉了
        if (applicationEvent instanceof EurekaInstanceCanceledEvent) {
            EurekaInstanceCanceledEvent event = (EurekaInstanceCanceledEvent) applicationEvent;
            PeerAwareInstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();
            Applications applications = registry.getApplications();
            applications.getRegisteredApplications().forEach(registeredApplication -> {
                registeredApplication.getInstances().forEach(instanceInfo -> {
                    if (instanceInfo.getInstanceId().equals(event.getServerId())) {
                        log.error("service " + instanceInfo.getAppName() + "(" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + ") down");
                        //todo notify to someone
                    }
                });
            });
        }

        //服务注册成功
        if (applicationEvent instanceof EurekaInstanceRegisteredEvent) {
            EurekaInstanceRegisteredEvent event = (EurekaInstanceRegisteredEvent) applicationEvent;
            InstanceInfo instanceInfo = event.getInstanceInfo();
            log.info("service " + instanceInfo.getAppName() + "(" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + ") Registered successfully！");
        }

        //服务续约
        if (applicationEvent instanceof EurekaInstanceRenewedEvent) {
            InstanceInfo instanceInfo = ((EurekaInstanceRenewedEvent) applicationEvent).getInstanceInfo();
            log.trace("service " + instanceInfo.getAppName() + "(" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + ") Renewed successfully！");
        }

        //服务可用
        if (applicationEvent instanceof EurekaRegistryAvailableEvent) {
            log.info("service available");
        }

        //启动服务
        if (applicationEvent instanceof EurekaServerStartedEvent) {
            log.info("service started");
        }
    }
}

