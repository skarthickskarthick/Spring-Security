package UserAdminAuthSystem.com.example.authsystem.service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token) {
        blacklist.put(token, Instant.now().plusSeconds(3600)); // Assume token expires in 1 hour
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}
