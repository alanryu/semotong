package kr.co.ucomp.web.plan.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.dto.PlanFreebieSearchDto;
import kr.co.ucomp.web.plan.dto.searchPlanZzimDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitMappingEntity;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanFreebieMappingEntity;
import kr.co.ucomp.web.plan.entity.PlanZzimEntity;
import kr.co.ucomp.web.plan.mapper.PlanBenefitMapper;
import kr.co.ucomp.web.plan.mapper.PlanFreebieMapper;
import kr.co.ucomp.web.plan.mapper.PlanZzimMapper;
import kr.co.ucomp.web.plan.service.PlanZzimService;

@Component
@Service
public class PlanZzimServiceImpl implements PlanZzimService {

	
	@Autowired PlanZzimMapper mapper;
	@Autowired private PlanBenefitMapper benefitMapper;
	@Autowired private PlanFreebieMapper freebieMapper;

	@Override
	public List<PlanEntity> getZzimListPlan(searchPlanZzimDto param) {
		
		List<PlanEntity> list  = mapper.getZzimListPlan(param);
		
		if(list !=null && list.size()>0) {
			for(PlanEntity itm : list) {
				// 요금제 혜{택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList =  benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if(benList !=null) {
					itm.setPlanBenefitCnt(benList.size());	
				}
				
				// 요금제 사은품정보 추가
				PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
				freebieParam.setDisplayYn("Y");
				freebieParam.setPlanId(itm.getId());
				List<PlanFreebieMappingEntity> freebieList = freebieMapper.maplistAll(freebieParam);
				itm.setFreebieList(freebieList);
				if(freebieList !=null) {
					itm.setPlanFreebieCnt(freebieList.size());
				}
			}
		}
		
		return list;
	}
	
	@Override
	public List<PlanEntity> getZzimNoDataListPlan(searchPlanZzimDto param){
		
		List<PlanEntity> list  = mapper.getZzimNoDataListPlan(param);
		
		if(list !=null && list.size()>0) {
			for(PlanEntity itm : list) {
				// 요금제 혜{택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList =  benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if(benList !=null) {
					itm.setPlanBenefitCnt(benList.size());	
				}
				
				// 요금제 사은품정보 추가
				PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
				freebieParam.setDisplayYn("Y");
				freebieParam.setPlanId(itm.getId());
				List<PlanFreebieMappingEntity> freebieList = freebieMapper.maplistAll(freebieParam);
				itm.setFreebieList(freebieList);
				if(freebieList !=null) {
					itm.setPlanFreebieCnt(freebieList.size());
				}
			}
		}
		
		return list;
	}
	
	@Override
	public long getZzimNoDataListPlanCount(searchPlanZzimDto param) {
		return mapper.getZzimNoDataListPlanCount(param);
	}
	


	@Override
	public List<PlanZzimEntity> getlist(searchPlanZzimDto param) {
	    return mapper.getlist(param);
	}

	@Override
	public long getCount(searchPlanZzimDto param) {
		return mapper.getCount(param);
	}


    @Override
    public long create(PlanZzimEntity param) {
        return mapper.create(param);
    }

    
    @Override
    public long delete(PlanZzimEntity param) {
        return mapper.delete(param);
    }

    @Override
    public long deleteAll(PlanZzimEntity param) {
        return mapper.deleteAll(param);
    }
    
	
}
