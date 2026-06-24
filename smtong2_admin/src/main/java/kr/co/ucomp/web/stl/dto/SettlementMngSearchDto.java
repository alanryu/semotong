package kr.co.ucomp.web.stl.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementMngSearchDto extends BaseSearchDto {
    private String createUserName;
    private String stlType;
    private String companyId;
    private String startYyyymm;
    private String endYyyymm;
}