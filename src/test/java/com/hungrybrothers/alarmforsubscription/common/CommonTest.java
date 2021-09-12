package com.hungrybrothers.alarmforsubscription.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import com.hungrybrothers.alarmforsubscription.security.JwtProperties;
import com.hungrybrothers.alarmforsubscription.security.JwtTokenProvider;
import com.hungrybrothers.alarmforsubscription.subscription.SubscriptionRepository;
import com.hungrybrothers.alarmforsubscription.utils.MailUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc()
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Disabled
@Transactional
@PropertySource("classpath:application.yml")
public class CommonTest {
    protected static final String TEST_USER_ID = "yh22953905@gmail.com";
    protected static final String TEST_USERNAME = "username";
    protected static final String TEST_PASSWORD = "password";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected SubscriptionRepository subscriptionRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected MailUtils mailUtils;

    protected Account savedAccount;

    protected String jwtToken;

    @BeforeEach
    public void init() {
        Set<AccountRole> roles = new HashSet<>();
        roles.add(AccountRole.ADMIN);
        roles.add(AccountRole.CLIENT);

        savedAccount = accountRepository.save(Account.builder()
            .userId(TEST_USER_ID)
            .username(TEST_USERNAME)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .roles(roles)
            .build());

        jwtToken = JwtProperties.REQUEST_HEADER_AUTHORIZATION_TYPE + jwtTokenProvider.createJwtToken(savedAccount);
    }
}
