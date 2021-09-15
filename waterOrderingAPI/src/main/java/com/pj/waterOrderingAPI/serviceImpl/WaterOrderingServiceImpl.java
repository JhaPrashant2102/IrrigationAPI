package com.pj.waterOrderingAPI.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.waterOrderingAPI.commons.WaterOrderingConstant;
import com.pj.waterOrderingAPI.models.OrderStatus;
import com.pj.waterOrderingAPI.models.WaterOrders;
import com.pj.waterOrderingAPI.repository.WaterOrderingRepository;
import com.pj.waterOrderingAPI.services.WaterOrderingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WaterOrderingServiceImpl implements WaterOrderingService{
	
	@Autowired
	WaterOrderingRepository waterOrderingRepository;

	@Override
	public OrderStatus acceptNewOrders(WaterOrders waterOrder) {
		
		OrderStatus returningStatus = new OrderStatus();
		List<WaterOrders> waterOrderList = null;
		List<OrderStatus> orderStatusList = null;
		boolean checkForSameSlotOrderflag = true;
		
		
		try {
			
			//First check --> check if order date & time is greater than current date and time
			LocalDateTime currentDateAndTime = LocalDateTime.now();
			
			if(waterOrder.getStartDateAndTime().isBefore(currentDateAndTime)) {
				returningStatus.setStatusMessage(WaterOrderingConstant.INVALID_ORDER_START_DATE);
				return returningStatus;
			}
			
			
			waterOrderList = waterOrderingRepository.getWaterOrderList();
			orderStatusList = waterOrderingRepository.getOrderStatusList();
			int waterOrderListSize = waterOrderList.size();
			
			//Before moving ahead with our current orders let's take an update of our existing orders
			updateOrders(orderStatusList);
			
			
			if(waterOrder!=null) {
				
				
				LocalDateTime requestedstartDateAndTime = waterOrder.getStartDateAndTime();
				
				
				//we also need endDateAndTime
				//to set endDateAndTime I have added hours to startDateAndTime
				//added this property so that my calculations are eased
				LocalDateTime requestedPromisedEndDateAndTime = requestedstartDateAndTime.plusHours(waterOrder.getDuration());
				
				
				List<WaterOrders> searchOrder = waterOrderList.stream().filter(order->waterOrder.getFarmId().equalsIgnoreCase(order.getFarmId())).collect(Collectors.toList());
				List<OrderStatus> searchOrderStatus = orderStatusList.stream().filter(order->waterOrder.getFarmId().equalsIgnoreCase(order.getWaterOrders().getFarmId())).collect(Collectors.toList());
				
				
				if(searchOrder.size()==0) {
					//We can successfully create order since there is no order at this farm
					
					//set orderId
					//orderId is being set to (1 + (Id of last element) in list)
					
					if(waterOrderListSize==0) {
						waterOrder.setOrderId(1);
					}else {
						waterOrder.setOrderId(waterOrderList.get(waterOrderListSize-1).getOrderId()+1);
					}
					
					
					
					
					//adding waterOrder to my storage
					waterOrderList.add(waterOrder);
					waterOrderingRepository.setWaterOrderList(waterOrderList);
					
					
					//mapping waterOrder to promised endDateAndTime and statusMessage 
					//since we are not using persistent storage here, I decided to create POJO and not hasMaps
					returningStatus.setWaterOrders(waterOrder);
					returningStatus.setStatusMessage(WaterOrderingConstant.ORDER_PLACED_MESSAGE);
					returningStatus.setPromisedEndDateAndTime(requestedPromisedEndDateAndTime);
					
					orderStatusList.add(returningStatus);
					
					log.info("Following Water Order has been successfully placed# {}",waterOrder.toString());
					
				}else {
					//LocalDateTime existingOrderStartDateAndTime = null;
					LocalDateTime existingOrderPromisedEndDateAndTime = null;
					for(OrderStatus placedOrder: searchOrderStatus) {
						
						//orders which are in progress for this farmID
						if(placedOrder.getStatusMessage().equalsIgnoreCase(WaterOrderingConstant.REQUESTED_MESSAGE)
								||placedOrder.getStatusMessage().equalsIgnoreCase(WaterOrderingConstant.INPROGRESS_MESSAGE)) {
							
							//existingOrderStartDateAndTime = placedOrder.getWaterOrders().getStartDateAndTime();
							existingOrderPromisedEndDateAndTime = placedOrder.getPromisedEndDateAndTime();
							
							if(requestedstartDateAndTime.isBefore(existingOrderPromisedEndDateAndTime)) {
								//Sorry we cannot place this order since we already have an existing order for this date and time duration
								checkForSameSlotOrderflag=false;
								break;
							}
						}
					}
					
					if(checkForSameSlotOrderflag) {
						//create the order
						waterOrder.setOrderId(waterOrderList.get(waterOrderListSize-1).getOrderId()+1);
						
						//adding waterOrder to my storage
						waterOrderList.add(waterOrder);
						waterOrderingRepository.setWaterOrderList(waterOrderList);
						
						
						//mapping waterOrder to promised endDateAndTime and statusMessage 
						//since we are not using persistent storage here, I decided to create POJO and not hasMaps
						returningStatus.setWaterOrders(waterOrder);
						returningStatus.setStatusMessage(WaterOrderingConstant.ORDER_PLACED_MESSAGE);
						returningStatus.setPromisedEndDateAndTime(requestedPromisedEndDateAndTime);
						
						orderStatusList.add(returningStatus);
						log.info("New water order for farm# {} created",waterOrder.getFarmId());
						log.info("Following Water Order has been successfully placed# {}",waterOrder.toString());
						
					}else {
						
						//we cannot place an order
						returningStatus.setWaterOrders(waterOrder);
						returningStatus.setStatusMessage(WaterOrderingConstant.ORDER_CANNOT_BE_PLACED_MESSAGE);
						log.info("Following Water Order Cannot be placed# {}",waterOrder.toString());
					}
					
				}
			}
			
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while accepting new orders due to exception# {}",exception.getMessage());
		}
		return returningStatus;
	}

	private void updateOrders(List<OrderStatus> orderStatusList) {
		
		log.info("Updating status of exiting orders");
		LocalDateTime currentDateAndTime = LocalDateTime.now();
		for(OrderStatus existingOrder: orderStatusList) {
			
			LocalDateTime existingOrderStartDateAndTime = existingOrder.getWaterOrders().getStartDateAndTime();
			LocalDateTime existingOrderPromisedEndDateAndTime = existingOrder.getPromisedEndDateAndTime();
			
			if(existingOrder.getStatusMessage().equalsIgnoreCase(WaterOrderingConstant.CANCELLED_MESSAGE)) {
				log.info("Following order was cancelled before delivery# {}",existingOrder.toString());
				continue;
			}
			
			if(currentDateAndTime.isBefore(existingOrderStartDateAndTime)) {
				//change the status to Requested
				existingOrder.setStatusMessage(WaterOrderingConstant.REQUESTED_MESSAGE);
				log.info("Water delivery to farm# {} will begin at# {} for hrs# {}"
						,existingOrder.getWaterOrders().getFarmId(),existingOrder.getWaterOrders().getStartDateAndTime()
						,existingOrder.getWaterOrders().getDuration());
				continue;
			}else if(currentDateAndTime.isBefore(existingOrderPromisedEndDateAndTime)) {
				//change the status to Inprogress
				existingOrder.setStatusMessage(WaterOrderingConstant.INPROGRESS_MESSAGE);
				log.info("Water delivery to farm# {} started",existingOrder.getWaterOrders().getFarmId());
				continue;
			}else {
				//Order is Delivered
				existingOrder.setStatusMessage(WaterOrderingConstant.DELIVERED_MESSAGE);
				log.info("Water delivery to farm# {} stopped",existingOrder.getWaterOrders().getFarmId());
				continue;
			}
		}
		
	}

	@Override
	public OrderStatus cancelOrder(int orderId) {
		OrderStatus orderStatus = null;
		List<OrderStatus> orderStatusList = null;
		try {
			updateOrders(waterOrderingRepository.getOrderStatusList());
			orderStatusList = waterOrderingRepository.getOrderStatusList();
			orderStatus= orderStatusList.stream().filter(order->order.getWaterOrders().getOrderId()==orderId).findFirst().orElse(null);
			if(orderStatus==null) {
				log.info("Cannot find order with this orderId");
				orderStatus = new OrderStatus();
				orderStatus.setStatusMessage(WaterOrderingConstant.NO_ORDER_FOUND_MESSAGE);
			}else if(orderStatus.getStatusMessage().equalsIgnoreCase(WaterOrderingConstant.CANCELLED_MESSAGE)) {
				log.info("Following order# {} has already been cancelled",orderStatus.toString());
			}else {
				orderStatus.setStatusMessage(WaterOrderingConstant.CANCELLED_MESSAGE);
				log.info("Following order# {} has been cancelled",orderStatus.toString());
			}
		
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while fetching status of orderID# {} due to exception# {}",orderId,exception.getMessage());
		}
		return orderStatus;
	}

	@Override
	public OrderStatus findStatusOfOrder(int orderId) {
		OrderStatus orderStatus = null;
		List<OrderStatus> orderStatusList = null;
		try {
			updateOrders(waterOrderingRepository.getOrderStatusList());
			orderStatusList = waterOrderingRepository.getOrderStatusList();
			orderStatus= orderStatusList.stream().filter(order->order.getWaterOrders().getOrderId()==orderId).findFirst().orElse(null);
			if(orderStatus==null) {
				log.info("Cannot find order with this orderId");
				orderStatus = new OrderStatus();
				orderStatus.setStatusMessage(WaterOrderingConstant.NO_ORDER_FOUND_MESSAGE);
			}else {
				log.info("Found order# {} with orderId# {}",orderStatus.toString(),orderId);
			}
		
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while fetching status of orderID# {} due to exception# {}",orderId,exception.getMessage());
		}
		return orderStatus;
	}

	@Override
	public List<OrderStatus> findStatusOfAllOrder() {
		
		List<OrderStatus> orderStatusList = null;
		
		try {
			updateOrders(waterOrderingRepository.getOrderStatusList());
			orderStatusList = waterOrderingRepository.getOrderStatusList();
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while fetching status of all orders due to exception# {}",exception.getMessage());
		}
		
		return orderStatusList;
	}

}
