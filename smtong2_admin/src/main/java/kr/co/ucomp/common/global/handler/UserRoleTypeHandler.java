package kr.co.ucomp.common.global.handler;

import kr.co.ucomp.common.global.Enum.UserRole;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType) throws SQLException {
        // UserRole enum의 code 값을 저장
        ps.setString(i, parameter.getCode());
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // DB에서 가져온 code 값을 UserRole enum으로 변환
        String code = rs.getString(columnName);
        return code == null ? null : getUserRoleByCode(code);
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : getUserRoleByCode(code);
    }

    @Override
    public UserRole getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : getUserRoleByCode(code);
    }

    // Helper 메서드: code를 통해 UserRole enum 값 반환
    private UserRole getUserRoleByCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole code: " + code);
    }
}