package ru.saydov.coffeeorder.client.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Фильтр Spring Security, извлекающий JWT из заголовка {@code Authorization}.
 *
 * <p>Если токен валиден — кладёт UUID клиента в {@code SecurityContextHolder}
 * в виде {@code principal}. Невалидные и отсутствующие токены пропускаются без
 * ошибки — авторизация будет отклонена дальше по цепочке через правила
 * {@code authorizeHttpRequests}.
 */
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var header = request.getHeader(AUTH_HEADER);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            var token = header.substring(BEARER_PREFIX.length());
            if (jwtProvider.isValid(token)) {
                var clientId = jwtProvider.getClientId(token);
                var auth = new UsernamePasswordAuthenticationToken(clientId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
