package UserModel;

public class ModelUser {
    private String id;
    private String authType;
    private String username;
    private String password;

    public ModelUser() {}

    public ModelUser(String id, String authType, String username, String password) {
        this.id = id;
        this.authType = authType;
        this.username = username;
        this.password = password;
    }

    // Getter & Setter ...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
