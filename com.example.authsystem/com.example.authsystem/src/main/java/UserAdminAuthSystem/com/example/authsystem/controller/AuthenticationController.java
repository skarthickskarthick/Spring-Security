package UserAdminAuthSystem.com.example.authsystem.controller;

import UserAdminAuthSystem.com.example.authsystem.dto.AuthenticationRequest;
import UserAdminAuthSystem.com.example.authsystem.dto.AuthenticationResponse;
import UserAdminAuthSystem.com.example.authsystem.dto.RegisterRequest;
import UserAdminAuthSystem.com.example.authsystem.dto.ChangePasswordRequest;
import UserAdminAuthSystem.com.example.authsystem.security.JwtUtil;
import UserAdminAuthSystem.com.example.authsystem.service.AuthenticationService;
import UserAdminAuthSystem.com.example.authsystem.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    JwtUtil jwtUtil,
                                    TokenBlacklistService tokenBlacklistService) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // ✅ Register New User
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully."));
    }

    // ✅ Login - Returns Access & Refresh Token
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        System.out.println("controler of login");
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    // ✅ Change Password
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

      // ✅ Logout (Blacklist token)
      @PostMapping("/logout")
      public ResponseEntity<String> logout(HttpServletRequest request) {
          String authorizationHeader = request.getHeader("Authorization");

          if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
              String token = authorizationHeader.substring(7);
              tokenBlacklistService.blacklistToken(token);

              SecurityContextHolder.clearContext();
              return ResponseEntity.ok("Successfully logged out.");
          }
          return ResponseEntity.badRequest().body("Invalid token.");
      }
    // ✅ Refresh Access Token using Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestParam String refreshToken) {
        AuthenticationResponse response = authenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}