package kr.co.ucomp.web.mypage.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequestDTO {

    @NotNull(message = "이메일 입력은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "패스워드 입력은 필수입니다.")
    private String password;
}