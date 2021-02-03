package dev.vrba.studentskyportal.backend.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(@NotNull UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(@NotNull HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/api/authentication/**").permitAll()
                .antMatchers("/api/**").hasRole("USER")
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().denyAll();

        // Append Cross-Origin-Resource-Sharing headers
        // so the api can be hosted on a separate subdomain (eg. api.fis-vse.cz or idk)
        http.cors();

        // Disable CSRF protection as it is useless when calling API endpoints
        http.csrf().disable();

        // Setup the user details service connected to PostgreSQL backend
        http.userDetailsService(this.userDetailsService);

        // Disable storing session and appending the JSESSIONID cookie as JWT is stateless by design
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
