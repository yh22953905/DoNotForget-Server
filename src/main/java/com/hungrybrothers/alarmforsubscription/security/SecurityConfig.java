package com.hungrybrothers.alarmforsubscription.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.redis.refreshtoken.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin().disable()
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
            .authorizeRequests()
            .mvcMatchers(Const.API_SIGN + "/up"
                , Const.API_SIGN + "/refresh-token"
                , Const.API_HATEOAS).permitAll()
            .mvcMatchers(Const.ERROR_URL).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
            .anyRequest().authenticated();

        http
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider, objectMapper, accountRepository, refreshTokenService))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtTokenProvider));
    }
}
