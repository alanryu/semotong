package kr.co.ucomp.web.csm.review.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;


public interface SemotongReviewService {

    List<SemotongReviewEntity> reviewList(SemotongReviewDto param);
    
    int reviewListCount(SemotongReviewDto param);
    
    SemotongReviewEntity review(@Param("id") long id);

    long insertReview(SemotongReviewEntity param);

    long updateReview(SemotongReviewEntity param);

    long deleteReview(@Param("id") long id);
    
    List<SemotongReviewEntity> userOrderReviewList(@Param("userId") long id);

	SemotongReviewEntity reviewAggregate(SemotongReviewDto param);
}
