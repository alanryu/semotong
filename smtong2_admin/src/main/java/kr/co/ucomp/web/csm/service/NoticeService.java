package kr.co.ucomp.web.csm.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;
import kr.co.ucomp.web.csm.entity.NoticeEntity;

import java.util.List;


public interface NoticeService {
	
	List<NoticeEntity> getListNotice(NoticeSearchDto param);
	
	long getListNoticeCount(NoticeSearchDto param);
	
	NoticeEntity getNotice(@Param("id") int param);
	
	long create(NoticeEntity param);
	
	long update(NoticeEntity param);
	
	long delNotice(@Param("id") int param);
	
	
	
	
}
