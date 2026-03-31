package CustomerController;

import Koneksi.DBKoneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerTransaksiOnline {

public static void prosesBeliProduk(String customerId, String produkId, long hargaSatuan, int jumlah, long totalHarga) {
    try (Connection conn = DBKoneksi.getConnection()) {
        conn.setAutoCommit(false);

        String idTransaksi = null;
        String orderId = null;

        // 🔍 1. Cek transaksi pending sebelumnya
        String sqlCekPending =
            "SELECT DISTINCT t.id_transaksi, dt.pembayaran_order_id AS order_id " +
            "FROM transaksi t " +
            "JOIN detail_transaksi dt ON dt.transaksi_id_transaksi = t.id_transaksi " +
            "JOIN pembayaran p ON p.order_id = dt.pembayaran_order_id " +
            "WHERE t.customer_id = ? AND p.status = 'pending' " +
            "ORDER BY t.tanggal_transaksi DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sqlCekPending)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idTransaksi = rs.getString("id_transaksi");
                    orderId = rs.getString("order_id");
                }
            }
        }

        // 📦 2. Cek stok dan jumlah yang sudah dibeli dalam order ini
        String sqlCekStok =
            "SELECT dp.jumlah_barang, p.stok, p.id_produk, p.nama " +
            "FROM detail_transaksi dp " +
            "JOIN produk p ON dp.produk_id_produk = p.id_produk " +
            "WHERE dp.pembayaran_order_id = ?";

        int jumlahSudahAda = 0;
        int stok = 0;
        String namaProduk = "";

        try (PreparedStatement ps = conn.prepareStatement(sqlCekStok)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (produkId.equals(rs.getString("id_produk"))) {
                        jumlahSudahAda = rs.getInt("jumlah_barang");
                        stok = rs.getInt("stok");
                        namaProduk = rs.getString("nama");
                        break;
                    }
                }
            }
        }

        // Ambil stok jika belum ada di transaksi (baru dibeli pertama kali)
        if (stok == 0) {
            String sqlAmbilStok = "SELECT nama, stok FROM produk WHERE id_produk = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlAmbilStok)) {
                ps.setString(1, produkId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        namaProduk = rs.getString("nama");
                        stok = rs.getInt("stok");
                    }
                }
            }
        }

        int totalSetelahBeli = jumlahSudahAda + jumlah;

        if (totalSetelahBeli > stok) {
            JOptionPane.showMessageDialog(null,
                "Transaksi gagal. Stok produk tidak mencukupi.\n\n" +
                "Detail Produk:\n" +
                "Nama Produk     : " + namaProduk + "\n" +
                "Stok Saat Ini   : " + stok + "\n" +
                "Sudah Dibeli    : " + jumlahSudahAda + "\n" +
                "Ingin Dibeli    : " + jumlah + "\n\n" +
                "Silakan ubah jumlah atau hubungi admin.");
            conn.rollback();
            return;
        }

        // 🛒 3. Jika tidak ada transaksi pending, buat transaksi baru
        if (idTransaksi == null) {
            idTransaksi = generateIdTransaksi(conn, customerId);
            orderId = generateOrderId(customerId);

            String sqlTrans =
                "INSERT INTO transaksi (id_transaksi, tanggal_transaksi, customer_id) " +
                "VALUES (?, NOW(), ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTrans)) {
                ps.setString(1, idTransaksi);
                ps.setString(2, customerId);
                ps.executeUpdate();
            }

            String sqlBayar =
                "INSERT INTO pembayaran (order_id, total, metode_pembayaran, tanggal, payment_url, snap_token, status) " +
                "VALUES (?, 0, NULL, NOW(), '', '', 'pending')";
            try (PreparedStatement ps = conn.prepareStatement(sqlBayar)) {
                ps.setString(1, orderId);
                ps.executeUpdate();
            }
        }

        // 🔄 4. Cek apakah produk sudah ada dalam detail_transaksi
        boolean produkAda = false;
        int jumlahLama = 0;
        long totalLama = 0;

        String sqlCekDet =
            "SELECT jumlah_barang, total_harga " +
            "FROM detail_transaksi " +
            "WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlCekDet)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, produkId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    produkAda = true;
                    jumlahLama = rs.getInt("jumlah_barang");
                    totalLama = rs.getLong("total_harga");
                }
            }
        }

        // 📝 5. Update atau insert detail_transaksi
        if (produkAda) {
            String sqlUpdDet =
                "UPDATE detail_transaksi " +
                "SET jumlah_barang = ?, total_harga = ? " +
                "WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdDet)) {
                ps.setInt(1, jumlahLama + jumlah);
                ps.setLong(2, totalLama + totalHarga);
                ps.setString(3, idTransaksi);
                ps.setString(4, produkId);
                ps.executeUpdate();
            }
        } else {
            String sqlInsDet =
                "INSERT INTO detail_transaksi " +
                "(transaksi_id_transaksi, produk_id_produk, harga, jumlah_barang, total_harga, pembayaran_order_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInsDet)) {
                ps.setString(1, idTransaksi);
                ps.setString(2, produkId);
                ps.setLong(3, hargaSatuan);
                ps.setInt(4, jumlah);
                ps.setLong(5, totalHarga);
                ps.setString(6, orderId);
                ps.executeUpdate();
            }
        }

        // 💰 6. Update total pembayaran
        String sqlUpdTotal =
            "UPDATE pembayaran SET total = total + ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdTotal)) {
            ps.setLong(1, totalHarga);
            ps.setString(2, orderId);
            ps.executeUpdate();
        }

        conn.commit();
        JOptionPane.showMessageDialog(
            null,
            "Produk berhasil ditambahkan ke transaksi.\nID Transaksi: " + idTransaksi
        );

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Gagal memproses pembelian:\n" + e.getMessage());
    }
}


    private static String generateIdTransaksi(Connection conn, String customerId) throws Exception {
        String prefix = "TCUS" + customerId;
        int nextNumber = 1;
        String newId;

        String checkSql = "SELECT COUNT(*) FROM transaksi WHERE id_transaksi = ?";
        String lastSql = "SELECT id_transaksi FROM transaksi WHERE id_transaksi LIKE ? ORDER BY id_transaksi DESC LIMIT 1";

        try (
            PreparedStatement getLast = conn.prepareStatement(lastSql);
            PreparedStatement checkExist = conn.prepareStatement(checkSql)
        ) {
            getLast.setString(1, prefix + "%");
            try (ResultSet rs = getLast.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString("id_transaksi");
                    if (lastId != null && lastId.length() > prefix.length()) {
                        String numberPart = lastId.substring(prefix.length());
                        nextNumber = Integer.parseInt(numberPart) + 1;
                    }
                }
            }

            while (true) {
                newId = String.format(prefix + "%03d", nextNumber);
                checkExist.setString(1, newId);
                try (ResultSet checkRs = checkExist.executeQuery()) {
                    if (checkRs.next() && checkRs.getInt(1) == 0) {
                        break;
                    }
                }
                nextNumber++;
            }

            return newId;
        }
    }

    private static String generateOrderId(String customerId) {
        String prefix = "ORDCUS" + customerId;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String timestamp = sdf.format(new Date());
        return prefix + timestamp;
    }
}
