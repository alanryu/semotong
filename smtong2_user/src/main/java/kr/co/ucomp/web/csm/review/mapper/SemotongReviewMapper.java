package kr.co.ucomp.web.csm.review.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;

import java.util.List;

@Mapper
public interface SemotongReviewMapper {

	List<SemotongReviewEntity> reviewList(SemotongReviewDto param);
	
	int reviewListCount(SemotongReviewDto param);

    SemotongReviewEntity review(@Param("id") long id);

    long insertReview(SemotongReviewEntity param);

    long updateReview(SemotongReviewEntity param);

    long deleteReview(@Param("id") long id);
    
    
    List<SemotongReviewEntity> userOrderReviewList(@Param("userId") long id);

	SemotongReviewEntity reviewAggregate(SemotongReviewDto param);
}
