package kr.co.ucomp.web.order.service;

import java.util.Map;


public interface OrderAPIGOGOService {
	
	Map<String,Object> sendOrder(int orderId);
	
}
