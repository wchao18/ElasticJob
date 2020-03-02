package com.springboot_demo.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangchao
 * @description 分布式定时器(有问题，不推荐使用)
 * @date 2020/02/25 15:09
 */
@Slf4j
public class MyDistributeListener extends AbstractDistributeOnceElasticJobListener {

    public MyDistributeListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        log.info("最后一个任务：{}开始前执行",shardingContexts.getJobName());
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        log.info("最后一个任务：{}完成后执行",shardingContexts.getJobName());
    }
}
