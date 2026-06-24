package kr.co.ucomp.web.cmm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.cmm.dto.FileMngDto;
import kr.co.ucomp.web.cmm.entity.FileMngEntity;


@Mapper
public interface FileMngMapper {

	FileMngEntity selectFileInfo(FileMngDto param);
	
	List<FileMngEntity> selectFileInfoList(FileMngDto param);
	
	void insertFileInfo(FileMngEntity param);
	
	int deleteFileInfo(FileMngEntity param);
	
}
