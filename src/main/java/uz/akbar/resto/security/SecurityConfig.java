package uz.akbar.resto.security;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.security.jwt.JwtAuthenticationEntryPoint;
import uz.akbar.resto.security.jwt.JwtAuthenticationFilter;
import uz.akbar.resto.utils.Utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/** SecurityConfig */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	public static final String[] AUTH_WHITELIST = {
			Utils.BASE_URL + "/auth/*",
			"/v3/api-docs/**",
			"/swagger-ui/**",
			"/swagger-ui.html",
	};

	@Bean
	public AuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setHideUserNotFoundExceptions(false);

		return authenticationProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				authorizationManagerRequestMatcherRegistry -> {
					authorizationManagerRequestMatcherRegistry
							.requestMatchers(AUTH_WHITELIST)
							.permitAll()
							.anyRequest()
							.authenticated();
				})
				.addFilterBefore(
						jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		http.httpBasic(Customizer.withDefaults());

		http.csrf(csrf -> csrf.ignoringRequestMatchers(Utils.BASE_URL + "/attach/upload"));
		// http.csrf(Customizer.withDefaults()); // csrf yoqilgan
		http.csrf(AbstractHttpConfigurer::disable); // csrf o'chirilgan

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.cors(
				httpSecurityCorsConfigurer -> { // cors konfiguratsiya qilingan
					CorsConfiguration configuration = new CorsConfiguration();

					configuration.setAllowedOriginPatterns(Arrays.asList("*"));
					configuration.setAllowedMethods(Arrays.asList("*"));
					configuration.setAllowedHeaders(Arrays.asList("*"));

					UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
					source.registerCorsConfiguration("/**", configuration);
					httpSecurityCorsConfigurer.configurationSource(source);
				});

		http.exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint));

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
			throws Exception {
		return configuration.getAuthenticationManager();
	}
}
