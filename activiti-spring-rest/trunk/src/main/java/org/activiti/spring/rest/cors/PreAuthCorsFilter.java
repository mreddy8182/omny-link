package org.activiti.spring.rest.cors;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.activiti.spring.auth.ExternalUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class PreAuthCorsFilter extends AbstractPreAuthenticatedProcessingFilter {

	protected static final Logger LOGGER = LoggerFactory
			.getLogger(ExternalUserDetailsService.class);


	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String referer = req.getHeader("Referer");
		LOGGER.debug(String.format("Request for %1$s from %2$s",
				req.getRequestURL(), referer));

		String thisOrigin = req.getScheme() + "://" + req.getServerName() + ":"
				+ req.getServerPort();
		if (referer == null || !referer.startsWith(thisOrigin)) {
			LOGGER.info("Cross origin request detected");

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(new PreAuthenticatedAuthentication(
					extractPrincipal(req)));

		} else {
			LOGGER.info("Same origin");
		}

		chain.doFilter(request, response);
	}

	private String extractPrincipal(HttpServletRequest req) {
		String referer = req.getHeader("Referer");
		String user = null;
		try {
			String[] params = referer.substring(referer.indexOf("?") + 1)
					.split("&");
			for (String param : params) {
				if (param.startsWith("email")) {
					user = param.substring(param.indexOf("=") + 1);
					LOGGER.info("Detected Pre-authenticated user: " + user);
				}
			}
		} catch (NullPointerException e) {
			LOGGER.info("NPE parsing user, continue to authentication");
		}
		return user;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest req) {
		LOGGER.debug("getPreAuthenticatedCredentials");
		return null;
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest req) {
		LOGGER.debug("getPreAuthenticatedPrincipal");
		return extractPrincipal(req);
	}
}