package kr.co.ucomp.common.global.Enum;

import lombok.Getter;

@Getter
public enum UserRole implements CodeEnum {
    ADMIN("A", "관리자"),
    HOST("H", "입점사"),
    USER("U", "사용자");

    private String code;
    private String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
