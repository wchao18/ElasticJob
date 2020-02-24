package com.it.autoconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangchao
 * @description 注册中心配置
 * @date 2020/02/22 21:52
 */
@ConfigurationProperties(prefix = "elasticjob.zookeeper")
@Getter
@Setter
public class ZookeeperProperties {
    //zookeeper地址列表
    private String serverList;
    //zookeeper命名空间
    private String namespace;
}
