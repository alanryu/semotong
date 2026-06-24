package kr.co.ucomp.common.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.AdminUserService;

@Component("permissionChecker")
public class PermissionChecker {

    @Autowired
    private AdminUserService adminUserService;

    public boolean canAccessByPlanReq(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {

            /* 로그인 사용자 정보 */
            AdminUserDto adminInfo = adminUserService.getDetailById(userDetails.getUsername());
            String authType = adminInfo.getAuthType();
            String simpleOpenYn = adminInfo.getSimpleOpenYn();

            return (("ADMIN".equals(authType) || "MANAGE".equals(authType) || "SALE".equals(authType)
                    || !"1".equals(simpleOpenYn)) && !"INTERNET".equals(authType) && !"INTERNET2".equals(authType));
        }

        return false;
    }

    public boolean canAccessByPlanOrder(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {

            /* 로그인 사용자 정보 */
            AdminUserDto adminInfo = adminUserService.getDetailById(userDetails.getUsername());
            String authType = adminInfo.getAuthType();
            String simpleOpenYn = adminInfo.getSimpleOpenYn();

            return (("ADMIN".equals(authType) || "MANAGE".equals(authType) || "1".equals(simpleOpenYn))
                    && !"INTERNET".equals(authType) && !"INTERNET2".equals(authType) && !"MKT".equals(authType));
        }

        return false;
    }

    public boolean canAccessByInternetReq(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {

            /* 로그인 사용자 정보 */
            AdminUserDto adminInfo = adminUserService.getDetailById(userDetails.getUsername());
            String authType = adminInfo.getAuthType();

            return (("ADMIN".equals(authType) || "MANAGE".equals(authType) || "INTERNET".equals(authType)
                    || "INTERNET2".equals(authType)
                    || "MKT".equals(authType) || "SKYLIFE".equals(authType)));
        }

        return false;
    }
}
