package kr.co.ucomp.common.exception.error;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드
 *
 * @param httpStatus HTTP 상태
 * @param code       에러 코드
 * @param message    에러 메시지
 */
public record ErrorCode(HttpStatus httpStatus, String code, String message) {}
