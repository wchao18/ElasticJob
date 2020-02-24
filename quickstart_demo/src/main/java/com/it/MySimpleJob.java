package com.it;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangchao
 * @description TODO
 * @date 2020/02/17 20:58
 */
@Slf4j
public class MySimpleJob implements SimpleJob, Serializable {

    private static final long serialVersionUID = 2763345942462814504L;

    @Override
    public void execute(ShardingContext shardingContext) {
        int shardingItem = shardingContext.getShardingItem();
        String shardingParameter = shardingContext.getShardingParameter();
        log.info("分片参数：{},分片项：{}", shardingParameter,shardingItem);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(String.format(simpleDateFormat.format(new Date()) + "作业分片：%d", shardingItem));
    }
}
