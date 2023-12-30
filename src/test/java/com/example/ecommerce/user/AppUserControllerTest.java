package com.example.ecommerce.user;

import com.example.ecommerce.User.AppUser;
import com.example.ecommerce.User.UserController;
import com.example.ecommerce.User.UserService;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.exception.UsernameOrEmailAlreadyTakenException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AppUserControllerTest {

    @Mock
    private UserService userService;


    @Test
    void shouldGetUserByUsername() {
        String username = "Giulio";

        AppUser appUser = AppUser
                .builder()
                .username(username)
                .build();

        when(userService.loadUserByUsername(username)).thenReturn(appUser);
        UserController underTest = new UserController(userService);
        assertThat(underTest.getUserByUsername(username))
                .isEqualTo(ResponseEntity.ok(appUser));
    }

    @Test
    void shouldThrowAnExceptionSearchingForNonExistingUser() {
        String username = "Giulio";
        when(userService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("Username " + username + " has not been found"));
        UserController underTest = new UserController(userService);

        assertThatThrownBy(()-> underTest
                .getUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldReisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("a","a@a","password");
        AppUser appUser = AppUser
                .builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(registerRequest.password())
                .build();

        when(userService.registerUser(registerRequest))
                .thenReturn(appUser);


        UserController underTest = new UserController(userService);

        assertThat(underTest.registerUser(registerRequest))
                .isEqualTo(ResponseEntity.ok(appUser));
        verify(userService).registerUser(registerRequest);

    }

    @Test
    void shouldThrowAnExceptionRegisteringNullUser() {
        UserController underTest = new UserController(userService);
        RegisterRequest registerRequest = null;
        when(userService.registerUser(registerRequest))
                .thenThrow(new NullPointerException("User cannot be null"));
        assertThatThrownBy(()-> underTest.registerUser(registerRequest))
                .isInstanceOf(NullPointerException.class);

    }

    @Test
    void shouldThrowAnExceptionRegisteringUserWithAlreadyUsedCredentials() {
        RegisterRequest registerRequest = new RegisterRequest("a","a","a");
        when(userService.registerUser(registerRequest))
                .thenThrow(new UsernameOrEmailAlreadyTakenException("Username o email already taken"));
        UserController underTest = new UserController(userService);
        assertThatThrownBy(()-> underTest.registerUser(registerRequest))
                .isInstanceOf(UsernameOrEmailAlreadyTakenException.class);
        verify(userService).registerUser(registerRequest);
    }
}
