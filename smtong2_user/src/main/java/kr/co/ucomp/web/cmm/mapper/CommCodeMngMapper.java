package kr.co.ucomp.web.cmm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.cmm.dto.CodeGroupDto;
import kr.co.ucomp.web.cmm.dto.CommCodeDto;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.entity.CodeGroupEntity;


@Mapper
public interface CommCodeMngMapper {
	/* ============== 코드 그룹 관리 ============================*/
	List<CodeGroupEntity> getListCodeGroup(CommCodeSearchDto param);

	Long getListCodeGroupCount(CommCodeSearchDto param);

	CodeGroupEntity getCodeGroup(@Param("codeGroup") String codeGroup);

	/* ============== 코드 관리 ============================*/
	List<CodeEntity> getListCode(CommCodeSearchDto param);

	Long getListCodeCount(CommCodeSearchDto param);

	CodeEntity getCode(CommCodeSearchDto param);
	
}
