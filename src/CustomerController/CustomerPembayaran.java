package CustomerController;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class CustomerPembayaran {

    public static class MidtransResponse {
        public String snapToken;
        public String redirectUrl;
    }

    public static String getOrderIdPendingByCustomer(Connection conn, String customerId) throws Exception {
    String sql = "SELECT p.order_id " +
                 "FROM pembayaran p " +
                 "JOIN detail_transaksi dt ON p.order_id = dt.pembayaran_order_id " +
                 "JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi " +
                 "WHERE p.status = 'pending' AND t.customer_id = ? " +
                 "ORDER BY t.tanggal_transaksi DESC LIMIT 1";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, customerId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("order_id");
            }
        }
    }
    return null;
}
    
public static boolean cekStokCukupUntukOrder(Connection conn, String customerId) throws SQLException {
    String sqlAmbilPembayaran =
        "SELECT order_id FROM pembayaran " +
        "WHERE status = 'pending' AND customer_id = ? LIMIT 1";

    try (PreparedStatement ps1 = conn.prepareStatement(sqlAmbilPembayaran)) {
        ps1.setString(1, customerId);

        try (ResultSet rs1 = ps1.executeQuery()) {
            if (!rs1.next()) {
                System.out.println("Tidak ada transaksi pending untuk customer ID: " + customerId);
                return false;
            }

            String orderId = rs1.getString("order_id");

            String sqlCekStok =
                "SELECT dt.jumlah_barang, p.stok, p.id_produk, p.nama " +
                "FROM detail_transaksi dt " +
                "JOIN produk p ON dt.produk_id_produk = p.id_produk " +
                "WHERE dt.pembayaran_order_id = ?";

            try (PreparedStatement ps2 = conn.prepareStatement(sqlCekStok)) {
                ps2.setString(1, orderId);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        int jumlah = rs2.getInt("jumlah_barang");
                        int stok = rs2.getInt("stok");
                        String namaProduk = rs2.getString("nama");

                        if (stok < jumlah) {
                            System.out.println("Stok tidak cukup untuk produk: " + namaProduk);
                            return false;
                        }
                    }
                }
            }
        }
    }

    return true; 
}



    public static String generateOrderIdWithTimestamp() {
        String prefix = "ORDCUS";
        String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
        return prefix + timestamp;
    }

    public static String getMetodePembayaran(Connection conn, String orderId) throws Exception {
        String sql = "SELECT metode_pembayaran FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("metode_pembayaran");
            }
        }
        return null;
    }

    public static void setMetodePembayaranBaru(Connection conn, String orderId, String metode) throws Exception {
        String sql = "UPDATE pembayaran SET metode_pembayaran = ?, snap_token = NULL, payment_url = NULL WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, metode);
            ps.setString(2, orderId);
            ps.executeUpdate();
        }
    }

    public static String mapMetodeToMidtrans(String metodeCombo) {
        switch (metodeCombo) {
            case "Transfer Bank (bank_transfer)": return "bank_transfer";
            case "GoPay (gopay)": return "gopay";
            case "Alfamart": return "alfamart";
            case "Indomaret": return "indomaret";
            case "Kartu Kredit (credit_card)": return "credit_card";
            default: return null;
        }
    }

    public static void prosesMidtrans(Connection conn, String orderId, String metodeCombo, boolean updateMetode) throws Exception {
        long total = getTotalPembayaran(conn, orderId);
        String metodeMidtrans = mapMetodeToMidtrans(metodeCombo);

        if (metodeMidtrans == null) {
            throw new IllegalArgumentException("Metode pembayaran tidak valid: " + metodeCombo);
        }

        MidtransResponse response = getSnapToken(orderId, total, metodeMidtrans);
        if (response == null || response.snapToken == null || response.redirectUrl == null) {
            throw new Exception("Gagal mendapatkan Snap Token dari Midtrans");
        }

        String sql;
        if (updateMetode) {
            sql = "UPDATE pembayaran SET snap_token = ?, payment_url = ?, status = 'pending', metode_pembayaran = ? WHERE order_id = ?";
        } else {
            sql = "UPDATE pembayaran SET snap_token = ?, payment_url = ?, status = 'pending' WHERE order_id = ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, response.snapToken);
            ps.setString(2, response.redirectUrl);
            if (updateMetode) {
                ps.setString(3, metodeMidtrans);
                ps.setString(4, orderId);
            } else {
                ps.setString(3, orderId);
            }
            ps.executeUpdate();
        }

        String custId = CustomerController.CustomerLogin.sessionCustomer.getId();
        new CustomerView.CustomerLinkPembayaran(response.redirectUrl, custId).setVisible(true);
    }

    public static long getTotalPembayaran(Connection conn, String orderId) throws Exception {
        String sql = "SELECT total FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total");
                }
            }
        }
        throw new Exception("Order ID tidak ditemukan");
    }

    public static boolean updateMetodeDanOrderId(Connection conn, String oldOrderId, String newOrderId, String metode) throws Exception {
        String sql = "UPDATE pembayaran SET order_id = ?, metode_pembayaran = ?, snap_token = NULL, payment_url = NULL, status = 'pending' WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newOrderId);
            ps.setString(2, metode);
            ps.setString(3, oldOrderId);
            return ps.executeUpdate() > 0;
        }
    }

    public static MidtransResponse getSnapToken(String orderId, long amount, String metodePembayaran) throws Exception {
        String serverKey = "SB-Mid-server-33P7SdeJwYtr_03zNmyYUwFz";
        String auth = Base64.getEncoder().encodeToString((serverKey + ":").getBytes());

        URL url = new URL("https://app.sandbox.midtrans.com/snap/v1/transactions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Basic " + auth);

        JsonObject json = new JsonObject();
        JsonObject transactionDetails = new JsonObject();
        transactionDetails.addProperty("order_id", orderId);
        transactionDetails.addProperty("gross_amount", amount);
        json.add("transaction_details", transactionDetails);

        JsonArray enabled = new JsonArray();
        enabled.add(metodePembayaran);
        json.add("enabled_payments", enabled);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status != 201) {
            throw new RuntimeException("Midtrans error. HTTP status: " + status);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            JsonObject response = JsonParser.parseReader(br).getAsJsonObject();
            MidtransResponse res = new MidtransResponse();
            res.snapToken = response.get("token").getAsString();
            res.redirectUrl = response.get("redirect_url").getAsString();
            return res;
        }
    }

    public static void kurangiStokProduk(Connection conn, String orderId) throws Exception {
        String sqlSelect = "SELECT produk_id_produk, jumlah_barang FROM detail_transaksi WHERE pembayaran_order_id = ?";
        try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
            psSelect.setString(1, orderId);
            try (ResultSet rs = psSelect.executeQuery()) {
                while (rs.next()) {
                    String produkId = rs.getString("produk_id_produk");
                    int jumlah = rs.getInt("jumlah_barang");

                    String sqlUpdate = "UPDATE produk SET stok = stok - ? WHERE id_produk = ? AND stok >= ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                        psUpdate.setInt(1, jumlah);
                        psUpdate.setString(2, produkId);
                        psUpdate.setInt(3, jumlah);
                        int affected = psUpdate.executeUpdate();
                        if (affected == 0) {
                            throw new Exception("Stok tidak cukup untuk produk: " + produkId);
                        }
                    }
                }
            }
        }
    }

public static String getCustomerIdByOrderId(Connection conn, String orderId) throws Exception {
    String sql = "SELECT t.customer_id FROM transaksi t " +
                 "JOIN detail_transaksi dt ON t.id_transaksi = dt.transaksi_id_transaksi " +
                 "WHERE dt.pembayaran_order_id = ? LIMIT 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, orderId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("customer_id");
            }
        }
    }
    return null;
}






}
