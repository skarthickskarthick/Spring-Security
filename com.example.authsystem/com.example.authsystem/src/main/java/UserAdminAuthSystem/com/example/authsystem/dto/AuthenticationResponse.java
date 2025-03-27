package UserAdminAuthSystem.com.example.authsystem.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public class AuthenticationResponse {
    private String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
