package UserController;

import UserModel.ModelTransaksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class UserTransaksi {

    public static String generateIdTransaksi(Connection conn, String userId) throws Exception {
        String prefix = "TXR" + userId.toUpperCase();
        int nextNumber = 1;
        String newId;

        String lastSql = "SELECT id_transaksi FROM transaksi WHERE id_transaksi LIKE ? ORDER BY id_transaksi DESC LIMIT 1 FOR UPDATE";
        try (PreparedStatement getLast = conn.prepareStatement(lastSql)) {
            getLast.setString(1, prefix + "%");
            try (ResultSet rs = getLast.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString("id_transaksi");
                    String numberPart = lastId.substring(prefix.length());
                    nextNumber = Integer.parseInt(numberPart) + 1;
                }
            }
        }

        newId = String.format(prefix + "%03d", nextNumber);
        return newId;
    }

    public static String generateOrderId(String userId) {
        return "ORD" + userId + System.currentTimeMillis();
    }

public static String getPendingTransactionId(Connection conn, String userId) throws Exception {
    String sql = """
        SELECT DISTINCT t.id_transaksi
        FROM transaksi t
        JOIN detail_transaksi dt ON t.id_transaksi = dt.transaksi_id_transaksi
        JOIN pembayaran p ON dt.pembayaran_order_id = p.order_id
        WHERE p.status = 'pending' AND t.user_id = ?
        ORDER BY p.tanggal DESC
        LIMIT 1
    """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("id_transaksi");
            }
        }
    }
    return null;
}

    public static void buatTransaksi(Connection conn, ModelTransaksi transaksi) throws Exception {
        PreparedStatement psTransaksi = null;
        PreparedStatement psBayar = null;

        try {
            conn.setAutoCommit(false);

            String idTransaksi = getPendingTransactionId(conn, transaksi.getUserId());

            boolean transaksiBaru = (idTransaksi == null);

            if (transaksiBaru) {
                // Buat ID transaksi baru
                idTransaksi = generateIdTransaksi(conn, transaksi.getUserId());
                transaksi.setIdTransaksi(idTransaksi);

                // INSERT ke tabel transaksi
                String sqlTransaksi = "INSERT INTO transaksi (id_transaksi, tanggal_transaksi, customer_id, user_id) VALUES (?, ?, ?, ?)";
                psTransaksi = conn.prepareStatement(sqlTransaksi);
                psTransaksi.setString(1, transaksi.getIdTransaksi());
                psTransaksi.setTimestamp(2, new Timestamp(transaksi.getTanggalTransaksi().getTime()));
                if (transaksi.getCustomerId() == null || transaksi.getCustomerId().isEmpty()) {
                    psTransaksi.setNull(3, java.sql.Types.VARCHAR);
                } else {
                    psTransaksi.setString(3, transaksi.getCustomerId());
                }
                psTransaksi.setString(4, transaksi.getUserId());
                psTransaksi.executeUpdate();

                // INSERT ke tabel pembayaran
                String orderId = generateOrderId(transaksi.getUserId());
                transaksi.setOrderId(orderId);
                String sqlBayar = "INSERT INTO pembayaran (order_id, total, metode_pembayaran, tanggal, payment_url, snap_token, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                psBayar = conn.prepareStatement(sqlBayar);
                psBayar.setString(1, orderId);
                psBayar.setLong(2, transaksi.getTotalHarga());
                psBayar.setNull(3, java.sql.Types.VARCHAR);
                psBayar.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                psBayar.setString(5, "");
                psBayar.setString(6, "");
                psBayar.setString(7, "pending");
                psBayar.executeUpdate();
            } else {
                transaksi.setIdTransaksi(idTransaksi);

                // Ambil order_id dari detail_transaksi
                String getOrderIdSql = "SELECT pembayaran_order_id FROM detail_transaksi WHERE transaksi_id_transaksi = ? ORDER BY pembayaran_order_id DESC LIMIT 1";
                String existingOrderId = null;
                try (PreparedStatement psGetOrderId = conn.prepareStatement(getOrderIdSql)) {
                    psGetOrderId.setString(1, transaksi.getIdTransaksi());
                    try (ResultSet rs = psGetOrderId.executeQuery()) {
                        if (rs.next()) {
                            existingOrderId = rs.getString("pembayaran_order_id");
                        }
                    }
                }

                if (existingOrderId == null) {
                    throw new Exception("Order ID tidak ditemukan untuk transaksi ID: " + transaksi.getIdTransaksi());
                }

                transaksi.setOrderId(existingOrderId);

                // Update total pembayaran
                String updateTotalSql = "UPDATE pembayaran SET total = total + ? WHERE order_id = ?";
                try (PreparedStatement psUpdateTotal = conn.prepareStatement(updateTotalSql)) {
                    psUpdateTotal.setLong(1, transaksi.getTotalHarga());
                    psUpdateTotal.setString(2, existingOrderId);
                    psUpdateTotal.executeUpdate();
                }
            }

            // Cek detail_transaksi
            String cekDetailSql = "SELECT jumlah_barang, total_harga FROM detail_transaksi WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?";
            try (PreparedStatement psCekDetail = conn.prepareStatement(cekDetailSql)) {
                psCekDetail.setString(1, transaksi.getIdTransaksi());
                psCekDetail.setString(2, transaksi.getProdukId());
                try (ResultSet rs = psCekDetail.executeQuery()) {
                    if (rs.next()) {
                        int jumlahLama = rs.getInt("jumlah_barang");
                        long totalHargaLama = rs.getLong("total_harga");

                        String updateDetailSql = "UPDATE detail_transaksi SET jumlah_barang = ?, total_harga = ? WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?";
                        try (PreparedStatement psUpdateDetail = conn.prepareStatement(updateDetailSql)) {
                            psUpdateDetail.setInt(1, jumlahLama + transaksi.getJumlahBarang());
                            psUpdateDetail.setLong(2, totalHargaLama + transaksi.getTotalHarga());
                            psUpdateDetail.setString(3, transaksi.getIdTransaksi());
                            psUpdateDetail.setString(4, transaksi.getProdukId());
                            psUpdateDetail.executeUpdate();
                        }
                    } else {
                        String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id_transaksi, produk_id_produk, harga, jumlah_barang, total_harga, pembayaran_order_id) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                            psDetail.setString(1, transaksi.getIdTransaksi());
                            psDetail.setString(2, transaksi.getProdukId());
                            psDetail.setLong(3, transaksi.getHarga());
                            psDetail.setInt(4, transaksi.getJumlahBarang());
                            psDetail.setLong(5, transaksi.getTotalHarga());
                            psDetail.setString(6, transaksi.getOrderId());
                            psDetail.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            if (psTransaksi != null) psTransaksi.close();
            if (psBayar != null) psBayar.close();
            conn.setAutoCommit(true);
        }
    }
}
