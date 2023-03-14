package ru.asteises.firstsecurityapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.asteises.firstsecurityapp.service.PersonDetailsService;

/**
 * Главный класс для настройки Spring Security.
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // конфигурируем сам Spring Security
        // конфигурируем авторизацию, запросы читаются сверху вниз.
        http.csrf().disable()// отключаем защиту от межсайтовой подделки запросов..
                .authorizeRequests() // указывает что все запросы будут проходить авторизацию
                .antMatchers("/auth/registration", "/auth/login", "/error").permitAll() // для этого запроса разрешаем вход всем пользователям..
                .anyRequest().authenticated() // для всех остальных нужна авторизация..
                .and() // логическое И - объединят блоки разной логики..
                .formLogin()// Настраиваем страницу авторизации
                .loginPage("/auth/login") // адрес новой странички login..
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/hello", true) // после успешной авторизации отправлять на страницу по умолчанию..
                .failureUrl("/auth/login?error") // в случае неудачной авторизации показать ошибку..
                .and()
                .logout()
                .logoutUrl("/logout") // при переходе на эту страницу у пользователя очистится информация о приложении в cookies и у приложения очистится информация о пользователе..
                .logoutSuccessUrl("/auth/logout"); // в случае успеха пользователь перейдет на эту страничку..

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Настраиваем аутентификацию, приходит Authentication - Credentials, возвращаем Authentication - Principal.
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            PersonDetailsService personDetailsService)
            throws Exception {

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(personDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }
}
