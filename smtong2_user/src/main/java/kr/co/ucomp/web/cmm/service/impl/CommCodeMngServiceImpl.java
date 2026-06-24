package kr.co.ucomp.web.cmm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.cmm.dto.CodeGroupDto;
import kr.co.ucomp.web.cmm.dto.CommCodeDto;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.entity.CodeGroupEntity;
import kr.co.ucomp.web.cmm.mapper.CommCodeMngMapper;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;

@Service("CommCodeMngService")

public class CommCodeMngServiceImpl implements CommCodeMngService {
	@Autowired
	CommCodeMngMapper mapper;
	
	
	/* =========================================================== 코드 그룹 서비스 ========================================== */
	/**
	 * 공통코드 그룹 list 조회
	 * @param : FaqSearchDto
	 * @return List<FaqEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public  List<CodeGroupEntity> getListCodeGroup(CommCodeSearchDto param){
		List<CodeGroupEntity> list  = mapper.getListCodeGroup(param);
		return list;
		
	}
	
	
	/**
	 * 공통코드 그룹 list 조회
	 * @param : FaqSearchDto
	 * @return List<FaqEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public long getListCodeGroupCount(CommCodeSearchDto param){
		long count = mapper.getListCodeGroupCount(param);
		
		return count;
		
	}
	
	/**
	 *  공통코드  그룹 단건 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public CodeGroupEntity getCodeGroup(@Param("codeGroup") String param) {
		CodeGroupEntity record = mapper.getCodeGroup(param);
		return record;
	}
	
	
	
	/* =========================================================== 코드 서비스 ========================================== */
	
	/**
	 * 공통코드 list 조회
	 * @param : FaqSearchDto
	 * @return List<FaqEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public  List<CodeEntity> getListCode(CommCodeSearchDto param){
		List<CodeEntity> list = mapper.getListCode(param);
		
		return list;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public  long getListCodeCount(CommCodeSearchDto param){
		long count = mapper.getListCodeCount(param);
		return count;
		
	}
	
	
	/**
	 *  공통코드  단건 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public CodeEntity getCode(CommCodeSearchDto param) {
		CodeEntity record = mapper.getCode(param);
		return record;
	}
	
	
	
}
