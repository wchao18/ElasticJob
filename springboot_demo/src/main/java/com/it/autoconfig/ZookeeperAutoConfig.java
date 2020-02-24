package com.it.autoconfig;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangchao
 * @description TODO
 * @date 2020/02/22 21:53
 */
@Configuration
@ConditionalOnProperty("elasticjob.zookeeper.server-list")//有值执行下面的配置
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfig {

    @Autowired
    private ZookeeperProperties zookeeperProperties;


    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter registryCenter() {
        String serverList = zookeeperProperties.getServerList();
        String namespace = zookeeperProperties.getNamespace();
        //zk配置
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(serverList, namespace);
        //配置超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(1000);
        //创建注册中心
        CoordinatorRegistryCenter coordinatorRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        return coordinatorRegistryCenter;
    }
}
