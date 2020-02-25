package com.it.autoconfig;

import com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangchao
 * @description 注解里面只能使用基本类型+String
 * @date 2020/02/22 22:17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticSimpleJob {

    String jobName() default "";

    String corn() default "";

    int shardingTotalCount() default 1;

    boolean overwrite() default false;

    Class<?> jobStrategy() default AverageAllocationJobShardingStrategy.class;

}
