package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.pmb.entity.ScrapingPlanEntity;

import java.util.List;

@Mapper
public interface ScrapingPlanMapper {
    List<ScrapingPlanEntity> getScrapingPlanList();
    long getScrapingPlanListCnt();
}
