package kr.co.ucomp.web.csm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;

@Mapper
public interface FaqMapper {
	List<FaqEntity> getListFaq(FaqSearchDto param);

	Long getListFaqCount(FaqSearchDto param);

	FaqEntity getFaq(@Param("id") int param);

	long create(FaqEntity param);

	long update(FaqEntity param);

	long delFaq(@Param("id") int param);
	
}
