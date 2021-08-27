package com.hungrybrothers.alarmforsubscription.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.exception.UserAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final Long TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30;

    private final AccountRepository accountRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    public String createJwtToken(Account account) {
        Date now = new Date();

        List<String> roles = account.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        return JWT.create()
            .withClaim("userId", account.getUserId())
            .withClaim("role", roles)
            .withIssuedAt(now)
            .withExpiresAt(new Date(now.getTime() + TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC256(secretKey));
    }

    public String resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(Const.REQUEST_HEADER_AUTHORIZATION))
            .orElse("")
            .replaceFirst(Const.REQUEST_HEADER_AUTHORIZATION_TYPE, "");
    }

    public Authentication getAuthentication(String jwtToken) {
        Account account = accountRepository.findByUserId(getEmail(jwtToken))
            .orElseThrow(UserAuthenticationException::new);

        return new UsernamePasswordAuthenticationToken(account, "", account.getAuthorities());
    }

    public boolean validateToken(String jwtToken) {
        if (!StringUtils.hasText(jwtToken)) return false;

        if (JWT.decode(jwtToken).getExpiresAt().before(new Date())) return false;

        return true;
    }

    private String getEmail(String jwtToken) {
        return JWT.decode(jwtToken).getClaim("userId").asString();
    }
}
