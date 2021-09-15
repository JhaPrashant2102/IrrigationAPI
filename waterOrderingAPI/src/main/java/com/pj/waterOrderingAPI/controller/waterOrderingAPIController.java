package com.pj.waterOrderingAPI.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pj.waterOrderingAPI.models.OrderStatus;
import com.pj.waterOrderingAPI.models.WaterOrders;
import com.pj.waterOrderingAPI.services.WaterOrderingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/waterOrderingService")
public class waterOrderingAPIController {
	
	@Autowired
	WaterOrderingService  waterOrderingService;
	
	//Accept new orders
	@PostMapping("/acceptOrders")
	public ResponseEntity<OrderStatus> acceptNewOrders(@RequestBody WaterOrders waterOrders){
		OrderStatus orderStatus = null;
		try {
			log.info("Received request to create a new order# {}",waterOrders.toString());
			orderStatus = waterOrderingService.acceptNewOrders(waterOrders);
			if(orderStatus==null) {
				log.info("Completed acceptNewOrders request and response is null");
			}else {
				log.info("Completed acceptNewOrders request and response is# {}",orderStatus.toString());
			}
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while creating an order due to exception# {}",exception.getMessage());
		}
		
		return new ResponseEntity<>(orderStatus, HttpStatus.OK);
	}
	
	
	//Cancels an order
	@PostMapping("/cancelOrders")
	public ResponseEntity<OrderStatus> cancelOrder(@RequestParam int orderId){
		
		OrderStatus orderStatus = null;
		try {
			log.info("Received request to cancel an order with orderId# {}",orderId);
			orderStatus = waterOrderingService.cancelOrder(orderId);
			if(orderStatus==null) {
				log.info("Completed cancelOrder request and response is null");
			}else {
				log.info("Completed cancelOrder request and response is# {}",orderStatus.toString());
			}
			
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while cancelling an order due to exception# {}",exception.getMessage());
		}
		
		return new ResponseEntity<>(orderStatus, HttpStatus.OK);
	}
	
	//returns the status of the order
	@GetMapping("/orderStatus")
	public ResponseEntity<OrderStatus> findStatusOfOrder(@RequestParam int orderId){
		
		OrderStatus orderStatus = null;
		
		try {
			log.info("Received request to findStatusOfOrder of an order with orderId# {}",orderId);
			orderStatus = waterOrderingService.findStatusOfOrder(orderId);
			if(orderStatus==null) {
				log.info("Completed findStatusOfOrder request and response is null");
			}else {
				log.info("Completed findStatusOfOrder request and response is# {}",orderStatus.toString());
			}
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while finding status of an order due to exception# {}",exception.getMessage());
		}
		
		return new ResponseEntity<>(orderStatus, HttpStatus.OK);
	}
	
	//returns the status of the order
	@GetMapping("/allOrderStatus")
	public ResponseEntity<Object> findStatusOfAllOrder(){
		List<OrderStatus> orderStatusList = null;
		try {
			log.info("Received request to findStatusOfAllOrder");
			orderStatusList = waterOrderingService.findStatusOfAllOrder();
			if(orderStatusList==null) {
				log.info("Completed findStatusOfAllOrder request and response is null");
			}else {
				log.info("Completed findStatusOfAllOrder request and response is# {}",orderStatusList.toString());
			}
		}catch(Exception exception) {
			exception.printStackTrace();
			log.error("Error occurred while fetching findStatusOfAllOrder due to exception# {}",exception.getMessage());
		}
		return new ResponseEntity<>(orderStatusList, HttpStatus.OK);
	}	
	
}
