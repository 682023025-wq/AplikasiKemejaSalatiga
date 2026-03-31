package UserController;

import Koneksi.DBKoneksi;
import UserModel.ModelProduk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class UserProduk {

    public static List<ModelProduk> cariProduk(String keyword) throws Exception {
        List<ModelProduk> produkList = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT p.id_produk, p.nama, dk.kategori AS kategori, du.ukuran AS ukuran, p.harga, p.stok " +
            "FROM produk p " +
            "JOIN detail_kategori dk ON p.detail_kategori_id_kategori = dk.id_kategori " +
            "JOIN detail_ukuran du ON p.detail_ukuran_id_ukuran = du.id_ukuran "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE p.id_produk LIKE ? OR p.nama LIKE ? OR dk.kategori LIKE ? OR du.ukuran LIKE ? ");
        }


        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (hasKeyword) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ModelProduk mp = new ModelProduk();
                    mp.setIdProduk(rs.getString("id_produk"));
                    mp.setNama(rs.getString("nama"));
                    mp.setKategori(rs.getString("kategori"));
                    mp.setUkuran(rs.getString("ukuran"));
                    mp.setHarga(rs.getLong("harga"));
                    mp.setStok(rs.getInt("stok"));
                    produkList.add(mp);
                }
            }
        }

        return produkList;
    }

    public static void loadProdukTable(JTable tabel, String keyword) {
        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        model.setRowCount(0);

        try {
            List<ModelProduk> produkList = cariProduk(keyword);
            int no = 1;
            for (ModelProduk p : produkList) {
                model.addRow(new Object[]{
                    no++,
                    p.getIdProduk(),
                    p.getNama(),
                    p.getKategori(),
                    p.getUkuran(),
                    p.getHarga(),
                    p.getStok()
                });
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(tabel, "Error: " + e.getMessage());
        }
    }

    public static int cekStokProduk(String idProduk) {
        int stok = 0;

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT stok FROM produk WHERE id_produk = ?")) {

            ps.setString(1, idProduk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stok = rs.getInt("stok");
                }
            }
        } catch (Exception e) {
            System.err.println("Error cekStokProduk: " + e.getMessage());
        }

        return stok;
    }

    public static String getCurrentUserRole() {
        if (UserLogin.getSessionUser() != null) {
            return UserLogin.getSessionUser().getAuthType();
        }
        return null;
    }
}
