package com.hungrybrothers.alarmforsubscription.security;

import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.account.AccountService;
import com.hungrybrothers.alarmforsubscription.common.Const;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    @Value("jwt.secret")
    private String secretKey;

    @Value("jwt.test-token")
    private String testToken;

    @Value("jwt.admin-id")
    private String adminId;

    private final Long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 365;

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public String createToken(String userId, Set<String> roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String userId = "";

        userId = getUserId(token);

        if (token.equals(testToken)) {
            userId = String.valueOf(accountRepository.findByUserId(adminId).get().getId());
        }

        UserDetails userDetails = accountService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUserId(String token) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        String userId = claims.getSubject();
        return userId;
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(Const.JWT_TOKEN);
    }

    public Boolean validateToken(String jwtToken) {
        try {
            if (jwtToken.equals(testToken)) {
                return true;
            }

            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (RuntimeException e) {
            return false;
        }
    }
}
