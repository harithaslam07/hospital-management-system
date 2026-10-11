package com.example.hospital_management.security;

import com.example.hospital_management.entity.Role;
import com.example.hospital_management.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    @Getter
    private final Long userId;
    private final String email;
    private final String passwordHash;
    @Getter
    private final Role role;

    public AppUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPassword();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}