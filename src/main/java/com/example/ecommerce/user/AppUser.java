package com.example.ecommerce.user;

import com.example.ecommerce.security.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AppUser implements UserDetails {
    @Id
    @SequenceGenerator(
            name = "app_user_sequence",
            sequenceName = "app_user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "app_user_sequence"
    )
    private Integer id;
    private String username;
    private String email;
    private String password;
    private LocalDate accountCreationTime;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isAccountExpired = false;
    private boolean isAccountLocked = false;
    private boolean credentialsExpired = false;
    private boolean isEnabled = true;

    public AppUser(Integer id, String username, String email, String password, LocalDate accountCreationTime, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountCreationTime = accountCreationTime;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
