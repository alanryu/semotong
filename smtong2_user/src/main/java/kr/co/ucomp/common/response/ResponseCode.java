package kr.co.ucomp.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 응답에 사용되는 표준 응답 코드 열거형
 * HTTP 상태 코드와 메시지를 포함합니다.
 *
 * @author 이정민
 * @since 2024.12.11
 * @version v1.2
 * 
 * 2024.12.18 (수)
 * 파일 업로드 관련 status 추가
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
	
    /**
     * 성공적인 요청 처리 (2xx)
     */
    OK(200, HttpStatus.OK, "Success"),                    	// 요청이 성공적으로 처리됨
    CREATED(201, HttpStatus.CREATED, "Created"),          	// 새로운 리소스가 성공적으로 생성됨
    ACCEPTED(202, HttpStatus.ACCEPTED, "Accepted"),			// 요청은 접수되었으나 처리는 완료되지 않음.			
    NO_CONTENT(204, HttpStatus.NO_CONTENT, "No Content"), 	// 요청은 성공했지만 반환할 컨텐츠 없음
    
    /**
     * 클라이언트 오류 응답 (4xx)
     */
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "Bad Request"),                               // 잘못된 요청 구문
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Unauthorized"),                             // 인증 필요
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "Forbidden"),                                     // 권한 없음
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "Not Found"),                                    // 리소스를 찾을 수 없음
    METHOD_NOT_ALLOWED(405, HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),          // 허용되지 않는 HTTP 메소드
    
    REQUEST_TIMEOUT(408, HttpStatus.REQUEST_TIMEOUT, "Request Timeout"), // 요청 시간 초과
    CONFLICT(409, HttpStatus.CONFLICT, "Conflict"),                                       // 리소스 상태의 충돌
    PAYLOAD_TOO_LARGE(413, HttpStatus.PAYLOAD_TOO_LARGE, "Payload Too Large"), // 요청 페이로드가 너무 큼
    UNSUPPORTED_MEDIA_TYPE(415, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type"), // 지원하지 않는 미디어 타입
    VALIDATION_ERROR(422, HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error"),            // 유효성 검증 실패

    /**
     * 서버 오류 응답 (5xx)
     */
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"), // 서버 내부 오류
    SERVICE_UNAVAILABLE(503, HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable");       // 서비스 일시적 사용 불가

    /**
     * HTTP 상태 코드
     */
    private final int code;

    /**
     * Spring Framework의 HttpStatus 열거형
     */
    private final HttpStatus status;

    /**
     * 응답 메시지
     */
    private final String message;

    /**
     * HttpStatus의 이름을 문자열로 반환
     *
     * @return HttpStatus 열거형의 이름
     */
    public String getStatus() {
        return status.name();
    }
}
