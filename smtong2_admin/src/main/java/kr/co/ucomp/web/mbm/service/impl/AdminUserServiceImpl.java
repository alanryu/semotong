package kr.co.ucomp.web.mbm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.AdminUserSearchDto;
import kr.co.ucomp.web.mbm.entity.AdminUserEntity;
import kr.co.ucomp.web.mbm.mapper.AdminUserMapper;
import kr.co.ucomp.web.mbm.service.AdminUserService;



@Service("AdminUserService")
public class AdminUserServiceImpl implements AdminUserService {
	@Autowired
	AdminUserMapper mapper;
	
	/**
	 * 관리자 list 조회
	 * @param : NoticeSearchDto
	 * @return List<FaqEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AdminUserEntity> getList(AdminUserSearchDto param) {
		List<AdminUserEntity> list =  mapper.getList(param);
		
		return list;
		
	}
	
	public long getListCount(AdminUserSearchDto param)  {
		long count = mapper.getListCount(param);
		return  count;
	}

	
	/**
	 * 관리자 단건 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public AdminUserEntity getDetail(@Param("id") int param) {
		AdminUserEntity record = mapper.getDetail(param);
		return record;
	}
	
	
	/**
	 * 관리자 아이디 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public AdminUserDto getDetailById(@Param("userId") String userId) {
		AdminUserDto record = mapper.getDetailById(userId);
		return record;
	}
	
	/**
	 * 관리자 저장
	 * @param : FaqEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long create(AdminUserEntity param) {
		
		return mapper.create(param);
		
	}
	
	/**
	 * 관리자  수정
	 * @param : FaqEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
	public long update(AdminUserEntity param) {
		
		return mapper.update(param);
		
	}
	
	
	/**
	 * 관리자  수정
	 * @param : FaqEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
	public long updatePwd(AdminUserEntity param) {
		
		return mapper.updatePwd(param);
		
	}
	
	/**
	 * 관리자 삭제
	 * @param : id(삭제 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional
	public long del(@Param("id") int param) {
		
		return mapper.del(param);
	}
	
}
