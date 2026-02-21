package com.example.demo.resolver;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.security.JwtService;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.service.UserService;

@Controller
public class AuthResolver {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthResolver(UserService userService,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @MutationMapping
    public LoginResponse login(@Argument String username, @Argument String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        Authentication result = authenticationManager.authenticate(auth);

        List<String> roles = result.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        String token = jwtService.generateToken(result.getName(), roles);
        String role = roles.isEmpty() ? "USER" : roles.get(0).replace("ROLE_", "");
        return new LoginResponse(token, username, role);
    }

    @MutationMapping
    public String register(@Argument String username, @Argument String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        userService.register(request);
        return "User registered successfully";
    }

    @MutationMapping
    public String registerAdmin(@Argument String username, @Argument String password, @Argument String adminToken) {
        // Verificar que el token sea válido (usando el secret del JWT)
        if (adminToken == null || !adminToken.equals("admin-secret-token")) {
            throw new IllegalArgumentException("Invalid admin token");
        }

        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        userService.registerAdmin(request);
        return "Admin registered successfully";
    }

    @MutationMapping
    public String logout(@Argument String token) {
        if (token != null && !token.isEmpty()) {
            tokenBlacklistService.addToBlacklist(token);
            return "Logged out successfully";
        }
        throw new IllegalArgumentException("Token is required");
    }
}
