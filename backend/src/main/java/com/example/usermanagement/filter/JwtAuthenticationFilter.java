package com.example.usermanagement.filter;

import com.example.usermanagement.config.JwtConfig;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtConfig jwtConfig, UserService userService) {
        this.jwtConfig = jwtConfig;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(jwtConfig.getHeader());
        String token = jwtConfig.resolveToken(authHeader);

        if (token != null && !jwtConfig.isTokenExpired(token)) {
            String username = jwtConfig.getUsernameFromToken(token);
            Long userId = jwtConfig.getUserIdFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getById(userId);
                if (user != null && user.getStatus() == 1) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    List<Role> roles = userService.getUserRoles(userId);
                    for (Role role : roles) {
                        authorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
                    }

                    List<Permission> permissions = userService.getUserPermissions(userId);
                    for (Permission permission : permissions) {
                        authorities.add(new SimpleGrantedAuthority(permission.getPermissionCode()));
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
