package CustomerController;

import Koneksi.DBKoneksi;
import CustomerModel.CustomerModelCustomer;
import java.sql.*;
import java.util.UUID;
import javax.swing.JOptionPane;

public class CustomerLogin {

    public static CustomerModelCustomer sessionCustomer = null;

    public static boolean isLoggedIn() {
        return sessionCustomer != null;
    }

    public static void logout() {
        sessionCustomer = null;
    }

    public static boolean login(String username, String password) {
        try {
            Connection conn = DBKoneksi.getConnection();
            String query = "SELECT * FROM customer WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Akun tidak ditemukan.\nSilakan daftar terlebih dahulu.");
                return false;
            } else {
                String dbPassword = rs.getString("password");

                if (dbPassword == null || !dbPassword.equals(password)) {
                    JOptionPane.showMessageDialog(null, "Password salah.");
                    return false;
                }

                CustomerModelCustomer customer = new CustomerModelCustomer();
                customer.setId(rs.getString("id"));
                customer.setAuthType(rs.getString("auth_type"));
                customer.setGoogleName(rs.getString("google_name"));
                customer.setPassword(rs.getString("password"));
                customer.setUsername(rs.getString("username"));
                customer.setAlamat(rs.getString("alamat"));
                customer.setCreatedAt(rs.getTimestamp("create_at"));

                sessionCustomer = customer;

                JOptionPane.showMessageDialog(null, "Login berhasil.\nSelamat datang, " + customer.getUsername() + "!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat login: " + e.getMessage());
            return false;
        }
    }

    public static boolean isUsernameExist(String username) {
        try {
            Connection conn = DBKoneksi.getConnection();
            String query = "SELECT id FROM customer WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isEmailExist(String email) {
        try {
            Connection conn = DBKoneksi.getConnection();
            String query = "SELECT id FROM customer WHERE google_name = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return true; 
        }
    }

    public static boolean daftarCustomerBaru(String username, String password, String konfirmasiPassword, String email, String alamat) {
        if (isUsernameExist(username)) {
            JOptionPane.showMessageDialog(null, "Username sudah digunakan.");
            return false;
        }

        if (isEmailExist(email)) {
            JOptionPane.showMessageDialog(null, "Email sudah digunakan.");
            return false;
        }

        try {
            Connection conn = DBKoneksi.getConnection();
            String id = generateIdCustomer();
            String sql = "INSERT INTO customer (id, username, password, alamat, auth_type, google_name, create_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, alamat);
            ps.setString(5, "local");
            ps.setString(6, email);
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            int inserted = ps.executeUpdate();
            return inserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

public static boolean loginDenganGoogleSementara(String username, String email) {
    try {
        Connection conn = DBKoneksi.getConnection();

        String checkQuery = "SELECT * FROM customer WHERE google_name = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, email);
        ResultSet rs = checkStmt.executeQuery();

        CustomerModelCustomer customer = new CustomerModelCustomer();

        if (rs.next()) {
            customer.setId(rs.getString("id"));
            customer.setAuthType(rs.getString("auth_type"));
            customer.setGoogleName(rs.getString("google_name"));
            customer.setPassword(rs.getString("password"));
            customer.setUsername(rs.getString("username"));
            customer.setAlamat(rs.getString("alamat"));
            customer.setCreatedAt(rs.getTimestamp("create_at"));
        } else {
            String id = generateIdCustomer();
            String insertQuery = "INSERT INTO customer (id, username, password, alamat, auth_type, google_name, create_at) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, id);
            insertStmt.setString(2, username);
            insertStmt.setString(3, null); 
            insertStmt.setString(4, null); 
            insertStmt.setString(5, "google");
            insertStmt.setString(6, email);
            insertStmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            insertStmt.executeUpdate();

            customer.setId(id);
            customer.setAuthType("google");
            customer.setGoogleName(email);
            customer.setPassword(null);
            customer.setUsername(username);
            customer.setAlamat(null);
            customer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        sessionCustomer = customer;
        JOptionPane.showMessageDialog(null, "Login Google berhasil.\nSelamat datang, " + username + "!");
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Gagal login dengan Google: " + e.getMessage());
        return false;
    }
}
public static String hashPassword(String password) {
    try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
        byte[] result = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().substring(0, 8);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    private static String generateIdCustomer() {
        return "CUST" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
