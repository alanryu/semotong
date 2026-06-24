package kr.co.ucomp.web.svc.banner.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.banner.dto.PopupSearchDto;
import kr.co.ucomp.web.svc.banner.entity.PopupEntity;

import java.util.List;

public interface PopupService {

  List<PopupEntity> list(PopupSearchDto param);

  long listCount(PopupSearchDto param);

  PopupEntity getDetail(@Param("id") long id);

  long create(PopupEntity param);

  long update(PopupEntity param);

  long delete(@Param("id") long id);

  List<PopupEntity> listWithoutLimit(PopupSearchDto dto);

  Boolean moveOrder(@Param("id") long popId, @Param("direction") String direction);

  void reorderSort();

}