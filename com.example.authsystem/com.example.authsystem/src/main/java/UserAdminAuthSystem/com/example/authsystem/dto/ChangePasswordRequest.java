package UserAdminAuthSystem.com.example.authsystem.dto;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    // ✅ Default Constructor
    public ChangePasswordRequest() {
    }

    // ✅ Parameterized Constructor
    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // ✅ Getters and Setters
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
