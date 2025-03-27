package UserAdminAuthSystem.com.example.authsystem.controller;

import UserAdminAuthSystem.com.example.authsystem.dto.AuthenticationRequest;
import UserAdminAuthSystem.com.example.authsystem.dto.AuthenticationResponse;
import UserAdminAuthSystem.com.example.authsystem.dto.ChangePasswordRequest;
import UserAdminAuthSystem.com.example.authsystem.entity.User;
import UserAdminAuthSystem.com.example.authsystem.security.JwtUtil;
import UserAdminAuthSystem.com.example.authsystem.service.AuthenticationService;
import UserAdminAuthSystem.com.example.authsystem.service.TokenBlacklistService;
import UserAdminAuthSystem.com.example.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserService userService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    JwtUtil jwtUtil,
                                    TokenBlacklistService tokenBlacklistService,
                                    UserService userService) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                      HttpServletRequest request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid token format"));
        }
        String jwtToken = authHeader.substring(7);
        Instant expirationTime = jwtUtil.extractExpiration(jwtToken);
        tokenBlacklistService.blacklistToken(jwtToken, expirationTime);

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok("Password changed successfully.");
    }
}
