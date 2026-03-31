package UserController;

import UserModel.ModelStruk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserStruk {

    public static boolean isLunas(Connection conn, String orderId) throws Exception {
        String sql = "SELECT status FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status").equalsIgnoreCase("lunas");
                }
            }
        }
        return false;
    }

    public static List<ModelStruk> getDetailStruk(Connection conn, String transaksiId) throws Exception {
        String sql = "SELECT p.nama, dt.jumlah_barang, dt.harga, dt.total_harga " +
                     "FROM detail_transaksi dt " +
                     "JOIN produk p ON dt.produk_id_produk = p.id_produk " +
                     "WHERE dt.transaksi_id_transaksi = ?";
        List<ModelStruk> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaksiId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    int jumlah = rs.getInt("jumlah_barang");
                    long harga = rs.getLong("harga");
                    long total = rs.getLong("total_harga");
                    list.add(new ModelStruk(nama, jumlah, harga, total));
                }
            }
        }
        return list;
    }

    public static long getTotal(Connection conn, String orderId) throws Exception {
        String sql = "SELECT total FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total");
                }
            }
        }
        return 0;
    }
    
    public static String getTanggal(Connection conn, String orderId) throws Exception {
        String sql = "SELECT tanggal FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tanggal");
                }
            }
        }
        return "";
    }
    
    public static String getMetodeBayar(Connection conn, String orderId) throws Exception {
        String sql = "SELECT metode_pembayaran FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String metode = rs.getString("metode_pembayaran");
                    return (metode == null || metode.isEmpty()) ? "Belum ditentukan" : metode;
                }
            }
        }
        return "Belum ditentukan";
    }
    
    public static String getStatusBayar(Connection conn, String orderId) throws Exception {
        String sql = "SELECT status FROM pembayaran WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return "Unknown";
    }

public static String generateIsiStruk(Connection conn, String orderId, Long nominalCash, Long kembalian) throws Exception {
    String transaksiId = getTransaksiIdByOrderId(conn, orderId);
    if (transaksiId == null) {
        return "Gagal menemukan transaksi untuk Order ID: " + orderId;
    }

    List<ModelStruk> isi = getDetailStruk(conn, transaksiId);
    long total = getTotal(conn, orderId);
    String tanggal = getTanggal(conn, orderId);
    String metodeBayar = getMetodeBayar(conn, orderId);
    String statusBayar = getStatusBayar(conn, orderId);

    NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

    StringBuilder sb = new StringBuilder();
    sb.append("====================================\n");
    sb.append("          TOKO BAJU XYZ\n");
    sb.append("      Jl. Contoh No. 123 Jakarta\n");
    sb.append("         Telp: (021) 1234567\n");
    sb.append("====================================\n");
    sb.append("No Transaksi : ").append(orderId).append("\n");
    sb.append("Tanggal     : ").append(tanggal).append("\n");
    sb.append("Metode Bayar: ").append(metodeBayar).append("\n");
    sb.append("Status Bayar: ").append(statusBayar).append("\n");
    sb.append("------------------------------------\n");
    sb.append(String.format("%-20s %5s %14s %14s\n", "Nama Produk", "Qty", "Harga", "Subtotal"));
    sb.append("------------------------------------\n");

    for (ModelStruk m : isi) {
        String nama = m.getNamaProduk();
        int qty = m.getJumlah();
        String hargaSatuan = rupiahFormat.format(m.getHargaSatuan());
        String subtotal = rupiahFormat.format(m.getTotalHarga());
        if (nama.length() > 20) nama = nama.substring(0, 17) + "...";
        sb.append(String.format("%-20s %5d %14s %14s\n", nama, qty, hargaSatuan, subtotal));
    }

    sb.append("------------------------------------\n");
    sb.append(String.format("%-20s %5s %14s %14s\n", "", "", "TOTAL :", rupiahFormat.format(total)));

    if ("Cash".equalsIgnoreCase(metodeBayar)) {
        if (nominalCash != null && kembalian != null) {
            sb.append(String.format("%-20s %5s %14s %14s\n", "", "", "BAYAR :", rupiahFormat.format(nominalCash)));
            sb.append(String.format("%-20s %5s %14s %14s\n", "", "", "KEMBALIAN :", rupiahFormat.format(kembalian)));
        }
    }

    sb.append("====================================\n");
    sb.append("       TERIMA KASIH ATAS KUNJUNGAN\n");
    sb.append("         SEMOGA HARI ANDA MENYENANGKAN\n");
    sb.append("====================================\n");

    return sb.toString();
}
public static String getTransaksiIdByOrderId(Connection conn, String orderId) throws Exception {
    String sql = "SELECT DISTINCT transaksi_id_transaksi FROM detail_transaksi WHERE pembayaran_order_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, orderId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("transaksi_id_transaksi");
            }
        }
    }
    return null;
}



}
