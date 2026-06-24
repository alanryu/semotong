package kr.co.ucomp.web.plan.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.dto.PlanFreebieSearchDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitMappingEntity;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanFreebieMappingEntity;
import kr.co.ucomp.web.plan.mapper.PlanBenefitMapper;
import kr.co.ucomp.web.plan.mapper.PlanFreebieMapper;
import kr.co.ucomp.web.plan.mapper.PlanMapper;
import kr.co.ucomp.web.plan.service.PlanService;
import java.util.List;

@Service("PlanService")
public class PlanServiceImpl implements PlanService {

	@Autowired
	private PlanMapper mapper;
	@Autowired
	private PlanBenefitMapper benefitMapper;
	@Autowired
	private PlanFreebieMapper freebieMapper;

	@Override
	@Transactional(readOnly = true)
	public List<PlanEntity> getList(SearchPlanDto param) {
		List<PlanEntity> list = mapper.getList(param);
		if (list != null && list.size() > 0) {
			for (PlanEntity itm : list) {
				// 요금제 혜{택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList = benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if (benList != null) {
					itm.setPlanBenefitCnt(benList.size());
				}
				// 요금제 사은품정보 추가
//				PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
//				freebieParam.setDisplayYn("Y");
//				freebieParam.setPlanId(itm.getId());
//				List<PlanFreebieMappingEntity> freebieList = freebieMapper.maplistAll(freebieParam);
//				itm.setFreebieList(freebieList);
//				if (freebieList != null) {
//					itm.setPlanFreebieCnt(freebieList.size());
//				}
			}
		}
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public long getListCount(SearchPlanDto param) {
		return mapper.getListCount(param);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanEntity getDetail(int id) {
		PlanEntity result = new PlanEntity();
		result = mapper.getDetail(id);

		if (result != null) {
			// 요금제 혜택 정보 추가
			PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
			benefitParam.setDisplayYn("Y");
			benefitParam.setPlanId(result.getId());
			List<PlanBenefitMappingEntity> benList = benefitMapper.maplistAll(benefitParam);
			result.setBenefitList(benList);
			if (benList != null) {
				result.setPlanBenefitCnt(benList.size());
				final String hostNm = result.getHostNm();
				benList.forEach(benefit -> benefit.setHostNm(hostNm));
			}
//			// 요금제 사은품정보 추가
//
//			PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
//			freebieParam.setDisplayYn("Y");
//			freebieParam.setPlanId(result.getId());
//			List<PlanFreebieMappingEntity> freebieList = freebieMapper.maplistAll(freebieParam);
//			result.setFreebieList(freebieList);
//			if (freebieList != null) {
//				result.setPlanFreebieCnt(freebieList.size());
//			}
		}

		return mapper.getDetail(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlanEntity> getAllListByPlanIds(SearchPlanDto param) {
		List<PlanEntity> list = mapper.getAllListByPlanIds(param);

		if (list != null && list.size() > 0) {
			for (PlanEntity itm : list) {
				// 요금제 혜{택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList = benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if (benList != null) {
					itm.setPlanBenefitCnt(benList.size());
				}
//				// 요금제 사은품정보 추가
//
//				PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
//				freebieParam.setDisplayYn("Y");
//				freebieParam.setPlanId(itm.getId());
//				List<PlanFreebieMappingEntity> freebieList = freebieMapper.maplistAll(freebieParam);
//				itm.setFreebieList(freebieList);
//				if (freebieList != null) {
//					itm.setPlanFreebieCnt(freebieList.size());
//				}
			}
		}

		return list;
	}

	@Override
	public long getBannerPlanListCount(SearchPlanDto param) {
		return mapper.getBannerPlanListCount(param);
	}

	@Override
	public List<PlanEntity> getBannerPlanList(SearchPlanDto param) {

		List<PlanEntity> list = mapper.getBannerPlanList(param);

		if (list != null && list.size() > 0) {
			for (PlanEntity itm : list) {
				// 요금제 혜{택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList = benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if (benList != null) {
					itm.setPlanBenefitCnt(benList.size());
				}
//				// 요금제 사은품정보 추가
//
//				PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
//				freebieParam.setDisplayYn("Y");
//				freebieParam.setPlanId(itm.getId());
//				List<PlanFreebieMappingEntity> freebieList = freebieMapper.maplistAll(freebieParam);
//				itm.setFreebieList(freebieList);
//				if (freebieList != null) {
//					itm.setPlanFreebieCnt(freebieList.size());
//				}
			}
		}

		return list;
	}

	@Override
	public List<PlanEntity> getChatbotPlanList(SearchPlanDto param) {
		
		
		List<PlanEntity> list  = mapper.getChatbotPlanList(param);
		
		if(list !=null && list.size()>0) {
			for(PlanEntity itm : list) {
				// 요금제 혜택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList =  benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if(benList !=null) {
					itm.setPlanBenefitCnt(benList.size());	
				}
				
			}
		}
		
		return list;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public long getRecomPlanListCount(SearchPlanDto param) {
		return mapper.getRecomPlanListCount(param);
	}
	
	
	@Override
	public List<PlanEntity> getRecomPlanList(SearchPlanDto param) {
		
		
		List<PlanEntity> list  = mapper.getRecomPlanList(param);
		
		if(list !=null && list.size()>0) {
			for(PlanEntity itm : list) {
				// 요금제 혜택 정보 추가
				PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
				benefitParam.setDisplayYn("Y");
				benefitParam.setPlanId(itm.getId());
				List<PlanBenefitMappingEntity> benList =  benefitMapper.maplistAll(benefitParam);
				itm.setBenefitList(benList);
				if(benList !=null) {
					itm.setPlanBenefitCnt(benList.size());	
				}
				
			}
		}
		
		return list;
	}
		
	
	@Override
	public List<PlanEntity> getPopulerPlanList() {
		List<PlanEntity> list  = mapper.getPopulerPlanList();
		return list;
	}
	

}