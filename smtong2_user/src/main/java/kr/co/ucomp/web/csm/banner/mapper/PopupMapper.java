package kr.co.ucomp.web.csm.banner.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.csm.banner.entity.PopupEntity;

@Mapper
public interface PopupMapper {

  List<PopupEntity> popupList();
}
