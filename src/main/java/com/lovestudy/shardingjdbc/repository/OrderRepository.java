package com.lovestudy.shardingjdbc.repository;

import com.lovestudy.shardingjdbc.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	@Query(value = "SELECT order_id ,user_id  FROM t_order  where order_id in (?1);", nativeQuery = true)
	public List<OrderEntity> findExpiredOrderState(List<String> bpIds);
	
	public List<OrderEntity> findOrderEntitiesByOrderIdBetweenOrderByOrderId(long id1, long id2);
	
	public OrderEntity findByOrderId(long orderId);
	
	public OrderEntity findByUserId(long userId);
}