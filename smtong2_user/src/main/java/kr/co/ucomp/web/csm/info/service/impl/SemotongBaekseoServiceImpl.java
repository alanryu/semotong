package kr.co.ucomp.web.csm.info.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.info.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.info.entity.SemotongBaekseoEntity;
import kr.co.ucomp.web.csm.info.mapper.SemotongBaekseoMapper;
import kr.co.ucomp.web.csm.info.service.SemotongBaekseoSevice;

import java.util.List;

@Service
public class SemotongBaekseoServiceImpl implements SemotongBaekseoSevice {
	
	@Autowired SemotongBaekseoMapper semotongBaekseoMapper;

	/**
	 * 백서 list 조회
	 * @param : SemotongBaekseoDto
	 * @return List<SemotongBaekseoEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SemotongBaekseoEntity> getListBaekseo(SemotongBaekseoDto param){
		return semotongBaekseoMapper.getListBaekseo(param);
	}


	@Override
	public long getListBaekseoCount(SemotongBaekseoDto param) {
		return semotongBaekseoMapper.getListBaekseoCount(param);
	}
	
	
	
	/**
	 * 백서 단일 조회
	 * @param : SemotongBaekseoDto
	 * @return SemotongBaekseoEntity
	 */
	@Override
	@Transactional(readOnly = true)
	public SemotongBaekseoEntity getBaekseo(int id){

		return semotongBaekseoMapper.getBaekseo(id);
	}
	
	
	/**
	 * 설명서 단일 조회
	 * @param : SemotongBaekseoDto
	 * @return SemotongBaekseoEntity
	 */
	@Override
	@Transactional(readOnly = true)
	public SemotongBaekseoEntity getMvnoInfo(){

		return semotongBaekseoMapper.getMvnoInfo();
	}





}
