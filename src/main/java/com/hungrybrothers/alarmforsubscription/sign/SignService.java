package com.hungrybrothers.alarmforsubscription.sign;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import com.hungrybrothers.alarmforsubscription.exception.AccountAlreadyExistsException;
import com.hungrybrothers.alarmforsubscription.exception.ErrorCode;
import com.hungrybrothers.alarmforsubscription.exception.VerifyCodeException;
import com.hungrybrothers.alarmforsubscription.security.JwtTokenProvider;
import com.hungrybrothers.alarmforsubscription.utils.MailUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Transactional
@Service
@RequiredArgsConstructor
public class SignService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailUtils mailUtils;

    public Account signUp(SignUpRequest signUpRequest) {
        accountRepository.findByUserId(signUpRequest.getUserId()).ifPresent(user -> {
            throw new AccountAlreadyExistsException(ErrorCode.VERIFY_CODE_EXCEPTION);
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

    @SneakyThrows
    public SignInResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtTokenProvider.validateToken(refreshToken)) {
            Account account = accountRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UsernameNotFoundException(refreshToken));

            return SignInResponse.builder()
                .jwtToken(jwtTokenProvider.createJwtToken(account))
                .refreshToken(jwtTokenProvider.createRefreshToken())
                .build();
        } else {
            throw new JWTVerificationException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    public void verifyEmail(VerifyEmailRequest request, Account account) {
        if (Objects.equals(request.getVerifyCode(), account.getVerifyCode())) {
            account.setVerified(true);
            accountRepository.save(account);
            return;
        }

        throw new VerifyCodeException(ErrorCode.VERIFY_CODE_EXCEPTION);
    }

    public void sendEmail(Account account) {
        String code = mailUtils.generateCode();

        mailUtils.sendMail(account.getUserId(), code);

        account.setVerifyCode(code);
        accountRepository.save(account);
    }
}
