package UserController;

import Koneksi.DBKoneksi;
import UserModel.ModelTambahAkun;
import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTambahAkun {

    // Controller 1 - Simpan User Baru
    public boolean saveUser(ModelTambahAkun user) {
        String sql = """
            INSERT INTO user (id, auth_type, username, password, gambar)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getAuthType());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setBytes(5, user.getGambar());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Controller 2 - Update User Berdasarkan ID
    public boolean updateUser(ModelTambahAkun user) {
    String sqlWithImage = "UPDATE user SET auth_type = ?, username = ?, password = ?, gambar = ? WHERE id = ?";
    String sqlWithoutImage = "UPDATE user SET auth_type = ?, username = ?, password = ? WHERE id = ?";

    try (Connection conn = DBKoneksi.getConnection()) {
        PreparedStatement ps;

        if (user.getGambar() != null) {
            ps = conn.prepareStatement(sqlWithImage);
            ps.setString(1, user.getAuthType());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setBytes(4, user.getGambar());
            ps.setString(5, user.getId());
        } else {
            ps = conn.prepareStatement(sqlWithoutImage);
            ps.setString(1, user.getAuthType());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getId());
        }

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // Controller 3 - Hapus User Berdasarkan ID
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM user WHERE id = ?";

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Controller 4 - Tampilkan Semua Data ke JTable
 

    // Controller 5 - Cari User Berdasarkan Username
   public void searchUser(JTable table, String keyword) {
    String sql = """
        SELECT id, auth_type, username, password FROM user
        WHERE username LIKE ? ORDER BY id ASC
    """;

    try (Connection conn = DBKoneksi.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Role", "Username", "Password"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // semua sel tidak bisa diedit
            }
        };

        while (rs.next()) {
            Object[] row = {
                rs.getString("id"),
                rs.getString("auth_type"),
                rs.getString("username"),
                rs.getString("password")
            };
            model.addRow(row);
        }

        table.setModel(model);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    
    //Controller 6 - penomoran otomatis
  public String generateAutoID() {
    String newId = "U001"; // Default jika belum ada data
    String sql = "SELECT id FROM user ORDER BY id DESC LIMIT 1"; // ✔ MySQL compatible

    try (Connection conn = DBKoneksi.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            String lastId = rs.getString("id").substring(1); // ambil angka saja, hilangkan 'U'
            int nextId = Integer.parseInt(lastId) + 1;
            newId = String.format("U%03d", nextId); // format jadi U001, U002, dst.
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return newId;
}


// Controller 7 - Cek apakah user dengan ID tertentu sudah ada
public boolean isUserExist(String id) {
    String sql = "SELECT id FROM user WHERE id = ?";

    try (Connection conn = DBKoneksi.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.next(); // true jika ditemukan

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Controller 8 - Ambil gambar user berdasarkan ID
public byte[] getGambarById(String id) {
    String sql = "SELECT gambar FROM user WHERE id = ?";
    try (Connection conn = DBKoneksi.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getBytes("gambar"); // bisa null kalau tidak ada gambar
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
}
