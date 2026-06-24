package kr.co.ucomp.web.svc.banner.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.BooleanNode;

import kr.co.ucomp.web.svc.banner.dto.PopupSearchDto;
import kr.co.ucomp.web.svc.banner.entity.PopupEntity;
import kr.co.ucomp.web.svc.banner.mapper.PopupMapper;
import kr.co.ucomp.web.svc.banner.service.PopupService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@Service
@Slf4j
public class PopupServiceImpl implements PopupService {

  @Autowired
  PopupMapper mapper;

  @Override
  public List<PopupEntity> list(PopupSearchDto param) {
    return mapper.list(param);
  }

  @Override
  public long listCount(PopupSearchDto param) {
    return mapper.listCount(param);
  }

  @Override
  public PopupEntity getDetail(long id) {
    return mapper.getDetail(id);
  }

  @Override
  public long create(PopupEntity param) {
    return mapper.create(param);
  }

  @Override
  public long update(PopupEntity param) {
    return mapper.update(param);
  }

  @Override
  public long delete(long id) {
    return mapper.delete(id);
  }

  @Override
  public List<PopupEntity> listWithoutLimit(PopupSearchDto dto) {
    return mapper.listWithoutLimit(dto);
  }

  @Override
  @Transactional
  public Boolean moveOrder(long id, String direction) {
    try {
      // 현재 팝업 정보 조회
      PopupEntity currentPopup = mapper.getDetail(id);
      if (currentPopup == null) {
        log.warn("존재하지 않는 팝업 ID: {}", id);
        return false;
      }

      Integer currentSort = currentPopup.getSort();
      if (currentSort == null) {
        log.warn("팝업의 순서 정보가 없습니다. ID: {}", id);
        return false;
      }

      PopupEntity targetPopup = null;

      if ("up".equals(direction)) {
        // 바로 위 항목 찾기
        targetPopup = mapper.findPreviousPopup(currentSort);
      } else if ("down".equals(direction)) {
        // 바로 아래 항목 찾기
        targetPopup = mapper.findNextPopup(currentSort);
      } else {
        log.warn("잘못된 방향 파라미터: {}", direction);
        return false;
      }

      if (targetPopup == null) {
        log.info("이동할 대상이 없습니다. ID: {}, 방향: {}", id, direction);
        return false;
      }

      // 순서 교환
      Integer targetSort = targetPopup.getSort();

      // 현재 팝업의 순서를 대상 팝업의 순서로 변경
      mapper.updateSort(id, targetSort);

      // 대상 팝업의 순서를 현재 팝업의 순서로 변경
      mapper.updateSort(targetPopup.getId(), currentSort);

      log.info("팝업 순서 이동 완료 - ID: {}, 방향: {}, 현재순서: {} -> {}",
          id, direction, currentSort, targetSort);

      return true;

    } catch (Exception e) {
      log.error("팝업 순서 이동 중 오류 발생 - ID: {}, 방향: {}", id, direction, e);
      throw e;
    }

  }

  @Override
  public void reorderSort() {
    mapper.reorderSort();
  }

}