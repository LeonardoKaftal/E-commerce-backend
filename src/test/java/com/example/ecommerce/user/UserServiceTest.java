package com.example.ecommerce.user;

import com.example.ecommerce.User.AppUser;
import com.example.ecommerce.User.AppUserRepository;
import com.example.ecommerce.User.UserService;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.exception.UsernameOrEmailAlreadyTakenException;
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



    @Test
    void shouldSearchUserByUsername() {
        String username = "luca";

        AppUser appUser = AppUser.builder()
                .username(username)
                .build();

        when(appUserRepository.findAppUserByUsername(username))
                .thenReturn(Optional.of(appUser));

        UserService underTest = new UserService(appUserRepository);
        var result = underTest.loadUserByUsername(username);

        verify(appUserRepository).findAppUserByUsername(username);
        assertThat(result).isEqualTo(appUser);
    }

    @Test
    void shouldThrowAnExceptionSearchingForNonExistingUser() {
        String username = "luca";

        when(appUserRepository.findAppUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("Username " + username + " has not been found"));

        UserService underTest = new UserService(appUserRepository);
        assertThatThrownBy(()-> underTest.loadUserByUsername(username))
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
                .build();

        when(appUserRepository.findAppUserByUsername(registerRequest.username()))
                .thenReturn(Optional.empty());
        when(appUserRepository.findAppUserByEmail(registerRequest.email()))
                .thenReturn(Optional.empty());

        when(appUserRepository.save(appUser))
                .thenReturn(appUser);

        UserService underTest = new UserService(appUserRepository);

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
        UserService underTest = new UserService(appUserRepository);

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
