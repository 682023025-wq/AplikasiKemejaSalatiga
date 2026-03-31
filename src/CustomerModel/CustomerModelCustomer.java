package CustomerModel;

import java.sql.Timestamp;

public class CustomerModelCustomer {
    private String id;
    private String username;
    private String password;
    private String alamat;
    private String authType;
    private String googleName; 
    private String googlePhotoUrl;
    private Timestamp createdAt;

    public CustomerModelCustomer() {}

    public CustomerModelCustomer(String id, String username, String password, String alamat,
                         String authType, String googleName, String googlePhotoUrl, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.alamat = alamat;
        this.authType = authType;
        this.googleName = googleName;
        this.googlePhotoUrl = googlePhotoUrl;
        this.createdAt = createdAt;
    }

    // === Getter & Setter ===
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }

    public String getGoogleName() { return googleName; }
    public void setGoogleName(String googleName) { this.googleName = googleName; }

    public String getGooglePhotoUrl() { return googlePhotoUrl; }
    public void setGooglePhotoUrl(String googlePhotoUrl) { this.googlePhotoUrl = googlePhotoUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
