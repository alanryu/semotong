package kr.co.ucomp.web.svc.banner.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BannerSearchDto  extends BaseSearchDto {
	
	private Integer isDispStatusAll;
	private Integer isDispStatusBef;
	private Integer isDispStatusDsp;
	private Integer isDispStatusEnd;
	private String searchRegDtStdt;
	private String searchRegDtEddt;
	private String searchBannerType;
	private String searchOrderSp;
	private String searchUseYn;

	// searchRegDtStdt에 대한 8자리 날짜 검증을 추가한 setter
    public void setSearchRegDtStdt(String searchRegDtStdt) {
        if (searchRegDtStdt != null && searchRegDtStdt.length() == 10) {
        	this.searchRegDtStdt = searchRegDtStdt + " 00:00:00";
        } else {
        	this.searchRegDtStdt = searchRegDtStdt;
        }
    }
    
    
    public void setSearchRegDtEddt(String searchRegDtEddt) {
    	 if (searchRegDtEddt != null && searchRegDtEddt.length() == 10) {
         	this.searchRegDtEddt = searchRegDtEddt + " 23:59:59";
         } else {
         	this.searchRegDtEddt = searchRegDtEddt;
         }
    }
	
}
