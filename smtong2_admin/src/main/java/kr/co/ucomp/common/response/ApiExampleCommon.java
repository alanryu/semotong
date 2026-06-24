package kr.co.ucomp.common.response;

public class ApiExampleCommon {
	// ----------------------------------------------------------- HTTP 상태 공통 ---------------------------------------------------------------- //
		public static final String NOT_FOUND_RESPONSE = """
		{
		  "code": 404
		  ,"status": "NOT_FOUND"
		  ,"message": "Not Found"
		  ,"totalCnt": 0
		  ,"data": null
		}""";
		public static final String BAD_REQUEST_RESPONSE = """
		{
		  "timestamp": "2024-12-26T05:08:01.465+00:00"
		  ,"status": 400
		  ,"error": "Bad Request"
		  ,"trace": "org.springframework.web.method.annotation"
		  ,"message": "Failed to "
		  ,"path": "/evt"
		}""";
		public static final String INTERNAL_S_E_RESPONSE = """
		{
		  "code": 500,
		  "status": "INTERNAL_SERVER_ERROR",
		  "message": "Internal Server Error",
		  "totalCnt": 0,
		  "data": null
		}""";
		
		
}
