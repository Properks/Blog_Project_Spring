package com.jeongmo.blog.config;

import com.jeongmo.blog.config.jwt.TokenProvider;
import com.jeongmo.blog.config.oauth.OAuth2BasedOnCookieRepository;
import com.jeongmo.blog.config.oauth.OAuth2SuccessfulHandler;
import com.jeongmo.blog.config.oauth.OAuth2UserCustomService;
import com.jeongmo.blog.service.CustomUserDetailService;
import com.jeongmo.blog.service.RefreshTokenService;
import com.jeongmo.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@RequiredArgsConstructor
@Configuration
public class OAuth2WebConfig {


    private static final String LOGIN = "/login";
    private static final String HOME = "/home";
    private final CustomUserDetailService customUserDetailService;
    private final TokenProvider tokenProvider;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    WebSecurityCustomizer configure() {
        return (web -> web.ignoring()
//                .requestMatchers(toH2Console()) Change to my sql
                .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")) // No "/static/**",
                .requestMatchers(AntPathRequestMatcher.antMatcher("/img/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**"))
        );
    }

    // If I input @EnableWebMvc and didn't input WebMvcConfigurer(addResourceHandler), I can't get static resource

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector intro) {
        return new MvcRequestMatcher.Builder(intro);
    }
    // This function get HandlerMappingIntrospector from spring(?) and return MvcRequestMatcher.builder
    // I know reason why filterChain has HttpSecurity as parameter like this function

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(LOGIN).authorizationEndpoint(customizer ->
                                customizer.authorizationRequestRepository(oAuth2BasedOnCookieRepository()))
                        .successHandler(oAuth2SuccessfulHandler())
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2UserCustomService))
                )
                .authorizeHttpRequests(request -> request
//                        .requestMatchers(mvc.pattern(LOGIN)).permitAll()
//                        .requestMatchers(mvc.pattern("/signup")).permitAll()
//                        .requestMatchers(mvc.pattern("/user")).permitAll()
//                        .requestMatchers(mvc.pattern("/api/email/{email}")).permitAll() // Must input with {}
//                        .requestMatchers(mvc.pattern(HOME)).permitAll()
                        .requestMatchers(mvc.pattern("/api/article/**")).authenticated()
                        .requestMatchers(mvc.pattern("/api/category/**")).authenticated()
                        .requestMatchers(mvc.pattern("/api/comment/**")).authenticated()
                        .anyRequest().permitAll())
                .formLogin(login ->
                        login.loginPage(LOGIN)
                                .failureHandler(failureHandler()) // Add failure handler when login is failed
                                .successHandler(formLoginSuccessfulHandler())) // Add successful handler
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl(HOME)
                                .invalidateHttpSession(true))
                .csrf(CsrfConfigurer<HttpSecurity>::disable);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        builder.userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder);

        return builder.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }
    // Need setHidUserNotFoundException(false) to get UsernameNotFoundException. If I implement failureHandler
    // without it, UsernameNotFoundException throw new BadCredentialException.

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> { // onAuthenticationFailure
            String username = request.getParameter("username");
            String errorMsg;
            if (exception instanceof UsernameNotFoundException) { // when not found user
                errorMsg = username + " doesn't exist";
            }
            else if (exception instanceof BadCredentialsException) { // Incorrect password
                errorMsg = "Check your password";
            }
            else {
                errorMsg = "Unknown Error";
            }

            response.sendRedirect("/login?error=" + errorMsg);
            // redirect URL contains error message to use error message in html and js
        };
    }

    @Bean
    TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    OAuth2BasedOnCookieRepository oAuth2BasedOnCookieRepository() {
        return new OAuth2BasedOnCookieRepository();
    }

    @Bean
    OAuth2SuccessfulHandler oAuth2SuccessfulHandler() {
        return new OAuth2SuccessfulHandler(tokenProvider, refreshTokenService, customUserDetailService);
    }

    @Bean
    FormLoginSuccessfulHandler formLoginSuccessfulHandler() {
        return new FormLoginSuccessfulHandler(tokenProvider, refreshTokenService);
    }
}
