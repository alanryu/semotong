package kr.co.ucomp.web.cmm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.cmm.dto.FileMngDto;
import kr.co.ucomp.web.cmm.entity.FileMngEntity;
import kr.co.ucomp.web.cmm.mapper.FileMngMapper;
import kr.co.ucomp.web.cmm.service.FileMngService;

@Service("FileMngService")

public class FileMngServiceImpl implements FileMngService {
	
	@Autowired FileMngMapper mapper;

	@Override
	public FileMngEntity selectFileInfo(FileMngDto param) {
		return mapper.selectFileInfo(param);
	}

	@Override
	public List<FileMngEntity> selectFileInfoList(FileMngDto param) {
		return mapper.selectFileInfoList(param);
	}

	@Override
	public int insertFileInfo(FileMngEntity param) {
		mapper.insertFileInfo(param);
		return param.getId();
	}

	@Override
	public int deleteFileInfo(FileMngEntity param) {
		return mapper.deleteFileInfo(param);
	}
	
}
