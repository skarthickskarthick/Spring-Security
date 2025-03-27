package UserAdminAuthSystem.com.example.authsystem.service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final ConcurrentHashMap<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklistToken(String token, Instant expirationTime) {
        blacklistedTokens.put(token, expirationTime);
    }

    public boolean isTokenBlacklisted(String token) {
        Instant expiration = blacklistedTokens.get(token);
        if (expiration == null) {
            return false; // Token is not blacklisted
        }
        // 🚨 Remove expired tokens from blacklist
        if (Instant.now().isAfter(expiration)) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true; // Token is blacklisted
    }
}
