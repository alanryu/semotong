package kr.co.ucomp.web.csm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.mapper.SemotongReviewMapper;
import kr.co.ucomp.web.csm.service.SemotongReviewService;

import java.util.List;

@Component
@Service
public class SemotongReviewServiceImpl implements SemotongReviewService {
	
	@Autowired
	private SemotongReviewMapper reviewMapper;
	
	/**
	 * review list 조회
	*/
	@Override
	@Transactional(readOnly = true)
	public List<SemotongReviewEntity> reviewList(SemotongReviewDto param) {
	    return reviewMapper.reviewList(param);
	}
	
	@Override
	public long countReviewList(SemotongReviewDto param){
	    return reviewMapper.countReviewList(param);
	}
	
	
	
	/**
	 * review 단건 조회
	 */
	@Override
	public SemotongReviewEntity review(SemotongReviewDto param){
	    return reviewMapper.review(param);
	}
	
	/**
	 * review 저장
	 */
	@Override
	@Transactional
	public long insertReview(SemotongReviewEntity param){
	    return reviewMapper.insertReview(param);
	}
	
	/**
	 * review  수정
	 */
	@Override
	@Transactional
	public long updateReview(SemotongReviewEntity param){
	    return reviewMapper.updateReview(param);
	}
	
	/**
	 * review  삭제
	 */
	@Override
	@Transactional
	public long deleteReview(SemotongReviewEntity param){
	    return reviewMapper.deleteReview(param);
	}
	
	
	public long updateDisplayYn(SemotongReviewEntity param) {
		return reviewMapper.updateDisplayYn(param);
	}
	
	
	
	
	
	
	
}
