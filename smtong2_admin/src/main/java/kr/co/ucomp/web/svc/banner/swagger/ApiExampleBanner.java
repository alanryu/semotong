package kr.co.ucomp.web.svc.banner.swagger;

public class ApiExampleBanner {

	/* ============================== FAQ APIs 요청 예시 json data ==================================*/
	public static final String BANNERLIST_REQUEST = """
	{
	  "page": 1,
	  "recordSize": 10,
	  "keyword": "배너명 검색",
	  "searchStartDt": "2024-01-01",
	  "searchEndDt": "2024-01-01",
	  "isDispStatusAll": "(상태조회)전체 조회(1/0)",
	  "isDispStatusBef": "(상태조회) 게시전 배너 조회여부(1/0)",
	  "isDispStatusDsp": "(상태조회) 게시 중인 배너 조회 여부(1/0)",
	  "isDispStatusEnd": "(상태조회) 게시 종료된 배너 조회 여부(1/0)",
	  "searchRegDtStdt": "(등록일 조회)2024-01-01T11:11Z",
	  "searchRegDtEddt": "(등록일 조회)2024-01-01T11:11Z",
	  "searchBannerType": "배너종류(공통코드 banner_type)"
	}""";

	public static final String BANNERCREATE_REQUEST = """
	{
	  "status": "ALWAYS : 배너종류(공통코드 banner_status)",
	  "type": "01 : 배너종류(공통코드 banner_type)",
	  "sort": 1,
	  "url": "https://example.com",
	  "bannerAlt": "배너 alt",
	  "urlTarget": "자창/팝업(S/P)",
	  "startDate": "2024-12-24T05:03Z",
	  "endDate": "2024-12-25T05:03Z",
	  "createId": "1",
	  "modifiedId": "1"
	}""";
	
	public static final String BANNERUPDATE_REQUEST = """
	{
	  "inquiry": "자주묻는 질문입니다.",
	  "ansContent": "자주묻는 답변입니다.",
	  "faqCategory": "cate01(공통코드 :qna_cate)",
	  "displayYn": "Y/N",
	  "modifiedId": 1
	}""";
	
	public static final String BANNERLIST_LIST_RESPONSE = """
	{
	  "code": 200,
	  "status": "OK",
	  "message": "Success",
	  "totalCnt": 1,
	  "data": [
	    {
	      "id": 6,
	      "status": "ALWAYS",
	      "type": "01",
	      "bannerName": "배너명3",
	      "imagePc": "",
	      "imageMo": "",
	      "sort": 1,
	      "url": "https://example.com",
	      "bannerAlt": "대체텍스트(Alt 값)",
	      "urlTarget": "S",
	      "startDate": "2024-12-26T00:00:00",
	      "endDate": "2024-12-28T00:00:00",
	      "createDate": "2024-12-26T16:58:43",
	      "createId": "1",
	      "modifiedDate": "2024-12-26T16:58:43",
	      "modifiedId": "1"
	    }
	  ]
	}""";
	
	public static final String BANNERLIST_RESPONSE = """
		{
		  "code": 200,
		  "status": "OK",
		  "message": "Success",
		  "totalCnt": 1,
		  "data": 
		    {
		      "id": 6,
		      "status": "ALWAYS",
		      "type": "01",
		      "bannerName": "배너명3",
		      "imagePc": "",
		      "imageMo": "",
		      "sort": 1,
		      "url": "https://example.com",
		      "bannerAlt": "대체텍스트(Alt 값)",
		      "urlTarget": "S",
		      "startDate": "2024-12-26T00:00:00",
		      "endDate": "2024-12-28T00:00:00",
		      "createDate": "2024-12-26T16:58:43",
		      "createId": "1",
		      "modifiedDate": "2024-12-26T16:58:43",
		      "modifiedId": "1"
		    }
		}""";

	public static final String BANNER_PLAN_CREATE_REQUEST = """
	{
	  "bannerId": 1,
	  "planId": 1
	}""";	
}
