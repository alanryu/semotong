package kr.co.ucomp.common.config.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.common.util.DebugUtil;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.AdminUserService;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private KakaoBizTalkUtils bizTalkService;
	@Autowired
	CommCodeMngService codeService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String UserId = user.getUsername();

		/* 로그인 사용자 정보 */
		AdminUserDto adminInfo = adminUserService.getDetailById(UserId);

		HttpSession session = request.getSession();
		session.setAttribute("loginUser", adminInfo);
		session.setAttribute("loginUserNm", adminInfo.getUsername());
		session.setAttribute("LoginId", UserId);

		String nextUrl = "/logincert";

		String targetUrl = "/mbm/adminuser/list";

		/* 권한에 따른 처음 url 설정 */
		String allAuthYn = adminInfo.getAllAuthYn();

		String adminMemAuthYn = adminInfo.getAdminMemAuthYn();
		String userMngYn = adminInfo.getUserMngYn();
		String companyMngYn = adminInfo.getCompanyMngYn();
		String prodMngYn = adminInfo.getProdMngYn();
		String reqMngYn = adminInfo.getReqMngYn();
		String serviceMngYn = adminInfo.getServiceMngYn();
		String settleMngYn = adminInfo.getSettleMngYn();
		String salesMngYn = adminInfo.getSalesMngYn();
		String simpleOpenYn = adminInfo.getSimpleOpenYn();

		String authType = adminInfo.getAuthType();

		System.out.println(authType);

		if ("Y".equals(allAuthYn)) {
			// 전체 권한
			targetUrl = "/mbm/adminuser/list";
		} else {
			// 전체권한이 없는 경우
			if ("Y".equals(adminMemAuthYn)) {
				// 관리자 관리 권한이 있는 경우
				targetUrl = "/mbm/adminuser/list";
			} else {
				// 관리자 관리 권한이 없는 경우
				if ("Y".equals(userMngYn)) {
					// 유저 관리 권한이 있는경우
					targetUrl = "/mbm/users/list";
				} else {
					// 유저 관리 권한이 없는경우
					if ("Y".equals(companyMngYn)) {
						// 입점사 관리 권한이 있는 경우
						targetUrl = "/mbm/company/list";
					} else {
						// 입점사 관리 권한이 없는 경우
						if ("Y".equals(prodMngYn)) {
							// 상품관리 관리 권한이 있는 경우
							targetUrl = "/pbm/plan/list";
						} else {
							// 상품관리 관리 권한이 없는 경우
							if ("Y".equals(reqMngYn)) {
								// 신청 관리 권한이 있는 경우
								// 간편개통 사업자인경우
								if (!StringUtils.isEmpty(simpleOpenYn) && StringUtils.equals("1", simpleOpenYn)) {
									targetUrl = "/pbm/order/list";
									// 간편개통 사업자가 아닌경우 : 추가 수정 필요? (3/29, 양성훈)

								} else {
									if ("INTERNET".equals(authType)) {
										targetUrl = "/pmb/internetreq/listNew";
									} else if ("INTERNET2".equals(authType)) {
										targetUrl = "/pmb/internetreq/listNew2";
									} else if ("MKT".equals(authType)) {
										targetUrl = "/pmb/internetreq/listNew";
									} else if ("SKYLIFE".equals(authType)) {
										targetUrl = "/pmb/internetreq/listNew";
									} else {

										targetUrl = "/pmb/plan-request/list";
									}

								}
							} else {
								// 신청관리 권한이 없는 경우
								if ("Y".equals(salesMngYn)) {
									// 영업관리 권한이 있는경우
									targetUrl = "/pmb/plan-sales/list";
								} else {
									// 서비스 관리 권한이 있는 경우
									if ("Y".equals(serviceMngYn)) {
										targetUrl = "/svc/mainbanner/dealList";
									} else {
										// 서비스 관리 권한이 없는 경우
										if ("Y".equals(settleMngYn)) {
											// 정산관리 권한이 있는 경우
											targetUrl = "/stl/earning/list";
										} else {
											session.removeAttribute("loginUser");
											session.removeAttribute("loginUserNm");
											response.sendRedirect("/login?errorMsg=98");
										}
									}
								}
							}
						}

					}

				}
			}
		}

		String adminPhoneNum = adminInfo.getPhoneNumber();
		String certNo = CommonUtil.generateAuthNo();

		CommCodeSearchDto cmmCodeParam = new CommCodeSearchDto();
		cmmCodeParam.setCodeGroup("common_env_code");
		cmmCodeParam.setUserYn("Y");
		cmmCodeParam.setCode("admin_cert_yn");
		CodeEntity codeinfo = codeService.getCodeInfo(cmmCodeParam);
		String adminCertYn = codeinfo == null ? "N" : codeinfo.getEtc1();
		session.setAttribute("successUrl", targetUrl);
		String adminId = adminInfo.getAdminId();
		List<String> adminIds = Arrays.asList(
				"lkh811204",
				"bsb550522",
				"utechonkykim7654",
				"adpiAmin",
				"sk1031",
				"utechonsjkim23",
				"utechonybkwon",
				"utechonadmin");

		if (adminIds.contains(adminId) == true) {
			adminCertYn = "N";
		}

		if ("N".equals(adminCertYn)) {
			session.setAttribute("LoginCertYn", "Y");
			nextUrl = targetUrl;
		} else {
			// 인증번호 전송
			String token = bizTalkService.getKakaoBizTalkToken();

			CommCodeSearchDto param = new CommCodeSearchDto();
			param.setCodeGroup("biz_template");
			param.setCode("adminCertMsg");

			Map<String, String> variable = new HashMap<String, String>();
			variable.put("cetNo", certNo);

			String sendMsg = bizTalkService.sendBizMessage(param, variable);
			bizTalkService.sendSMSMsg(token, sendMsg, "sms", adminPhoneNum);

			session.setAttribute("LoginCert", certNo);
		}

		super.setAlwaysUseDefaultTargetUrl(true);
		super.setDefaultTargetUrl(nextUrl);
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
