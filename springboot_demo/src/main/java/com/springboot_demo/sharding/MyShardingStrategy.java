package com.springboot_demo.sharding;

import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;
import org.springframework.util.CollectionUtils;
import java.util.*;

/**
 * @author wangchao
 * @description 自定义分片策略
 * @date 2020/02/25 11:27
 */
public class MyShardingStrategy implements JobShardingStrategy {
    @Override
    public Map<JobInstance, List<Integer>> sharding(List<JobInstance> jobInstances, String jobName, int shardingTotalCount) {
        Map<JobInstance, List<Integer>> rtnMap = new HashMap<>();
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>(shardingTotalCount);
        for (int i = 0; i < shardingTotalCount; i++) {
            arrayDeque.add(i);
        }
        while (arrayDeque.size() > 0) {
            for (JobInstance jobInstance : jobInstances) {
                Integer shardingItem = arrayDeque.pop();
                List<Integer> list = rtnMap.get(jobInstance);
                if (!CollectionUtils.isEmpty(list)) {
                    list.add(shardingItem);
                } else {
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    arrayList.add(shardingItem);
                    rtnMap.put(jobInstance, arrayList);
                }
            }
        }
        return rtnMap;
    }
}
