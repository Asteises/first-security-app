package ru.asteises.firstsecurityapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.asteises.firstsecurityapp.models.Person;

import java.util.Collection;

@RequiredArgsConstructor
public class PersonDetails implements UserDetails {

    private final Person person;

    /**
     * Список прав пользователя.
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.person.getPassword();
    }

    // Любое поле Person
    @Override
    public String getUsername() {
        return this.person.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Нужно чтобы получать данные аутентифицированного пользователя.
     * @return
     */
    public Person getPerson() {
        return this.person;
    }
}
