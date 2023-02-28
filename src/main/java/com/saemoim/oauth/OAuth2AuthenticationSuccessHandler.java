package com.saemoim.oauth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.saemoim.jwt.JwtUtil;
import com.saemoim.redis.RedisUtil;
import com.saemoim.security.UserDetailsImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final RedisUtil redisUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
		var id = userDetails.getId();
		var username = userDetails.getUsername();
		var role = userDetails.getRole();
		// JWT 생성
		String accessToken = jwtUtil.createAccessToken(id, username, role);
		String refreshToken = _issueRefreshToken(id, accessToken);

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
		response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, refreshToken);
		// response.sendRedirect("http://localhost:63342/final-project-saeMoim/project_saemoim.main/templates/main.html");
	}

	private String _issueRefreshToken(Long userId, String accessToken) {
		String refreshToken = jwtUtil.createRefreshToken(userId);

		redisUtil.setData(refreshToken, accessToken, JwtUtil.REFRESH_TOKEN_TIME);

		return refreshToken;
	}
}