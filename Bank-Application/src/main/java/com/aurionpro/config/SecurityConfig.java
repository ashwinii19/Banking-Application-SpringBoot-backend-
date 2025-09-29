//package com.aurionpro.config;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.aurionpro.security.JwtAuthenticationEntryPoint;
//import com.aurionpro.security.JwtAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//	private final UserDetailsService userDetailsService;
//	private final JwtAuthenticationFilter authenticationFilter;
//	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
//	
//	@Autowired
//	public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter authenticationFilter,
//			JwtAuthenticationEntryPoint authenticationEntryPoint) {
//		this.userDetailsService = userDetailsService;
//		this.authenticationFilter = authenticationFilter;
//		this.authenticationEntryPoint = authenticationEntryPoint;
//	}
//	
//	@Bean
//    static PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//	
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
// 
// 
//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .cors(withDefaults())
//            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
//            .authorizeHttpRequests(auth -> auth
//                // allow register & login without authentication
//               
////                .requestMatchers(
////                		"/api/auth/register",
////                		"/api/auth/login",
////                        "/swagger-ui.html/**",
////                        "/v3/api-docs/**",
////                        "/swagger-resources/**",
////                        "/webjars/**"
////                    ).permitAll()
//            		
//            		.requestMatchers(
//            			    "/v3/api-docs",
//            			    "/v3/api-docs/**",
//            			    "/swagger-ui.html",
//            			    "/swagger-ui/**",
//            			    "/swagger-resources/**",
//            			    "/webjars/**"
//            			).permitAll()
//
//        	            // apne public APIs
//        	            .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
//        	  
//               
//               
//
//                // secure student endpoints (require authentication)
//                .requestMatchers("/api/customers/**").authenticated()
//                .requestMatchers("/api/users/**").authenticated()
//                .requestMatchers("/api/accounts/**").authenticated()
//                .requestMatchers("/api/transactions/**").authenticated()
// 
//                // everything else also requires authentication
//                .anyRequest().authenticated()
//            );
// 
//        // Add JWT filter
//        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
// 
//        return http.build();
//    }
//
////	@Bean
////	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////	    http.csrf(csrf -> csrf.disable())
////	        .authorizeHttpRequests(auth -> auth
////	        		.requestMatchers(
////	        			    "/api/auth/register",
////	        			    "/api/auth/login",
////	        			    "/v3/api-docs/**",
////	        			    "/swagger-ui/**",
////	        			    "/swagger-ui.html",
////	        			    "/swagger-resources/**",
////	        			    "/webjars/**"
////	        			).permitAll().
////	        		requestMatchers("/api/customers/**").authenticated()
////	                .requestMatchers("/api/users/**").authenticated()
////	                .requestMatchers("/api/accounts/**").authenticated()
////	                .requestMatchers("/api/transactions/**").authenticated()
////
////	            .anyRequest().authenticated()
////	        );
////
////	    // Add your JWT filter **after** the authorization config
////	    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
////
////	    return http.build();
////	}
//
//
////	// Password encoder bean
////	@Bean
////	public PasswordEncoder passwordEncoder() {
////		return new BCryptPasswordEncoder();
////	}
////
////	// Expose AuthenticationManager bean (useful for manual auth in services)
////	@Bean
////	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
////		return config.getAuthenticationManager();
////	}
//}


package com.aurionpro.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aurionpro.security.JwtAuthenticationEntryPoint;
import com.aurionpro.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter authenticationFilter,
                          JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.authenticationFilter = authenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                .requestMatchers("/api/passbook/generate").authenticated()
                .requestMatchers("/api/customers/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/accounts/**").authenticated()
                .requestMatchers("/api/transactions/**").authenticated()

                .anyRequest().authenticated()
            );

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

   
}

