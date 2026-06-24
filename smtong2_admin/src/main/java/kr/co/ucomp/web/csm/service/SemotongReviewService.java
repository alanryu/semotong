package kr.co.ucomp.web.csm.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.entity.SemotongReviewEntity;


public interface SemotongReviewService {

	List<SemotongReviewEntity> reviewList(SemotongReviewDto param);
	
	long countReviewList(SemotongReviewDto param);
	
	SemotongReviewEntity review(SemotongReviewDto param);
	
	long insertReview(SemotongReviewEntity param);
	
	long updateReview(SemotongReviewEntity param);
	
	long deleteReview(SemotongReviewEntity param);
	
	long updateDisplayYn(SemotongReviewEntity param);
}
