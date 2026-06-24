package kr.co.ucomp.web.csm.notice.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.notice.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.notice.entity.NoticeEntity;

import java.util.List;

@Mapper
public interface NoticeMapper {
  List<NoticeEntity> getListNotice(NoticeSearchDto param);

  Long getListNoticeCount(NoticeSearchDto param);

  NoticeEntity getNotice(@Param("id") long param);

}
