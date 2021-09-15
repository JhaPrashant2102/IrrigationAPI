package com.pj.waterOrderingAPI.commons;

public class WaterOrderingConstant {

	public static final String ORDER_PLACED_MESSAGE = "Order has been successfully placed";
	
	public static final String REQUESTED_MESSAGE = "Order has been placed but not yet delivered.";
	
	public static final String INPROGRESS_MESSAGE = "Order is being delivered right now.";
	
	public static final String DELIVERED_MESSAGE = "Order has been delivered.";
	
	public static final String CANCELLED_MESSAGE = "Order was cancelled before delivery.";
	
	public static final String ORDER_CANNOT_BE_PLACED_MESSAGE = "Order cannot be placed since we already have an order in these slots";

	public static final String NO_ORDER_FOUND_MESSAGE = "Cannot find order with this orderId";

	public static final String INVALID_ORDER_START_DATE = "Water order cannot be placed at past date and time";

}
