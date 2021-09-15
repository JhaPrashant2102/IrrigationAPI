package com.pj.waterOrderingAPI.models;


import java.time.LocalDateTime;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.waterOrderingAPI.commons.WaterOrderingConstant;

import lombok.Data;

public @Data class WaterOrders {
	
	//an uniqueId to identify order
	private int orderId;
	
	//an uniqueId to identify farm
	@NotNull
	@NotBlank
	@NotEmpty
	private String farmId;
	
	//the date and time when water should be delivered
	@NotNull
	@FutureOrPresent(message = WaterOrderingConstant.INVALID_ORDER_START_DATE)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startDateAndTime;
	
	//duration for which water should flow
	//We have assumed duration in hours only
	@NotNull
	@NotBlank
	@NotEmpty
	private int duration;
	
}
