package UserController;

import Koneksi.DBKoneksi;
import UserModel.ModelKeranjang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UserKeranjang {

    public static String cekTransaksiAktif(String userId) {
        String orderIdPending = null;
        try (Connection conn = DBKoneksi.getConnection()) {
            String sql = """
                SELECT pb.order_id 
                FROM pembayaran pb
                JOIN detail_transaksi dt ON pb.order_id = dt.pembayaran_order_id
                JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
                WHERE pb.status = 'pending' AND t.user_id = ?
                ORDER BY pb.tanggal DESC LIMIT 1
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        orderIdPending = rs.getString("order_id");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengecek transaksi pending: " + e.getMessage());
        }
        return orderIdPending;
    }

    public static List<ModelKeranjang> getKeranjangPending(Connection conn, String userId) throws Exception {
        String sql = """
            SELECT dt.produk_id_produk, p.nama, dk.kategori, du.ukuran, dt.harga, dt.jumlah_barang, dt.total_harga 
            FROM detail_transaksi dt 
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id 
            JOIN produk p ON dt.produk_id_produk = p.id_produk 
            JOIN detail_kategori dk ON p.detail_kategori_id_kategori = dk.id_kategori 
            JOIN detail_ukuran du ON p.detail_ukuran_id_ukuran = du.id_ukuran 
            WHERE pb.status = 'pending' AND t.user_id = ? 
            ORDER BY dt.transaksi_id_transaksi, dt.produk_id_produk
        """;

        List<ModelKeranjang> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    ModelKeranjang mk = new ModelKeranjang();
                    mk.setNo(no++);
                    mk.setProdukId(rs.getString("produk_id_produk"));
                    mk.setNamaProduk(rs.getString("nama"));
                    mk.setKategori(rs.getString("kategori"));
                    mk.setUkuran(rs.getString("ukuran"));
                    mk.setHarga(rs.getLong("harga"));
                    mk.setJumlah(rs.getInt("jumlah_barang"));
                    mk.setSubtotal(rs.getLong("total_harga"));
                    list.add(mk);
                }
            }
        }
        return list;
    }

    public static int getJumlahItem(Connection conn, String userId) throws Exception {
        String sql = """
            SELECT COUNT(*) 
            FROM detail_transaksi dt 
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id 
            WHERE pb.status = 'pending' AND t.user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public static int getTotalBarang(Connection conn, String userId) throws Exception {
        String sql = """
            SELECT SUM(jumlah_barang) 
            FROM detail_transaksi dt 
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id 
            WHERE pb.status = 'pending' AND t.user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public static long getTotalHarga(Connection conn, String userId) throws Exception {
        String sql = """
            SELECT SUM(total_harga) 
            FROM detail_transaksi dt 
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id 
            WHERE pb.status = 'pending' AND t.user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    public static void updateJumlahBarang(Connection conn, String userId, String produkId, int jumlahBaru) throws Exception {
        String orderId = getPendingOrderId(conn, userId);
        if (orderId == null) return;

        String sql = """
            UPDATE detail_transaksi 
            SET jumlah_barang = ?, total_harga = harga * ? 
            WHERE produk_id_produk = ? AND pembayaran_order_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jumlahBaru);
            ps.setInt(2, jumlahBaru);
            ps.setString(3, produkId);
            ps.setString(4, orderId);
            ps.executeUpdate();
        }
        updateTotalPembayaran(conn, orderId);
    }

    public static void tambahJumlahBarang(Connection conn, String userId, String produkId, int jumlahTambah) throws Exception {
        String orderId = getPendingOrderId(conn, userId);
        if (orderId == null) return;

        String sql = """
            UPDATE detail_transaksi 
            SET jumlah_barang = jumlah_barang + ?, total_harga = harga * (jumlah_barang + ?) 
            WHERE produk_id_produk = ? AND pembayaran_order_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jumlahTambah);
            ps.setInt(2, jumlahTambah);
            ps.setString(3, produkId);
            ps.setString(4, orderId);
            ps.executeUpdate();
        }
        updateTotalPembayaran(conn, orderId);
    }

public static void hapusItem(Connection conn, String userId, String produkId) throws Exception {
    String orderId = getPendingOrderId(conn, userId);
    if (orderId == null) return;

    // 1. Ambil transaksi_id sebelum item dihapus
    String transaksiId = null;
    String getTransaksiIdSql = "SELECT transaksi_id_transaksi FROM detail_transaksi WHERE produk_id_produk = ? AND pembayaran_order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(getTransaksiIdSql)) {
        ps.setString(1, produkId);
        ps.setString(2, orderId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                transaksiId = rs.getString(1);
            }
        }
    }

    // 2. Hitung jumlah item SEBELUM menghapus
    int count = 0;
    String sqlCount = "SELECT COUNT(*) FROM detail_transaksi WHERE pembayaran_order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sqlCount)) {
        ps.setString(1, orderId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
    }

    // 3. Hapus item yang dipilih
    String sqlDelete = "DELETE FROM detail_transaksi WHERE produk_id_produk = ? AND pembayaran_order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
        ps.setString(1, produkId);
        ps.setString(2, orderId);
        ps.executeUpdate();
    }

    // 4. Jika tadi hanya 1 item, maka setelah ini jadi kosong → hapus pembayaran & transaksi
    if (count == 1) {
        String sqlHapusPembayaran = "DELETE FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlHapusPembayaran)) {
            ps.setString(1, orderId);
            ps.executeUpdate();
        }

        if (transaksiId != null) {
            String sqlHapusTransaksi = "DELETE FROM transaksi WHERE id_transaksi = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlHapusTransaksi)) {
                ps.setString(1, transaksiId);
                ps.executeUpdate();
            }
        }
    } else {
        updateTotalPembayaran(conn, orderId);
    }
}

    
public static void cleartransaksi(Connection conn, String userId) throws Exception {
    String orderId = getPendingOrderId(conn, userId);
    if (orderId == null) return;

    String idTransaksi = null;
    String sqlTransaksiId = "SELECT DISTINCT transaksi.id_transaksi FROM transaksi "
                          + "JOIN detail_transaksi ON transaksi.id_transaksi = detail_transaksi.transaksi_id_transaksi "
                          + "WHERE detail_transaksi.pembayaran_order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sqlTransaksiId)) {
        ps.setString(1, orderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            idTransaksi = rs.getString("id_transaksi");
        }
    }

    String sqlDetail = "DELETE FROM detail_transaksi WHERE pembayaran_order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
        ps.setString(1, orderId);
        ps.executeUpdate();
    }

    String sqlPembayaran = "DELETE FROM pembayaran WHERE order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sqlPembayaran)) {
        ps.setString(1, orderId);
        ps.executeUpdate();
    }

    if (idTransaksi != null) {
        String sqlTransaksi = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlTransaksi)) {
            ps.setString(1, idTransaksi);
            ps.executeUpdate();
        }
    }
}



    public static String getPendingOrderId(Connection conn, String userId) throws Exception {
        String sql = """
            SELECT pb.order_id 
            FROM pembayaran pb
            JOIN detail_transaksi dt ON pb.order_id = dt.pembayaran_order_id
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            WHERE pb.status = 'pending' AND t.user_id = ?
            ORDER BY pb.tanggal DESC LIMIT 1
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("order_id") : null;
            }
        }
    }

    private static void updateTotalPembayaran(Connection conn, String orderId) throws Exception {
        String sql = """
            UPDATE pembayaran 
            SET total = (SELECT COALESCE(SUM(total_harga),0) 
                         FROM detail_transaksi 
                         WHERE pembayaran_order_id = ?) 
            WHERE order_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ps.setString(2, orderId);
            ps.executeUpdate();
        }
    }

    public static String getProdukIdByRow(Connection conn, String userId, int rowIndex) throws Exception {
        String sql = """
            SELECT dt.produk_id_produk 
            FROM detail_transaksi dt 
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id 
            WHERE pb.status = 'pending' AND t.user_id = ?
            ORDER BY dt.transaksi_id_transaksi, dt.produk_id_produk
            LIMIT 1 OFFSET ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, rowIndex);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("produk_id_produk") : null;
            }
        }
    }
public static java.sql.Date getTanggalTransaksi(Connection conn, String userId) throws Exception {
    String sql = """
        SELECT pb.tanggal 
        FROM pembayaran pb
        JOIN detail_transaksi dt ON pb.order_id = dt.pembayaran_order_id
        JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
        WHERE pb.status = 'pending' AND t.user_id = ?
        ORDER BY pb.tanggal DESC
        LIMIT 1
    """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDate("tanggal");
            }
        }
    }
    return null;
}

}
