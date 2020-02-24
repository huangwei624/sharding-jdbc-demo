package com.lovestudy.shardingjdbc.controller;

import com.lovestudy.shardingjdbc.entity.OrderEntity;
import com.lovestudy.shardingjdbc.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController {
	@Autowired
	private OrderRepository orderRepository;

	// 查询所有的订单信息
	@RequestMapping("/getOrderAll")
	public List<OrderEntity> getOrderAll() {
		return (List<OrderEntity>) orderRepository.findAll();
	}

	// 使用in条件查询
	@RequestMapping("/inOrder")
	public List<OrderEntity> inOrder() {
		List<String> ids = new ArrayList<>();
		ids.add("2");
		ids.add("3");
		ids.add("4");
		ids.add("5");
		return orderRepository.findExpiredOrderState(ids);

	}

	// 增加
	@RequestMapping("/insertOrder")
	public String insertOrder(OrderEntity orderEntity) {
		for (int i = 10; i < 20; i++) {
			OrderEntity order = new OrderEntity();
			order.setOrderId((long) i);
			order.setUserId((long) i);
			orderRepository.save(order);
		}
		return "success";
	}
	
	@GetMapping("findOrderEntitiesByOrderIdBetween")
	public List<OrderEntity> findOrderEntitiesByOrderIdBetween(){
		return orderRepository.findOrderEntitiesByOrderIdBetween(2, 6);
	}

}
