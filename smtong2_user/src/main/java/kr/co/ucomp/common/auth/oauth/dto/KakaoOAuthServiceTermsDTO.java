package kr.co.ucomp.common.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoOAuthServiceTermsDTO {

    private Long id;

    @JsonProperty("service_terms")
    private List<ServiceTermDTO> serviceTerms;
}
