package kr.co.ucomp.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;


/**
 * 공통 응답 형식
 * @param <T>
 *
 * @author 이정민
 * @since 2024.12.11
 * @version v1.1
 * 2024.12.18 (수)
 * totalCnt 추가.
 */
@Getter
@Builder
public class CustomApiResponse<T> {
    private final int code;
    private final String status;
    private final String message;
    private final long totalCnt;
    private final T data;


    public static <T> ResponseEntity<CustomApiResponse<T>>  of(ResponseCode responseCode, long totalCnt, T data,String message) {
        CustomApiResponse<T> response = CustomApiResponse.<T>builder()
                .code(responseCode.getCode())
                .status(responseCode.getStatus())
                .message(StringUtils.isEmpty(message) ?  responseCode.getMessage() : message)
                .totalCnt(totalCnt)
                .data(data)
                .build();

        return ResponseEntity.status(responseCode.getCode()).body(response);
    }


    public static <T> ResponseEntity<CustomApiResponse<T>> success(ResponseCode responseCode, T data) {
        return of(responseCode, 0, data,"");
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> success(ResponseCode responseCode, long totalCnt, T data) {
        return of(responseCode, totalCnt, data,"");
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> error(ResponseCode responseCode) {
        return of(responseCode, 0,null,"");
    }
    
    public static <T> ResponseEntity<CustomApiResponse<T>> error(ResponseCode responseCode,String message) {
        return of(responseCode, 0,null,message);
    }

}
