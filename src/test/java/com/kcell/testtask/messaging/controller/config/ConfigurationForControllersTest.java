package com.kcell.testtask.messaging.controller.config;

import com.kcell.testtask.messaging.model.Role;
import com.kcell.testtask.messaging.model.User;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class ConfigurationForControllersTest {
    public static final String MOCK_USERNAME = "yeldos.manap@gmail.com";

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (username.equals(MOCK_USERNAME)) {
                    return User.builder()
                            .username(MOCK_USERNAME)
                            .role(Role.valueOf("USER"))
                            .build();
                } else {
                    throw new UsernameNotFoundException("User <" + username + "> not found");
                }
            }
        };
    }

}
