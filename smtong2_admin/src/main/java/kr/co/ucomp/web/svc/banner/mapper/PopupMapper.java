package kr.co.ucomp.web.svc.banner.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.banner.dto.PopupSearchDto;
import kr.co.ucomp.web.svc.banner.entity.PopupEntity;

import java.util.List;

@Mapper
public interface PopupMapper {

  List<PopupEntity> list(PopupSearchDto param);

  long listCount(PopupSearchDto param);

  PopupEntity getDetail(@Param("id") long id);

  long create(PopupEntity param);

  long update(PopupEntity param);

  long delete(@Param("id") long id);

  List<PopupEntity> listWithoutLimit(PopupSearchDto dto);

  List<PopupEntity> getActivePopopList();

  Boolean moveOrder(@Param("id") long popId, @Param("direction") String direction);

  PopupEntity findPreviousPopup(Integer currentSort);

  PopupEntity findNextPopup(Integer currentSort);

  int updateSort(@Param("id") Long id, @Param("sort") Integer sort);

  long reorderSort();

}