package com.example.ecommerce.user;

import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.exception.UsernameOrEmailAlreadyTakenException;
import com.example.ecommerce.security.MyPasswordEncoder;
import com.example.ecommerce.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    MyPasswordEncoder passwordEncoder;

    @Test
    void shouldSearchByEmail() {
        String email = "luca@mail.com";

        AppUser appUser = AppUser.builder()
                .email(email)
                .build();

        when(appUserRepository.findAppUserByEmail(appUser.getEmail()))
                .thenReturn(Optional.of(appUser));


        UserService underTest = new UserService(appUserRepository,null);
        var result = underTest.loadUserByUsername(appUser.getEmail());

        verify(appUserRepository).findAppUserByEmail(appUser.getEmail());
        assertThat(result).isEqualTo(appUser);
    }

    @Test
    void shouldThrowAnExceptionSearchingForNonExistingUser() {
        String email = "luca@email.com";

        when(appUserRepository.findAppUserByEmail(email))
                .thenThrow(new UsernameNotFoundException("Email " + email + " has not been found"));

        UserService underTest = new UserService(appUserRepository,null);
        assertThatThrownBy(()-> underTest.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("a","a@a","a");
        AppUser appUser = AppUser
                .builder()
                .email(registerRequest.email())
                .password(registerRequest.password())
                .username(registerRequest.username())
                .accountCreationTime(LocalDate.now())
                .role(Role.USER)
                .isEnabled(true)
                .build();

        when(appUserRepository.findAppUserByUsername(registerRequest.username()))
                .thenReturn(Optional.empty());
        when(appUserRepository.findAppUserByEmail(registerRequest.email()))
                .thenReturn(Optional.empty());

        when(appUserRepository.save(appUser))
                .thenReturn(appUser);

        when(passwordEncoder.encode(appUser.getPassword()))
                .thenReturn(appUser.getPassword());

        UserService underTest = new UserService(appUserRepository,passwordEncoder);

        assertThat(underTest.registerUser(registerRequest))
                .isEqualTo(appUser);

        verify(appUserRepository).findAppUserByUsername(registerRequest.username());
        verify(appUserRepository).findAppUserByEmail(registerRequest.email());
    }

    @Test
    void shouldThrowAnExceptionRegisteringInvalidUser() {
        RegisterRequest registerRequest = new RegisterRequest("a","a@a","a");
        when(appUserRepository.findAppUserByUsername(registerRequest.username()))
                .thenThrow(new UsernameOrEmailAlreadyTakenException("Username " + registerRequest.username() + " has already been taken"));


        UserService underTest = new UserService(appUserRepository,null);

        assertThatThrownBy(()-> underTest.registerUser(registerRequest))
                .isInstanceOf(UsernameOrEmailAlreadyTakenException.class);

        Mockito.reset(appUserRepository);
        when(appUserRepository.findAppUserByUsername(registerRequest.username()))
                .thenReturn(Optional.empty());
        when(appUserRepository.findAppUserByEmail(registerRequest.email()))
                .thenThrow(new UsernameOrEmailAlreadyTakenException("Email " + registerRequest.email() + " has already been taken"));

        final UserService finalUnderTest = underTest;
        assertThatThrownBy(()-> finalUnderTest.registerUser(registerRequest))
                .isInstanceOf(UsernameOrEmailAlreadyTakenException.class);
    }
}
