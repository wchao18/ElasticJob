package com.it;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * @author wangchao
 * @description TODO
 * @date 2020/02/17 21:05
 */
public class JobMain {

    private static final String ZK_ADDRESS = "192.168.222.129:2181";

    private static final String JOB_NAMESPACE = "elastic‐job‐example‐java";

    public static void main(String[] args) {
        //启动简单任务
        //new JobScheduler(registryCenter(), simpleJobConfiguration()).init();
        new JobScheduler(registryCenter(),dataflowJobConfiguration()).init();
    }


    private static CoordinatorRegistryCenter registryCenter() {
        //zk配置
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ZK_ADDRESS, JOB_NAMESPACE);
        //配置超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(1000);
        //创建注册中心
        CoordinatorRegistryCenter coordinatorRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        //初始化
        coordinatorRegistryCenter.init();
        return coordinatorRegistryCenter;
    }

    //简单任务配置
    private static LiteJobConfiguration simpleJobConfiguration() {
        //job核心配置
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder("simpleJob", "0/3 * * * * ?", 3).build();
        //job类型配置
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, MySimpleJob.class.getCanonicalName());
        //job根配置,overwrite为覆盖以前的配置
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }


    //流式任务配置
    private static LiteJobConfiguration dataflowJobConfiguration() {
        //job核心配置
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder("dataflowJob", "0/10 * * * * ?", 2).build();
        //job类型配置
        DataflowJobConfiguration dataflowJobConfiguration = new DataflowJobConfiguration(jobCoreConfiguration, MyDataflowJob.class.getCanonicalName(), true);
        //job根配置,overwrite为覆盖以前的配置
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(dataflowJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }


    //脚本任务配置
    private static LiteJobConfiguration scriptJobConfiguration() {
        //job核心配置
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder("scriptJob", "0/10 * * * * ?", 2).build();
        //job类型配置
        ScriptJobConfiguration scriptJobConfiguration = new ScriptJobConfiguration(jobCoreConfiguration,"" );
        //job根配置,overwrite为覆盖以前的配置
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(scriptJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }
}
