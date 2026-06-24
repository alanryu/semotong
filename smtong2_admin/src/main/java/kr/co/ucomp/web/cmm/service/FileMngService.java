package kr.co.ucomp.web.cmm.service;

import java.util.List;

import kr.co.ucomp.web.cmm.dto.FileMngDto;
import kr.co.ucomp.web.cmm.entity.FileMngEntity;


public interface FileMngService {
	
	FileMngEntity selectFileInfo(FileMngDto param);
	
	List<FileMngEntity> selectFileInfoList(FileMngDto param);
	
	int insertFileInfo(FileMngEntity param);
	
	int deleteFileInfo(FileMngEntity param);
	
	
}
