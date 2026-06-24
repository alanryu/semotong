package kr.co.ucomp.web.csm.banner.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.csm.banner.entity.PopupEntity;
import kr.co.ucomp.web.csm.banner.mapper.PopupMapper;
import kr.co.ucomp.web.csm.banner.service.PopupService;

@Component
@Service
public class PopupServiceImpl implements PopupService {

  @Autowired
  PopupMapper mapper;

  @Override
  public List<PopupEntity> popupList() {

    return mapper.popupList();

  }

}
