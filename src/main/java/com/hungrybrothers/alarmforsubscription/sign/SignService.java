package com.hungrybrothers.alarmforsubscription.sign;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import lombok.RequiredArgsConstructor;
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

    public Account signUp(SignRequest signRequest) {
        accountRepository.findByUserId(signRequest.getUserId()).ifPresent(user -> {
            throw new RuntimeException(); // TODO AccountAlreadyExistsException
        });

        Set<AccountRole> roles = new HashSet<>();
        roles.add(AccountRole.valueOf(signRequest.getAccountRole()));

        return accountRepository.save(Account.builder()
            .userId(signRequest.getUserId())
            .username(signRequest.getUsername())
            .password(passwordEncoder.encode(signRequest.getPassword()))
            .roles(roles)
            .build());
    }
}
