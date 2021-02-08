package dev.vrba.studentskyportal.backend.security;

import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final JwtTokenService jwtTokenService;

    private final UsersRepository usersRepository;

    public SecurityConfiguration(
            UserDetailsService userDetailsService,
            JwtTokenService jwtTokenService,
            UsersRepository usersRepository
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.usersRepository = usersRepository;
    }

    @Override
    public void configure(@NotNull HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/",
                        "/api-docs",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/authentication/**"
                ).permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/**").hasRole("USER")
                .anyRequest().denyAll();

        // Append Cross-Origin-Resource-Sharing headers
        // so the api can be hosted on a separate subdomain (eg. api.fis-vse.cz or idk)
        http.cors();

        // Disable CSRF protection as it is useless when calling API endpoints
        http.csrf().disable();

        // Setup the user details service connected to PostgreSQL backend
        http.userDetailsService(userDetailsService);

        // Disable storing session and appending the JSESSIONID cookie as JWT is stateless by design
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add JWT filters for authentication and authorization flow
        http.addFilter(new JwtAuthorizationFilter(
                    authenticationManager(),
                    jwtTokenService,
                    userDetailsService,
                    usersRepository
            ));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UsernameEncoder usernameEncoder() throws NoSuchAlgorithmException {
        return new UsernameEncoder("SHA-256");
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        configuration.setAllowCredentials(false);
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
