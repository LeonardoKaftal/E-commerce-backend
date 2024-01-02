package com.example.ecommerce.user;

import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.exception.UsernameOrEmailAlreadyTakenException;
import com.example.ecommerce.security.MyPasswordEncoder;
import com.example.ecommerce.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final MyPasswordEncoder passwordEncoder;

    @Override
    public AppUser loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return appUserRepository.findAppUserByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Email " + email + " has not been found"));
    }

    public AppUser registerUser(@NonNull RegisterRequest registerRequest) {
        boolean takenUsername = appUserRepository.findAppUserByUsername(registerRequest.username()).isPresent();
        boolean takenEmail = appUserRepository.findAppUserByEmail(registerRequest.email()).isPresent();

        if (takenUsername)
            throw new UsernameOrEmailAlreadyTakenException("Username " + registerRequest.username() + " has already been taken");

        if (takenEmail)
            throw new UsernameOrEmailAlreadyTakenException("Email " + registerRequest.email() + " has already been taken");

        AppUser appUser = AppUser
                .builder()
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .username(registerRequest.username())
                .accountCreationTime(LocalDate.now())
                .role(Role.USER)
                .isEnabled(true)
                .build();

        return appUserRepository.save(appUser);
    }
}
