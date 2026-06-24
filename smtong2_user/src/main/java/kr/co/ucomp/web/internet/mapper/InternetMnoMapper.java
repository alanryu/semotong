package kr.co.ucomp.web.internet.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.internet.entity.InternetMnoEntity;

@Mapper
public interface InternetMnoMapper {
	
	List<InternetMnoEntity> list();
	
	Long count();
	
	List<InternetMnoEntity> listNew();
	
	Long countNew();
}
