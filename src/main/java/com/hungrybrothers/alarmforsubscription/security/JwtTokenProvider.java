package com.hungrybrothers.alarmforsubscription.security;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountAdapter;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.exception.UserAuthenticationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final AccountRepository accountRepository;

    @Value("${jwt.secret}")
    private String secretKey; // TODO to JwtProperties.java

    public String createJwtToken(Account account) {
        Date now = new Date();

        List<String> roles = account.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        return JWT.create()
            .withClaim(JwtProperties.KEY_USER_ID, account.getUserId())
            .withClaim(JwtProperties.KEY_ROLES, roles) // TODO role -> roles
            .withIssuedAt(now)
            .withExpiresAt(new Date(now.getTime() + JwtProperties.JWT_TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC256(secretKey)); // TODO HMAC256 -> HMAC512
    }

    public String createRefreshToken() {
        Date now = new Date();

        return JWT.create()
            .withIssuedAt(now)
            .withExpiresAt(new Date(now.getTime() + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC256(secretKey));
    }

    public String resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(JwtProperties.REQUEST_HEADER_AUTHORIZATION))
            .orElse("")
            .replaceFirst(JwtProperties.REQUEST_HEADER_AUTHORIZATION_TYPE, "");
    }

    public Authentication getAuthentication(String jwtToken) {
        AccountAdapter accountAdapter = new AccountAdapter(accountRepository.findByUserId(getEmail(jwtToken))
            .orElseThrow(UserAuthenticationException::new));

        return new UsernamePasswordAuthenticationToken(accountAdapter, "", accountAdapter.getAuthorities());
    }

    public boolean validateToken(String jwtToken) {
        if (!StringUtils.hasText(jwtToken)) return false;

        return !JWT.decode(jwtToken).getExpiresAt().before(new Date());
    }

    private String getEmail(String jwtToken) {
        return JWT.decode(jwtToken).getClaim(JwtProperties.KEY_USER_ID).asString();
    }
}
