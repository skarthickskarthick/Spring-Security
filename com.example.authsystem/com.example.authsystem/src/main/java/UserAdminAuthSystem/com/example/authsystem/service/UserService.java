package UserAdminAuthSystem.com.example.authsystem.service;

import UserAdminAuthSystem.com.example.authsystem.dto.ChangePasswordRequest;
import UserAdminAuthSystem.com.example.authsystem.entity.Role;
import UserAdminAuthSystem.com.example.authsystem.entity.User;
import UserAdminAuthSystem.com.example.authsystem.repository.UserRepository;
import jakarta.transaction.Transactional; // ✅ Fixed Import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Constructor Injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Register a New User
    @Transactional
    public User registerUser(String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) { // ✅ Optimized Check
            throw new RuntimeException("Username is already taken.");
        }

        // ✅ Ensure role is not null, default to USER
        if (role == null) {
            role = Role.USER; // Adjust based on your project’s role setup
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        System.out.println("Saving user: " + user);
        return userRepository.save(user);
    }

    // ✅ Get User Profile
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Get List of All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Check if a User Exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ✅ Change Password
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password before updating
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect.");
        }

        // Save new encoded password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ✅ Update Password Directly (For Internal Use)
    @Transactional
    public void updatePassword(String username, String newEncodedPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(newEncodedPassword);
        userRepository.save(user);
    }
}
