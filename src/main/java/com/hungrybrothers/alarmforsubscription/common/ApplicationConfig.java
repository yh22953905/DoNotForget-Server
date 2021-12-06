package com.hungrybrothers.alarmforsubscription.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountAdapter;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@PropertySource("classpath:application.yml")
public class ApplicationConfig {
    @Value("${jwt.admin-id}")
    private String adminId;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return delegatingPasswordEncoder;
    }

    @Bean
    public GracefulShutdown gracefulShutdown() {
        GracefulShutdown gracefulShutdown = new GracefulShutdown();
        return gracefulShutdown;
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers(gracefulShutdown);
        return tomcatServletWebServerFactory;
    }

    @Bean
    public AuditorAware auditorAware() {
        return () -> {
            Optional<Authentication> optionalAuthentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

            if (optionalAuthentication.isPresent()) {
                Authentication authentication = optionalAuthentication.get();

                if (!authentication.getPrincipal().equals("anonymousUser")) {
                    AccountAdapter accountAdapter = (AccountAdapter) authentication.getPrincipal();
                    Account account = accountAdapter.getAccount();
                    return Optional.of(account);
                } else {
                    return null;
                }
            } else {
                return null;            }
        };
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountRepository accountRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {
//                Set<Account.Role> roles = new HashSet<>();
//                roles.add(Account.Role.ADMIN);
//                roles.add(Account.Role.CLIENT);
//
//                Account admin = Account.builder()
//                        .userId(adminId)
//                        .username("admin")
//                        .password("1234")
//                        .roles(roles)
//                        .build();
//
//                Account savedAdmin = accountRepository.save(admin);
            }
        };
    }
}
