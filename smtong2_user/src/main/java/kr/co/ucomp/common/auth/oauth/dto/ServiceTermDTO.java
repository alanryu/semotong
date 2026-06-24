package kr.co.ucomp.common.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ServiceTermDTO {
    private String tag;

    private boolean required;

    private boolean agreed;

    private boolean revocable;

    @JsonProperty("agreed_at")
    private LocalDateTime agreedAt;
}
