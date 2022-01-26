package com.hungrybrothers.alarmforsubscription.sign;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import com.hungrybrothers.alarmforsubscription.exception.UserIdPasswordException;
import com.hungrybrothers.alarmforsubscription.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class SignService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Account signUp(SignUpRequest signUpRequest) {
        accountRepository.findByUserId(signUpRequest.getUserId()).ifPresent(user -> {
            throw new RuntimeException(); // TODO AccountAlreadyExistsException
        });

        Set<AccountRole> roles = new HashSet<>();
        roles.add(AccountRole.valueOf(signUpRequest.getAccountRole()));

        return accountRepository.save(Account.builder()
            .userId(signUpRequest.getUserId())
            .username(signUpRequest.getUsername())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .roles(roles)
            .build());
    }
}
