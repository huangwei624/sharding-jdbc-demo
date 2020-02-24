package com.lovestudy.shardingjdbc.config;

import java.util.Collection;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;

// 表分片算法
@Slf4j
public class TableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<Long> {
	
	// 根据分片值和SQL的=运算符计算分片结果名称集合.
	@Override
	public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<Long> shardingValue) {
		log.info("---------->>doEqualSharding");
		for (String tableName : availableTargetNames) {
			if (tableName.endsWith(shardingValue.getValue() % 2 + "")) {
				return tableName;
			}
		}
		throw new IllegalArgumentException();
	}

	// 根据分片值和SQL的IN运算符计算分片结果名称集合.
	@Override
	public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<Long> shardingValue) {
		log.info("---------->>doInSharding");
		return null;
	}

	// 根据分片值和SQL的BETWEEN运算符计算分片结果名称集合.
	@Override
	public Collection<String> doBetweenSharding(Collection<String> availableTargetNames,
			ShardingValue<Long> shardingValue) {
		log.info("---------->>doBetweenSharding");
		return null;
	}

}
