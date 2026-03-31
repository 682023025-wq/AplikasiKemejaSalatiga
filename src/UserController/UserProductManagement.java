package UserController;

import Koneksi.DBKoneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import UserModel.ModelProductManagement;

public class UserProductManagement {

    public List<ModelProductManagement> getAllProducts() {
        List<ModelProductManagement> list = new ArrayList<>();

        String query = """
            SELECT p.id_produk AS Id, p.nama AS Nama, dk.kategori AS Kategori,
                   du.ukuran AS Ukuran, p.harga AS Harga, p.stok AS Stok, p.gambar
            FROM detail_kategori dk
            JOIN produk p ON dk.id_kategori = p.detail_kategori_id_kategori
            JOIN detail_ukuran du ON du.id_ukuran = p.detail_ukuran_id_ukuran
            ORDER BY p.id_produk ASC
        """;

        try (Connection conn = DBKoneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ModelProductManagement product = new ModelProductManagement();
                product.setId(rs.getString("Id"));
                product.setNama(rs.getString("Nama"));
                product.setKategori(rs.getString("Kategori"));
                product.setUkuran(rs.getString("Ukuran"));
                product.setHarga(rs.getInt("Harga"));
                product.setStok(rs.getInt("Stok"));
                product.setGambar(rs.getBlob("gambar"));
                list.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ModelProductManagement> Nama_Controller(String Nama) {
        List<ModelProductManagement> hasil = new ArrayList<>();

        String query = """
            SELECT p.id_produk AS Id, p.nama AS Nama, dk.kategori AS Kategori,
                   du.ukuran AS Ukuran, p.harga AS Harga, p.stok AS Stok
            FROM detail_kategori dk
            JOIN produk p ON dk.id_kategori = p.detail_kategori_id_kategori
            JOIN detail_ukuran du ON du.id_ukuran = p.detail_ukuran_id_ukuran
            WHERE p.nama LIKE ?
            ORDER BY p.id_produk ASC
        """;

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + Nama + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ModelProductManagement produk = new ModelProductManagement();
                    produk.setId(rs.getString("Id"));
                    produk.setNama(rs.getString("Nama"));
                    produk.setKategori(rs.getString("Kategori"));
                    produk.setUkuran(rs.getString("Ukuran"));
                    produk.setHarga(rs.getInt("Harga"));
                    produk.setStok(rs.getInt("Stok"));
                    hasil.add(produk);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasil;
    }

    // Controller 3 - get id ukuran
    public String getIdUkuran(String ukuran) {
        String idUkuran = null;
        String sql = "SELECT id_ukuran FROM detail_ukuran WHERE ukuran = ?";

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ukuran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idUkuran = rs.getString("id_ukuran");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idUkuran;
    }

    // Controller 4 - get id kategori
    public String getIdKategori(String kategori) {
        String idKategori = null;
        String sql = "SELECT id_kategori FROM detail_kategori WHERE kategori = ?";

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kategori);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idKategori = rs.getString("id_kategori");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idKategori;
    }

    public boolean saveProduct(ModelProductManagement product) {
        String sql = """
            INSERT INTO produk (id_produk, nama, detail_kategori_id_kategori,
                                detail_ukuran_id_ukuran, harga, stok)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        String idKategori = getIdKategori(product.getKategori());
        String idUkuran = getIdUkuran(product.getUkuran());

        if (idKategori == null || idUkuran == null) {
            System.err.println("Kategori atau Ukuran tidak valid!");
            return false;
        }

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getId());
            ps.setString(2, product.getNama());
            ps.setString(3, idKategori);
            ps.setString(4, idUkuran);
            ps.setInt(5, product.getHarga());
            ps.setInt(6, product.getStok());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Controller 6 - update product
    public boolean updateProduct(ModelProductManagement product) {
        String sql = """
            UPDATE produk 
            SET nama = ?, detail_kategori_id_kategori = ?, detail_ukuran_id_ukuran = ?, 
                harga = ?, stok = ?
            WHERE id_produk = ?
        """;

        String idKategori = getIdKategori(product.getKategori());
        String idUkuran = getIdUkuran(product.getUkuran());

        if (idKategori == null || idUkuran == null) {
            System.err.println("Kategori atau Ukuran tidak valid!");
            return false;
        }

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getNama());
            ps.setString(2, idKategori);
            ps.setString(3, idUkuran);
            ps.setInt(4, product.getHarga());
            ps.setInt(5, product.getStok());
            ps.setString(6, product.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Controller 7 - delete product
    public boolean deleteProduct(String id) {
        String sql = "DELETE FROM produk WHERE id_produk = ?";

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Controller 8 - auto increment ID
    public String generateNextProductId() {
        String lastId = null;
        String nextId = "P001";

        String sql = "SELECT id_produk FROM produk ORDER BY id_produk DESC LIMIT 1";

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                lastId = rs.getString("id_produk");
                int number = Integer.parseInt(lastId.substring(1));
                nextId = String.format("P%03d", number + 1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextId;
    }

    // Controller 9 - delete image
    public boolean deleteImageById(String idProduk) {
        String sql = "UPDATE produk SET gambar = NULL WHERE id_produk = ?";

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idProduk);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Controller 10 - update image
    public ModelProductManagement updateProductImage(String idProduk, byte[] imageBytes) {
        String updateSql = "UPDATE produk SET gambar = ? WHERE id_produk = ?";
        String selectSql = """
            SELECT p.id_produk AS Id, p.nama AS Nama, dk.kategori AS Kategori,
                   du.ukuran AS Ukuran, p.harga AS Harga, p.stok AS Stok, p.gambar
            FROM detail_kategori dk
            JOIN produk p ON dk.id_kategori = p.detail_kategori_id_kategori
            JOIN detail_ukuran du ON du.id_ukuran = p.detail_ukuran_id_ukuran
            WHERE p.id_produk = ?
        """;

        ModelProductManagement updatedProduct = null;

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            updateStmt.setBytes(1, imageBytes);
            updateStmt.setString(2, idProduk);
            updateStmt.executeUpdate();

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, idProduk);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        updatedProduct = new ModelProductManagement();
                        updatedProduct.setId(rs.getString("Id"));
                        updatedProduct.setNama(rs.getString("Nama"));
                        updatedProduct.setKategori(rs.getString("Kategori"));
                        updatedProduct.setUkuran(rs.getString("Ukuran"));
                        updatedProduct.setHarga(rs.getInt("Harga"));
                        updatedProduct.setStok(rs.getInt("Stok"));
                        updatedProduct.setGambar(rs.getBlob("gambar"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updatedProduct;
    }
}
