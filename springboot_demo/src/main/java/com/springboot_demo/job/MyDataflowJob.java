package com.springboot_demo.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.it.autoconfig.ElastciDataflowJob;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangchao
 * @description dataflow分为数据抓取和数据处理
 * @date 2020/02/22 16:58
 */
@Slf4j
@Component
@ElastciDataflowJob(
        jobName = "myDataflowJob",
        corn = "0/10 * * * * ?",
        shardingTotalCount = 2,
        overwrite = true,
        streamingProcess = true
)
public class MyDataflowJob implements DataflowJob<Order> {

    private List<Order> orderList = new ArrayList<>();

    {
        for (int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setOrderId(Long.valueOf(i + 1));
            order.setStatus(0);
            orderList.add(order);
        }
    }

    @Override
    public List<Order> fetchData(ShardingContext shardingContext) {
        List<Order> list = orderList.stream().filter(o -> o.getStatus() == 0)
                .filter(o -> o.getOrderId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem())
                .collect(Collectors.toList());

        List<Order> subList = null;
        if (list != null && list.size() > 0) {
            subList = list.subList(0, 10);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalDateTime now = LocalDateTime.now();
        log.info(now + ":分片项：{} 抓取数据：{}", shardingContext.getShardingItem(), subList);
        return subList;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Order> list) {
        list.forEach(o -> o.setStatus(1));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalDateTime now = LocalDateTime.now();
        log.info(now + ":分片项：{} 处理数据", shardingContext.getShardingItem());
    }
}


@Data
class Order {

    /**
     *订单ID
     */
    private Long orderId;

    /**
     * 0：未处理 1：已处理
     */
    private Integer status;
}
