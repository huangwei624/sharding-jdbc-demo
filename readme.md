# Sharding-jdbc

## 基于java代码实现分库分表

### sharding-jdbc数据源配置
```java
// 数据源相关配置信息
@Configuration
// @ConfigurationProperties(prefix = "spring.jdbc.db0")     // 这些属性需要生成geter seter 方法
public class DataSourceConfig {
	@Value("${spring.jdbc.db0.className}")
	private String className;
	@Value("${spring.jdbc.db0.url}")
	private String url;
	@Value("${spring.jdbc.db0.username}")
	private String username;
	@Value("${spring.jdbc.db0.password}")
	private String password;

	@Bean
	public IdGenerator getIdGenerator() {
		return new CommonSelfIdGenerator();
	}

	@Bean
	public DataSource getDataSource() {
		return buildDataSource();
	}

	// 分库分表数据源
	private DataSource buildDataSource() {
		// 设置分表映射
		Map<String, DataSource> dataSourceMap = new HashMap<>(2);
		dataSourceMap.put("db_0", createDataSource("db_0"));
		//dataSourceMap.put("ds_1", createDataSource("ds_1"));
		
		// 设置默认db为ds_0，也就是为那些没有配置分库分表策略的指定的默认库
		// 如果只有一个库，也就是不需要分库的话，map里只放一个映射就行了，只有一个库时不需要指定默认库，
		// 但2个及以上时必须指定默认库，否则那些没有配置策略的表将无法操作数据
		DataSourceRule rule = new DataSourceRule(dataSourceMap, "db_0");
		
		// 2.设置分表映射，将t_order_0和t_order_1两个实际的表映射到t_order逻辑表
		// TableRule orderTableRule = TableRule.builder("t_order").dataSourceRule(rule).build();
		TableRule orderTableRule = TableRule.builder("t_order")
				                           .actualTables(Arrays.asList("t_order_0", "t_order_1"))
				                           .dataSourceRule(rule).build();
		// 3.具体的分库分表策略
		ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(rule)
				.tableRules(Arrays.asList(orderTableRule))
				// .databaseShardingStrategy(new DatabaseShardingStrategy("user_id", new DatabaseShardingAlgorithm()))
				.tableShardingStrategy(new TableShardingStrategy("user_id", new TableShardingAlgorithm()))
				.build();
		// 创建数据源
		return ShardingDataSourceFactory.createDataSource(shardingRule);
	}

	// 基础数据源
	private DataSource createDataSource(String dataSourceName) {
		// 使用druid连接数据库
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(className);
		druidDataSource.setUrl(String.format(url, dataSourceName));
		druidDataSource.setUsername(username);
		druidDataSource.setPassword(password);
		return druidDataSource;
	}
	
}

```

### 分库分表策略
```java
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

```

## 基于Java配置实现分库分表

```yaml
## 基于配置的sharding-jdbc
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: none
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect

sharding:
  jdbc:
    datasource:
      names: db1
      db1:
        username: root
        password: 123456
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://127.0.0.1:3306/db_1
        type: com.alibaba.druid.pool.DruidDataSource
    config:
      sharding:
        tables:
          ### 需要分片的表名称
          t_order:
            table-strategy:
              inline:
                #### 根据userid 进行分片
                sharding-column: user_id
                ### 分片算法表达式, 真实数据库+表名
                algorithm-expression: db_1.t_order_$->{user_id % 2}
            ### 真实数据节点，由数据源名 + 表名组成
            actual-data-nodes: db1.t_order_$->{0..1}
        props:
          sql:
            ### 开启分片日志
            show: true
```
