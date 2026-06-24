package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.PlanCateMngDto;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.entity.PlanCateMngEntity;

import java.util.List;
import java.util.Map;

@Mapper
public interface PlanCateMngMapper {

    List<PlanCateMngEntity> getTaglist(PlanCateMngDto param);
    
    List<PlanCateMngEntity> getCatelist(PlanCateMngDto param);
    
    long getCateListCount(PlanCateMngDto param);
    
    
    
    long create(PlanCateMngEntity param);
    
    long update(PlanCateMngEntity param);

    long delete(PlanCateMngEntity param);
    
    long updatePlanTag(PlanUpdateDto param);
    
    
    
    String maxTagCode();
    
    String maxCateCode();
    
    
    
    long deleteTagCateMap(@Param("cateCode") String cateCode);
    
    long insertTagCateMap(@Param("cateCode") String cateCode,@Param("tagCode") String tagCode);
    

}
