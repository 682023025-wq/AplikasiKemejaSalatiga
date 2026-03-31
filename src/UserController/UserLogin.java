package UserController;

import Koneksi.DBKoneksi;
import UserModel.ModelUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserLogin {

    private static ModelUser sessionUser = null;

    public static ModelUser login(String username, String password) throws Exception {
        ModelUser user = null;
        try (Connection conn = DBKoneksi.getConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new ModelUser();
                user.setId(rs.getString("id"));
                user.setAuthType(rs.getString("auth_type"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                
                setSessionUser(user);
            }

            rs.close();
            ps.close();
        }
        return user;
    }

    public static void setSessionUser(ModelUser user) {
        sessionUser = user;
    }

    public static ModelUser getSessionUser() {
        return sessionUser;
    }

    public static void logout() {
        sessionUser = null;
    }
    
    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] result = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 8); // pendek, hanya 8 karakter pertama
        } catch (Exception e) {
            throw new RuntimeException("Gagal hashing password: " + e.getMessage());
        }
    }
}
