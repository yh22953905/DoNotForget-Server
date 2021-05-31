package com.hungrybrothers.alarmforsubscription.sign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class SignService {
    @Value("${jwt.url.kakao}")
    private String kakaoUrl;
    @Value("${jwt.url.naver}")
    private String naverUrl;

    private String AUTHORIZATION = "Authorization";
    private String BEARER = "Bearer ";
    private String EMAIL = "email";
    private String JWT_TOKEN = "jwt-token";

    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public Map<String, ?> authenticationOpenId(SignDto signDto) throws JsonProcessingException {
        TokenAuthentication tokenAuthentication = null;

        Account.Provider provider = Account.Provider.valueOf(signDto.getType().toUpperCase());

        switch (provider) {
            case KAKAO:
                tokenAuthentication = new KakaoTokenAuthentication();
                break;
            case NAVER:
                tokenAuthentication = new NaverTokenAuthentication();
                break;
        }

        try {
            Account account = tokenAuthentication.doAuthentication(signDto.getOpenIdToken());

            if (!accountRepository.findByUserId(account.getUserId()).isPresent()) {
                account = accountRepository.save(account);
            } else {
                account = accountRepository.findByUserId(account.getUserId()).get();
            }

            Set<String> roles = account.getRoles().stream().map(Account.Role::name).collect(Collectors.toSet());
            String jwtToken = jwtTokenProvider.createToken(String.valueOf(account.getId()), roles);

            Map<String, Object> result = new HashMap<>();
            result.put(JWT_TOKEN, jwtToken);

            return result;
        } catch (JsonProcessingException e) {
            throw e;
        }
    }

    interface TokenAuthentication {
        Account doAuthentication(String tokenValue) throws JsonProcessingException;
    }

    private class KakaoTokenAuthentication implements TokenAuthentication {
        RestTemplate restTemplate = new RestTemplate();

        @Override
        public Account doAuthentication(String tokenValue) throws JsonProcessingException {
            RequestEntity<?> requestEntity = RequestEntity
                    .get(URI.create(kakaoUrl))
                    .header(AUTHORIZATION, BEARER + tokenValue)
                    .build();

            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            String body = responseEntity.getBody();
            JsonNode jsonBodyNode = objectMapper.readTree(body);
            String userEmail = jsonBodyNode.findPath(EMAIL).asText();

            return Account.builder()
                    .userId(userEmail)
                    .username("KAKAO-" + UUID.randomUUID())
                    .roles(Collections.singleton(Account.Role.CLIENT))
                    .provider(Account.Provider.KAKAO)
                    .build();
        }
    }

    private class NaverTokenAuthentication implements TokenAuthentication {
        RestTemplate restTemplate = new RestTemplate();

        @Override
        public Account doAuthentication(String tokenValue) throws JsonProcessingException {
            RequestEntity<?> requestEntity = RequestEntity
                    .get(URI.create(naverUrl))
                    .header(AUTHORIZATION, BEARER + tokenValue)
                    .build();

            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            String body = responseEntity.getBody();
            JsonNode jsonBodyNode = objectMapper.readTree(body);
            String userEmail = jsonBodyNode.findPath(EMAIL).asText();

            return Account.builder()
                    .userId(userEmail)
                    .username("NAVER-" + UUID.randomUUID())
                    .roles(Collections.singleton(Account.Role.CLIENT))
                    .provider(Account.Provider.NAVER)
                    .build();
        }
    }
}
