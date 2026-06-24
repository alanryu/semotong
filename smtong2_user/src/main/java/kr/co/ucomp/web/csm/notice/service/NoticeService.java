package kr.co.ucomp.web.csm.notice.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;
import kr.co.ucomp.web.csm.notice.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.notice.entity.NoticeEntity;

import java.util.List;


public interface NoticeService {
	
	List<NoticeEntity> getListNotice(NoticeSearchDto param);
	
	long getListNoticeCount(NoticeSearchDto param);
	
	NoticeEntity getNotice(@Param("id") long param);
	
}
