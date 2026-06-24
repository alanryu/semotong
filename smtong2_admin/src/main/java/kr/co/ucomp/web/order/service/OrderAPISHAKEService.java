package kr.co.ucomp.web.order.service;

import java.util.Map;


public interface OrderAPISHAKEService {
	
	Map<String,Object> sendOrder(int orderId);
	
}
