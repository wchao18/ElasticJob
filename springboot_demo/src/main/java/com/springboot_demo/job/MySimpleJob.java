package com.springboot_demo.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.it.autoconfig.ElasticSimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangchao
 * @description 简单任务类
 * @date 2020/02/17 20:58
 */
@Slf4j
@ElasticSimpleJob(
        jobName = "simpleJob",
        corn = "0/10 * * * * ?",
        shardingTotalCount = 2,
        overwrite = true,
        jobEvent = true
)
@Component//加@Configuration注解在读取注解的时候会有问题
public class MySimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        int shardingItem = shardingContext.getShardingItem();
        String shardingParameter = shardingContext.getShardingParameter();
        log.info("分片参数：{},分片项：{}", shardingParameter, shardingItem);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(String.format(simpleDateFormat.format(new Date()) + "作业分片：%d", shardingItem));
    }
}
