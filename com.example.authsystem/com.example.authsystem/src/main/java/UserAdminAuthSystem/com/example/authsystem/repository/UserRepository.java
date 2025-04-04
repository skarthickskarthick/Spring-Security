package UserAdminAuthSystem.com.example.authsystem.repository;



import UserAdminAuthSystem.com.example.authsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // ✅ Check if username already exists
    boolean existsByUsername(String username);
}
