package kr.co.ucomp.common.global.base;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseSearchDto {
    private int page;
	
    private int recordSize;
	
    private String searchType;
	
    private String keyword;
	
	private String searchStartDt;
	
	private String searchEndDt;
	
	private String searchOrderType;
    
    
    public BaseSearchDto() {
        this.page = 1;
        this.recordSize = 10;
    }

    public int getOffset() {
        return (page - 1) * recordSize;
    }
    
    
    
    // searchStartDt에 대한 8자리 날짜 검증을 추가한 setter
    public void setSearchStartDt(String searchStartDt) {
        if (searchStartDt != null && searchStartDt.length() == 10) {
        	this.searchStartDt = searchStartDt + " 00:00:00";
        } else {
        	this.searchStartDt = searchStartDt;
        }
    }
    
    
    public void setSearchEndDt(String searchEndDt) {
    	 if (searchEndDt != null && searchEndDt.length() == 10) {
         	this.searchEndDt = searchEndDt + " 23:59:59";
         } else {
         	this.searchEndDt = searchEndDt;
         }
    }
    
    	
}
