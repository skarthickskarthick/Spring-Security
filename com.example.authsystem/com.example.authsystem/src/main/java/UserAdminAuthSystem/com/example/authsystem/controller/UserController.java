package UserAdminAuthSystem.com.example.authsystem.controller;

import UserAdminAuthSystem.com.example.authsystem.entity.User;
import UserAdminAuthSystem.com.example.authsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserProfile(userDetails.getUsername()));
    }
}
