package UserAdminAuthSystem.com.example.authsystem.service;

import UserAdminAuthSystem.com.example.authsystem.dto.AuthenticationRequest;
import UserAdminAuthSystem.com.example.authsystem.dto.AuthenticationResponse;
import UserAdminAuthSystem.com.example.authsystem.dto.RegisterRequest;
import UserAdminAuthSystem.com.example.authsystem.dto.ChangePasswordRequest;
import UserAdminAuthSystem.com.example.authsystem.entity.User;
import UserAdminAuthSystem.com.example.authsystem.repository.UserRepository;
import UserAdminAuthSystem.com.example.authsystem.security.CustomUserDetailsService;
import UserAdminAuthSystem.com.example.authsystem.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 CustomUserDetailsService userDetailsService,
                                 JwtUtil jwtUtil,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println("🔹 Attempting login for: " + request.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        System.out.println("🔹 Stored Password (Encoded): " + passwordEncoder.encode(userDetails.getPassword()));
        System.out.println("🔹 Entered Password: " + request.getPassword());

        // ✅ Secure password verification
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            System.out.println("❌ Password mismatch!");
            throw new RuntimeException("Invalid username or password");
        }

        // ✅ Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        System.out.println("✅ Authentication successful!");

        // ✅ Generate tokens
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    // ✅ Refresh token logic
    public AuthenticationResponse refreshAccessToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            return new AuthenticationResponse(newAccessToken, refreshToken);
        }
        throw new RuntimeException("Invalid refresh token");
    }

    // ✅ Register a new user
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(
                null, // ID auto-generated
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // ✅ Always encode passwords
                request.getRole()
        );

        userRepository.save(user);
        return "User registered successfully";
    }

    // ✅ Change password securely
    public String changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }
}
