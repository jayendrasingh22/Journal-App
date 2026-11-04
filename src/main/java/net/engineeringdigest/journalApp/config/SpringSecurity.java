package net.engineeringdigest.journalApp.config;

import net.engineeringdigest.journalApp.Service.UserDetailServiceImpl;
import net.engineeringdigest.journalApp.filter.JwtFilter;
import net.engineeringdigest.journalApp.oauth.GoogleOAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter {
    private final UserDetailServiceImpl userDetailService;
    private final JwtFilter jwtFilter;
    private final GoogleOAuthSuccessHandler googleOAuthSuccessHandler;

    public SpringSecurity(UserDetailServiceImpl userDetailService, JwtFilter jwtFilter,
                          GoogleOAuthSuccessHandler googleOAuthSuccessHandler) {
        this.userDetailService = userDetailService;
        this.jwtFilter = jwtFilter;
        this.googleOAuthSuccessHandler = googleOAuthSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/public/**", "/oauth2/**", "/login/oauth2/**",
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/journal/**", "/user/**", "/user").authenticated()
                .anyRequest().permitAll()
                .and()
                .oauth2Login().successHandler(googleOAuthSuccessHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}