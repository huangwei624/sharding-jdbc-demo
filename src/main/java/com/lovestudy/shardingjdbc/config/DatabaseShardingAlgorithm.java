package com.lovestudy.shardingjdbc.config;

import java.util.Collection;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;

public class DatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<Long> {

	@Override
	public String doEqualSharding(Collection<String> databases, ShardingValue<Long> shardingValue) {
		for (String tableName : databases) {
			System.out.println("tableName:" + tableName + ",----" + shardingValue.getValue());
			if (tableName.endsWith(shardingValue.getValue() % 2 + "")) {
				return tableName;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<Long> shardingValue) {

		return null;
	}

	@Override
	public Collection<String> doBetweenSharding(Collection<String> availableTargetNames,
			ShardingValue<Long> shardingValue) {

		return null;
	}

}
