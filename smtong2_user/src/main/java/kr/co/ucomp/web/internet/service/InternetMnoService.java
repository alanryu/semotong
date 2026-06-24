package kr.co.ucomp.web.internet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.internet.entity.InternetMnoEntity;

@Service
public interface InternetMnoService {

	public List<InternetMnoEntity> list();
	
	public Long count();
	
	public List<InternetMnoEntity> listNew();
	
	public Long countNew();	
	
}
