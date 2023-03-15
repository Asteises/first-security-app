package ru.asteises.firstsecurityapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.ldap.EmbeddedLdapServerContextSourceFactoryBean;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import ru.asteises.firstsecurityapp.service.PersonDetailsService;

import javax.sql.DataSource;

/**
 * Главный класс для настройки Spring Security.
 */
@EnableWebSecurity // Так Spring понимает что это класс конфигурации Security.
@EnableGlobalMethodSecurity(prePostEnabled = true) // Так мы включим авторизацию на уровне методов.
@RequiredArgsConstructor
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // конфигурируем сам Spring Security
        // конфигурируем авторизацию, запросы читаются сверху вниз.
        http.csrf().disable()// отключаем защиту от межсайтовой подделки запросов..
                .authorizeRequests() // указывает что все запросы будут проходить авторизацию
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/auth/registration", "/auth/login", "/error").permitAll() // для этого запроса разрешаем вход всем пользователям..
                .anyRequest().hasAnyRole("USER", "ADMIN") // для всех остальных страниц..
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

    /**
     * Если нужно игнорировать какие-то запросы, то указываем их в этом методе.
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/ignore1", "/ignore2");
    }

    // TODO
    @Bean
    public EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean() {
        EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean =
                EmbeddedLdapServerContextSourceFactoryBean.fromEmbeddedLdapServer();
        contextSourceFactoryBean.setPort(0);
        return contextSourceFactoryBean;
    }

    // TODO
    @Bean
    AuthenticationManager ldapAuthenticationManager(
            BaseLdapPathContextSource contextSource) {
        LdapBindAuthenticationManagerFactory factory =
                new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserDnPatterns("uid={0},ou=people");
        factory.setUserDetailsContextMapper(new PersonContextMapper());
        return factory.createAuthenticationManager();
    }

    // TODO
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    // TODO
    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.createUser(user);
        return users;
    }

    // TODO
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
