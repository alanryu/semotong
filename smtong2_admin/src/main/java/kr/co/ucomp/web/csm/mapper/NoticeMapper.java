package kr.co.ucomp.web.csm.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.entity.NoticeEntity;

import java.util.List;

@Mapper
public interface NoticeMapper {
  List<NoticeEntity> getListNotice(NoticeSearchDto param);

  Long getListNoticeCount(NoticeSearchDto param);

  NoticeEntity getNotice(@Param("id") int param);

  long create(NoticeEntity param);

  long update(NoticeEntity param);

  long delNotice(@Param("id") int param);


}
