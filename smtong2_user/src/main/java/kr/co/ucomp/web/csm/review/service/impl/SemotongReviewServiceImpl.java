package kr.co.ucomp.web.csm.review.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.review.mapper.SemotongReviewMapper;
import kr.co.ucomp.web.csm.review.service.SemotongReviewService;

import java.util.List;

@Component
@Service
public class SemotongReviewServiceImpl implements SemotongReviewService {

    @Autowired
    private SemotongReviewMapper reviewMapper;

    /**
     * review list 조회
     * @param : SemotongReviewDto
     * @return List<SemotongReviewDto>
    */
    @Override
    @Transactional(readOnly = true)
    public List<SemotongReviewEntity> reviewList(SemotongReviewDto param) {
        return reviewMapper.reviewList(param);
    }

    
    /**
     * review list 조회
     * @param : SemotongReviewDto
     * @return List<SemotongReviewDto>
    */
    @Override
    @Transactional(readOnly = true)
    public int reviewListCount(SemotongReviewDto param) {
        return reviewMapper.reviewListCount(param);
    }

    
    /**
     * review 단건 조회
     * @param : id(조회 id)
     * @return SemotongReviewDto
     */
    @Override
    @Transactional(readOnly = true)
    public SemotongReviewEntity review(long id){
        return reviewMapper.review(id);
    }

    /**
     * review 저장
     * @param : SemotongReviewDto
     * @return 결과 (생성 갯수)
     */
    @Override
    @Transactional
    public long insertReview(SemotongReviewEntity param){
        return reviewMapper.insertReview(param);
    }

    /**
     * review  수정
     * @param : SemotongReviewDto
     * @return 결과 (수정 갯수)
     */
    @Override
    @Transactional
    public long updateReview(SemotongReviewEntity param){
        return reviewMapper.updateReview(param);
    }

    /**
     * review  삭제
     * @param : SemotongReviewDto
     * @return 결과 (삭제 갯수)
     */
    @Override
    @Transactional
    public long deleteReview(long id){
        return reviewMapper.deleteReview(id);
    }
    
    /**
     * review list 조회
     * @param : SemotongReviewDto
     * @return List<SemotongReviewDto>
    */
    @Override
    @Transactional(readOnly = true)
    public List<SemotongReviewEntity> userOrderReviewList(@Param("userId") long id) {
        return reviewMapper.userOrderReviewList(id);
    }


	@Override
	public SemotongReviewEntity reviewAggregate(SemotongReviewDto param) {
		return reviewMapper.reviewAggregate(param);
	}
}
