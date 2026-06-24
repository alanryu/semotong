package kr.co.ucomp.common.exception.error;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 기본 예외 생성
     *
     * @param errorCode 에러 코드
     */
    public ErrorException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
        log.error(errorCode.message());
    }

    /**
     * 예외 생성
     *
     * @param errorCode 에러 코드
     * @param cause     발생한 예외
     */
    public ErrorException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
        log.error("{}: {}", errorCode.message(), cause.getMessage(), cause);
    }
}