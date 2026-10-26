package com.jobtrackr.auth;

import com.jobtrackr.auth.AuthDtos.*;
import com.jobtrackr.exception.EmailAlreadyUsedException;
import com.jobtrackr.exception.NotFoundException;
import com.jobtrackr.security.JwtService;
import com.jobtrackr.user.User;
import com.jobtrackr.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new EmailAlreadyUsedException(email);
        User saved = users.save(new User(email, encoder.encode(req.password()), req.fullName().trim()));
        return new AuthResponse(jwt.generateToken(saved.getEmail()), saved.getEmail(), saved.getFullName());
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));
        User user = users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return new AuthResponse(jwt.generateToken(user.getEmail()), user.getEmail(), user.getFullName());
    }
}
