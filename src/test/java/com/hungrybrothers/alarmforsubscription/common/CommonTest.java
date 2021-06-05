package com.hungrybrothers.alarmforsubscription.common;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.subscription.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
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
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected SubscriptionRepository subscriptionRepository;

    @Value("${jwt.test-token}")
    protected String testToken;

    @Value("${jwt.admin-id}")
    private String adminId;

    @Value("${jwt.admin-username}")
    private String adminUsername;

    @Value("${jwt.admin-password}")
    private String adminPassword;

    protected Account admin;

    @BeforeEach
    public void init() {
        Set<Account.Role> roles = new HashSet<>();
        roles.add(Account.Role.ADMIN);
        roles.add(Account.Role.CLIENT);

        Account account = Account.builder()
                .userId(adminId)
                .username(adminUsername)
                .password(adminPassword)
                .roles(roles)
                .build();

        admin = accountRepository.save(account);
    }
}
