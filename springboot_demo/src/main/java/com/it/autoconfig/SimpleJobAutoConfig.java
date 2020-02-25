package com.it.autoconfig;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.springboot_demo.job.MySimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author wangchao
 * @description TODO
 * @date 2020/02/22 22:25
 */
@Configuration
@ConditionalOnBean(CoordinatorRegistryCenter.class)
@AutoConfigureAfter(ZookeeperAutoConfig.class)
public class SimpleJobAutoConfig {


    @Autowired
    private CoordinatorRegistryCenter registryCenter;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;


    //@PostConstruct
    public void initSimpleJob() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ElasticSimpleJob.class);

        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            Object instance = entry.getValue();
            Class<?>[] interfaces = instance.getClass().getInterfaces();
            for (Class<?> superInterface : interfaces) {
                if (superInterface == SimpleJob.class) {
                    ElasticSimpleJob elasticSimpleJob = instance.getClass().getAnnotation(ElasticSimpleJob.class);
                    String corn = elasticSimpleJob.corn();
                    String jobName = elasticSimpleJob.jobName();
                    boolean overwrite = elasticSimpleJob.overwrite();
                    int shardingTotalCount = elasticSimpleJob.shardingTotalCount();
                    Class<?> jobStrategy = elasticSimpleJob.jobStrategy();
                    boolean jobEvent = elasticSimpleJob.jobEvent();
                    //job核心配置
                    JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobName, corn, shardingTotalCount).build();
                    //job类型配置
                    SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, MySimpleJob.class.getCanonicalName());
                    //job根配置,overwrite为覆盖以前的配置
                    LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).jobShardingStrategyClass(jobStrategy.getCanonicalName()).overwrite(overwrite).build();
                    //new JobScheduler(registryCenter, liteJobConfiguration).init();
                    //修改启动方式
                    if (jobEvent) {
                        JobEventConfiguration jec = new JobEventRdbConfiguration(dataSource);
                        new SpringJobScheduler((ElasticJob) instance, registryCenter, liteJobConfiguration, jec).init();
                    } else {
                        new SpringJobScheduler((ElasticJob) instance, registryCenter, liteJobConfiguration).init();
                    }
                }
            }
        }


    }


}
