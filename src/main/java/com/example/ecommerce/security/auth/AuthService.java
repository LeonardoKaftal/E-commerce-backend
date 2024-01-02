package com.example.ecommerce.security.auth;

import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.security.jwt.TokenService;
import com.example.ecommerce.user.UserController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final UserController userController;
    private final AuthenticationManager authenticationManager;

    public String registerUser(RegisterRequest registerRequest) {
        userController.registerUser(registerRequest);
        return tokenService.generateToken(new UsernamePasswordAuthenticationToken(registerRequest.email(), registerRequest.password()));
    }

    public String authenticate(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));
        return tokenService.generateToken(auth);
    }
}
