/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CustomerController;
import CustomerModel.CustomerModelStruk;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 *
 * @author AGIL
 */




public class CustomerStruk {

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

public static List<CustomerModelStruk> getDetailStruk(Connection conn, String transaksiId) throws Exception {
    String sql = "SELECT " +
                 "  t.id_transaksi, " +
                 "  c.google_name AS email_customer, " +
                 "  c.username AS nama_customer," +
                 "  c.alamat AS alamat_customer, " +
                 "  p.nama AS nama_produk, " +
                 "  dt.jumlah_barang, " +
                 "  dt.harga, " +
                 "  dt.total_harga " +
                 "FROM detail_transaksi dt " +
                 "JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi " +
                 "JOIN customer c ON t.customer_id = c.id " +
                 "JOIN produk p ON dt.produk_id_produk = p.id_produk " +
                 "WHERE dt.transaksi_id_transaksi = ?";

    List<CustomerModelStruk> list = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, transaksiId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String idTransaksi = rs.getString("id_transaksi");
                String orderId = "-"; // Karena tidak ada kolom ini di `transaksi`
                String namaCustomer = rs.getString("nama_customer");
                String email = rs.getString("email_customer");
                String alamat = rs.getString("alamat_customer");
                String namaProduk = rs.getString("nama_produk");
                int jumlah = rs.getInt("jumlah_barang");
                long harga = rs.getLong("harga");
                long total = rs.getLong("total_harga");

                list.add(new CustomerModelStruk(idTransaksi, orderId, namaCustomer, email, alamat,
                                        namaProduk, jumlah, harga, total));
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
        return "-";
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

    public static String generateIsiStrukCustomer(Connection conn, String orderId) throws Exception {
        String transaksiId = getTransaksiIdByOrderId(conn, orderId);
        if (transaksiId == null) {
            return "Gagal menemukan transaksi untuk Order ID: " + orderId;
        }

        List<CustomerModelStruk> isi = getDetailStruk(conn, transaksiId);
        if (isi.isEmpty()) {
            return "Data struk tidak ditemukan untuk transaksi ID: " + transaksiId;
        }

        long total = getTotal(conn, orderId);
        String tanggal = getTanggal(conn, orderId);
        String metodeBayar = getMetodeBayar(conn, orderId);
        String statusBayar = getStatusBayar(conn, orderId);

        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

        CustomerModelStruk info = isi.get(0); // ambil data customer dari entri pertama

        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("           KEMEJA SALATIGA\n");
        sb.append("       Jalan Patimura 72\n");
        sb.append("====================================\n");
        sb.append("No Order     : ").append(orderId).append("\n");
        sb.append("ID Transaksi : ").append(info.getTransaksiId()).append("\n");
        sb.append("Nama         : ").append(info.getNamaCustomer()).append("\n");
        sb.append("Email        : ").append(info.getEmailCustomer()).append("\n");
        sb.append("Alamat       : ").append(info.getAlamatCustomer()).append("\n");
        sb.append("Tanggal      : ").append(tanggal).append("\n");
        sb.append("Metode Bayar : ").append(metodeBayar).append("\n");
        sb.append("Status Bayar : ").append(statusBayar).append("\n");
        sb.append("------------------------------------\n");
        sb.append(String.format("%-20s %5s %14s %14s\n", 
                "Nama Produk\t", "Qty\t", "Harga\t", "Subtotal"));
        sb.append("------------------------------------\n");

        for (CustomerModelStruk m : isi) {
            String nama = m.getNamaProduk();
            int qty = m.getJumlah();
            String hargaSatuan = rupiahFormat.format(m.getHargaSatuan());
            String subtotal = rupiahFormat.format(m.getTotalHarga());
            if (nama.length() > 20) nama = nama.substring(0, 17) + "\t";
            sb.append(String.format("%-20s %5d %14s %14s\n", nama, qty, hargaSatuan, subtotal));
        }

        sb.append("------------------------------------\n");
        sb.append(String.format("%-20s %5s %14s %14s\n", "", "", "TOTAL :", rupiahFormat.format(total)));
        sb.append("====================================\n");
        sb.append("        TERIMA KASIH TELAH BELANJA\n");
        sb.append("       SEMOGA HARI ANDA MENYENANGKAN\n");
        sb.append("====================================\n");

        return sb.toString();
    }
    
  

 public static String getEmailCustomerByOrderId(Connection conn, String customerId) throws Exception {
    String sql = "SELECT google_name FROM customer WHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, customerId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("google_name"); // Ini email
            }
        }
    }
    return null;
}


}

