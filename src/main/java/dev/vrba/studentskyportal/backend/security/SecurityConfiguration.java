package dev.vrba.studentskyportal.backend.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
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

        // Disable storing session and appending the JSESSIONID cookie as JWT is stateless by design
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
