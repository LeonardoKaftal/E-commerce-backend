package com.example.ecommerce.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyPasswordEncoder {
    
    public final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    
    public String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
