package com.it.autoconfig;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.springboot_demo.job.MyDataflowJob;
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
 * @description 任务流配置
 * @date 2020/02/22 22:25
 */
@Configuration
@ConditionalOnBean(CoordinatorRegistryCenter.class)
@AutoConfigureAfter(ZookeeperAutoConfig.class)
public class DataflowJobAutoConfig {


    @Autowired
    private CoordinatorRegistryCenter registryCenter;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initDataflowJob() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ElastciDataflowJob.class);

        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            Object instance = entry.getValue();
            Class<?>[] interfaces = instance.getClass().getInterfaces();
            for (Class<?> superInterface : interfaces) {
                if (superInterface == DataflowJob.class) {
                    ElastciDataflowJob elastciDataflowJob = instance.getClass().getAnnotation(ElastciDataflowJob.class);
                    String corn = elastciDataflowJob.corn();
                    String jobName = elastciDataflowJob.jobName();
                    boolean overwrite = elastciDataflowJob.overwrite();
                    boolean streamingProcess = elastciDataflowJob.streamingProcess();
                    int shardingTotalCount = elastciDataflowJob.shardingTotalCount();
                    Class<?> jobStrategy = elastciDataflowJob.jobStrategy();
                    boolean jobEvent = elastciDataflowJob.jobEvent();
                    //job核心配置
                    JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobName, corn, shardingTotalCount).build();
                    //job类型配置
                    DataflowJobConfiguration dataflowJobConfiguration = new DataflowJobConfiguration(jobCoreConfiguration, MyDataflowJob.class.getCanonicalName(), streamingProcess);
                    //job根配置,overwrite为覆盖以前的配置
                    LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(dataflowJobConfiguration).jobShardingStrategyClass(jobStrategy.getCanonicalName()).overwrite(overwrite).build();
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
