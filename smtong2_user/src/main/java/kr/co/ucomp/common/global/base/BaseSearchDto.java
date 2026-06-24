package kr.co.ucomp.common.global.base;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
}
