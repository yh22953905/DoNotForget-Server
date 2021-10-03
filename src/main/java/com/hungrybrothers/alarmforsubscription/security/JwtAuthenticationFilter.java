package com.hungrybrothers.alarmforsubscription.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountAdapter;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.sign.RefreshToken;
import com.hungrybrothers.alarmforsubscription.sign.RefreshTokenRepository;
import com.hungrybrothers.alarmforsubscription.sign.SignInRequest;
import com.hungrybrothers.alarmforsubscription.sign.SignInResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper, AccountRepository accountRepository, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl(Const.API_SIGN + "/in");
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        SignInRequest signInRequest = objectMapper.readValue(request.getInputStream(), SignInRequest.class);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            signInRequest.getUserId(), signInRequest.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        AccountAdapter accountAdapter = (AccountAdapter) authResult.getPrincipal();

        String jwtToken = jwtTokenProvider.createJwtToken(accountAdapter.getAccount());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        Account account = accountRepository.findByUserId(accountAdapter.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException(accountAdapter.getUsername()));

        account.setRefreshToken(refreshToken);

        accountRepository.save(account);

        RefreshToken savedRefreshToken = refreshTokenRepository.save(RefreshToken.builder()
            .userId(account.getUserId())
            .refreshToken(refreshToken)
            .build());

        response.getWriter().write(objectMapper.writeValueAsString(SignInResponse.builder()
            .jwtToken(jwtToken)
            .refreshToken(refreshToken)
            .build()));
    }
}
