package com.pj.waterOrderingAPI.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pj.waterOrderingAPI.models.OrderStatus;
import com.pj.waterOrderingAPI.models.WaterOrders;

@Service
public interface WaterOrderingService {
	
	public OrderStatus acceptNewOrders(WaterOrders waterOrders);

	public OrderStatus cancelOrder(int orderId);

	public OrderStatus findStatusOfOrder(int orderId);

	public List<OrderStatus> findStatusOfAllOrder();

}
