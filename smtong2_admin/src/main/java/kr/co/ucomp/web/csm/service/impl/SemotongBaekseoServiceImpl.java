package kr.co.ucomp.web.csm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.entity.SemotongBaekseoEntity;
import kr.co.ucomp.web.csm.mapper.SemotongBaekseoMapper;
import kr.co.ucomp.web.csm.service.SemotongBaekseoSevice;

@Service
public class SemotongBaekseoServiceImpl implements SemotongBaekseoSevice {

    @Autowired SemotongBaekseoMapper semotongBaekseoMapper;

    /**
     * 백서 list 조회
     * @param : SemotongBaekseoDto
     * @return List<SemotongBaekseoEntity>
     */
    @Override
    @Transactional(readOnly = true)
    public List<SemotongBaekseoEntity> getListBaekseo(SemotongBaekseoDto param){
        return semotongBaekseoMapper.getListBaekseo(param);
    }
    
    @Override
	public Long getListBaekseoCount(SemotongBaekseoDto param) {
		return semotongBaekseoMapper.getListBaekseoCount(param);
	}

    /**
     * 백서 단일 조회
     * @param : SemotongBaekseoDto
     * @return SemotongBaekseoEntity
     */
    @Override
    @Transactional(readOnly = true)
    public SemotongBaekseoEntity getBaekseo(SemotongBaekseoDto param){
        return semotongBaekseoMapper.getBaekseo(param);
    }

    /**
     * 백서 저장
     * @param : SemotongBaekseoDto
     * @return 결과 (생성 갯수)
     */
    @Override
    @Transactional
    public long insertBaekseo(SemotongBaekseoEntity param) {
        return semotongBaekseoMapper.insertBaekseo(param);
    }

    /**
     * 백서 수정
     * @param : SemotongBaekseoDto
     * @return 결과 (수정 갯수)
     */
    @Override
    @Transactional
    public long updateBaekseo(SemotongBaekseoEntity param) {

        return semotongBaekseoMapper.updateBaekseo(param);

    }

    /**
     * 백서 삭제
     * @param : SemotongBaekseoDto
     * @return 결과 (삭제 갯수)
     */
    @Override
    @Transactional
    public long deleteBaekseo(SemotongBaekseoEntity param) {
        return semotongBaekseoMapper.deleteBaekseo(param);
    }

	@Override
	public long updateDisplaySp(SemotongBaekseoEntity param) {
		return semotongBaekseoMapper.updateDisplaySp(param);
	}

	
}
