package com.pj.waterOrderingAPI.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

public @Data class OrderStatus {
	
	private WaterOrders waterOrders;
	
	private String statusMessage;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime promisedEndDateAndTime;

	

}
