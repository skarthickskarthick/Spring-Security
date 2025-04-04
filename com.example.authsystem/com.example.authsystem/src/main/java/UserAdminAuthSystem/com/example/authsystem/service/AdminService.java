package UserAdminAuthSystem.com.example.authsystem.service;

import UserAdminAuthSystem.com.example.authsystem.entity.User;
import UserAdminAuthSystem.com.example.authsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor (Manual Dependency Injection)
    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            user.setRole(userDetails.getRole());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
