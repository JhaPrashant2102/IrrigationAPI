package com.pj.waterOrderingAPI.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pj.waterOrderingAPI.models.OrderStatus;
import com.pj.waterOrderingAPI.models.WaterOrders;

import lombok.Data;


/**
 * Since persistent storage is not required,
 * I am just naming this as my repository
 * therefore not mentioning @Repository annotation
 * 
 * This Class will store waterOrders and their status
 */
@Component
public @Data class WaterOrderingRepository {
	
	private List<WaterOrders> waterOrderList;
	
	private List<OrderStatus> orderStatusList;

	public WaterOrderingRepository(List<WaterOrders> waterOrderList, List<OrderStatus> orderStatusList) {
		this.waterOrderList = waterOrderList;
		this.orderStatusList = orderStatusList;
	}
	
	public WaterOrderingRepository() {
		this.waterOrderList = new ArrayList<>();
		this.orderStatusList = new ArrayList<>();
	}
	
}
