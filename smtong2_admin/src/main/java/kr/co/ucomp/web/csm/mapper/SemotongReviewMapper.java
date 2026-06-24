package kr.co.ucomp.web.csm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.entity.SemotongReviewEntity;

import java.util.List;

@Mapper
public interface SemotongReviewMapper {

	List<SemotongReviewEntity> reviewList(SemotongReviewDto param);
	
	long countReviewList(SemotongReviewDto param);

    SemotongReviewEntity review(SemotongReviewDto param);

    long insertReview(SemotongReviewEntity param);

    long updateReview(SemotongReviewEntity param);

    long deleteReview(SemotongReviewEntity param);
    
    long updateDisplayYn(SemotongReviewEntity param);
    
    
}
