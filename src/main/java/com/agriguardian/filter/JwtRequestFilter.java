package com.agriguardian.filter;

import com.agriguardian.domain.AppUserAuthDetails;
import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Status;
import com.agriguardian.exception.BadTokenException;
import com.agriguardian.exception.UserFromTokenDoesNotExistsException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.security.JwtProvider;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    public static final String REFRESH = "/api/v1/auth/refresh";
    private final JwtProvider jwtProvider;
    private final HandlerExceptionResolver resolver;
    private final AppUserService userService;


    public JwtRequestFilter(JwtProvider jwtProvider,
                            AppUserService userService,
                            @Lazy @Qualifier(value = "handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtProvider = jwtProvider;
        this.resolver = resolver;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            log.debug("header: {}", request.getHeader("Authorization"));

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String token = authorizationHeader.substring(7);
                try {
                    jwtProvider.isTokenAccess(token);
                    AppUserAuthDetails tokenInfo = jwtProvider.readTokenInfo(token);

                    AppUser user = userService.findByUsername(tokenInfo.getUsername()).orElseThrow(() -> new UserFromTokenDoesNotExistsException("User does not exists: " + tokenInfo.getUsername()));
                    if (Status.ACTIVATED != user.getStatus()) {
                        throw new AccessDeniedException("Account status is " + user.getStatus() + ". Need activation");
                    }
                    log.info("internalFilter: Token data: " + tokenInfo);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(tokenInfo, null, tokenInfo.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    chain.doFilter(request, response);
                } catch (BadTokenException | JwtException | UserFromTokenDoesNotExistsException | AccessDeniedException exception) {
                    if (REFRESH.equals(new ServletServerHttpRequest(request).getURI().getRawPath())) {
                        log.warn("internalFilter: " + exception);
                        chain.doFilter(request, response);
                    } else {
                        resolver.resolveException(request, response, null, exception);
                    }
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
