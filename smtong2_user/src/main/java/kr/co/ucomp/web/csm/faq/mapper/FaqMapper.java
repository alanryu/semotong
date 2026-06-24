package kr.co.ucomp.web.csm.faq.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;

@Mapper
public interface FaqMapper {
	List<FaqEntity> getListFaq(FaqSearchDto param);

	Long getListFaqCount(FaqSearchDto param);

	FaqEntity getFaq(@Param("id") int param);

}
