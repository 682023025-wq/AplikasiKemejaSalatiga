package UserController;

import UserModel.ModelPembayaran;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserPembayaran {

    public static String generateOrderIdByTimestamp(String userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
        String timestamp = sdf.format(new Date());
        return "ORD" + userId + timestamp;
    }

    public static ModelPembayaran getPembayaranPending(Connection conn, String userId) throws Exception {
        String sql = """
            SELECT pb.order_id, pb.total, pb.metode_pembayaran, pb.tanggal, pb.status
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
                    ModelPembayaran mp = new ModelPembayaran();
                    mp.setOrderId(rs.getString("order_id"));
                    mp.setTotal(rs.getLong("total"));
                    mp.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    mp.setTanggal(rs.getString("tanggal"));
                    mp.setStatus(rs.getString("status"));
                    return mp;
                }
            }
        }
        return null;
    }

    public static boolean bayarCash(Connection conn, String orderId, long uangDibayar) throws Exception {
        String sqlGet = "SELECT total FROM pembayaran WHERE order_id = ? AND status = 'pending'";
        long totalBayar = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlGet)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalBayar = rs.getLong("total");
                } else {
                    throw new Exception("Order ID tidak ditemukan atau sudah lunas");
                }
            }
        }

        if (uangDibayar < totalBayar) {
            throw new Exception("Nominal pembayaran kurang dari total tagihan");
        }

        String sqlUpdate = "UPDATE pembayaran SET status = 'lunas', metode_pembayaran = 'cash' WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            ps.setString(1, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public static String getStatusPembayaran(Connection conn, String orderId) throws Exception {
        String sql = "SELECT status FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
                return null;
            }
        }
    }

    public static String getOrderIdPending(Connection conn) throws Exception {
        String sql = "SELECT order_id FROM pembayaran WHERE status = 'pending' LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("order_id");
            }
            return null;
        }
    }

    public static long getTotalPembayaran(Connection conn, String orderId) throws Exception {
        String sql = "SELECT total FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total");
                }
                throw new Exception("Order ID tidak ditemukan");
            }
        }
    }

    public static boolean updateStatusPembayaran(Connection conn, String orderId, String status, String metode) throws Exception {
        String sql = "UPDATE pembayaran SET status = ?, metode_pembayaran = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, metode);
            ps.setString(3, orderId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

public static void kurangiStokProduk(Connection conn, String orderId, String userId) throws Exception {
    String sqlSelect = """
        SELECT dt.produk_id_produk, dt.jumlah_barang, p.nama, p.stok
        FROM detail_transaksi dt
        JOIN produk p ON dt.produk_id_produk = p.id_produk
        JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
        WHERE dt.pembayaran_order_id = ? AND t.user_id = ?
    """;

    List<String> gagalProduk = new ArrayList<>();

    try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
        psSelect.setString(1, orderId);
        psSelect.setString(2, userId);
        try (ResultSet rs = psSelect.executeQuery()) {
            while (rs.next()) {
                String produkId = rs.getString("produk_id_produk");
                String namaProduk = rs.getString("nama");
                int jumlah = rs.getInt("jumlah_barang");
                int stokSekarang = rs.getInt("stok");

                if (stokSekarang < jumlah) {
                    gagalProduk.add("- " + namaProduk + " (stok: " + stokSekarang + ", diminta: " + jumlah + ")");
                    continue;
                }

                // Jika stok cukup, kurangi stok
                String sqlUpdate = "UPDATE produk SET stok = stok - ? WHERE id_produk = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setInt(1, jumlah);
                    psUpdate.setString(2, produkId);
                    psUpdate.executeUpdate();
                }
            }
        }
    }

    if (!gagalProduk.isEmpty()) {
        throw new Exception("Stok tidak mencukupi untuk:\n" + String.join("\n", gagalProduk) +
                "\n\nSilakan perbarui jumlah pembelian Anda.");
    }
}



    public static String getOrderIdPendingOrLunas(Connection conn) throws Exception {
        String sql = "SELECT order_id FROM pembayaran WHERE status IN ('pending', 'lunas') ORDER BY tanggal DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("order_id");
            }
            return null;
        }
    }

    public static boolean updateSnapTokenAndUrl(Connection conn, String orderId, String snapToken, String paymentUrl) throws SQLException {
        String sql = "UPDATE pembayaran SET snap_token = ?, payment_url = ? WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, snapToken);
            stmt.setString(2, paymentUrl);
            stmt.setString(3, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
}
