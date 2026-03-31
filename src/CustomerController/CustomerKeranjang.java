package CustomerController;

import Koneksi.DBKoneksi;
import CustomerModel.CustomerModelKeranjang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerKeranjang {

    public static List<CustomerModelKeranjang> getKeranjangByCustomer(String customerId) throws Exception {
        List<CustomerModelKeranjang> list = new ArrayList<>();
        String sql = """
            SELECT dt.produk_id_produk, p.nama, dk.kategori, du.ukuran,
                   dt.harga, dt.jumlah_barang, dt.total_harga
            FROM detail_transaksi dt
            JOIN produk p ON dt.produk_id_produk = p.id_produk
            JOIN detail_kategori dk ON p.detail_kategori_id_kategori = dk.id_kategori
            JOIN detail_ukuran du ON p.detail_ukuran_id_ukuran = du.id_ukuran
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id
            WHERE pb.status = 'pending' AND t.customer_id = ?
            ORDER BY dt.transaksi_id_transaksi, dt.produk_id_produk
        """;

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    CustomerModelKeranjang mk = new CustomerModelKeranjang();
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

    public static String getPendingTransaksiIdByCustomer(String customerId) throws Exception {
        String sql = """
            SELECT t.id_transaksi
            FROM transaksi t
            JOIN pembayaran pb ON pb.order_id = t.id_transaksi
            WHERE pb.status = 'pending' AND t.customer_id = ?
            ORDER BY t.tanggal_transaksi DESC
            LIMIT 1
        """;

        try (Connection conn = DBKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_transaksi");
                }
            }
        }
        return null;
    }

public static boolean tambahJumlahBarang(String customerId, String produkId) throws Exception {
    try (Connection conn = DBKoneksi.getConnection()) {
        String sql = """
            SELECT dt.transaksi_id_transaksi, dt.jumlah_barang, dt.harga, p.stok, pb.order_id
            FROM detail_transaksi dt
            JOIN produk p ON dt.produk_id_produk = p.id_produk
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            LEFT JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id
            WHERE (pb.status IS NULL OR pb.status = 'pending') 
              AND t.customer_id = ? 
              AND dt.produk_id_produk = ?
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setString(2, produkId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String transaksiId = rs.getString("transaksi_id_transaksi");
                    int jumlah = rs.getInt("jumlah_barang");
                    int stok = rs.getInt("stok");
                    long harga = rs.getLong("harga");
                    String orderId = rs.getString("order_id");

                    if (jumlah >= stok) return false;

                    conn.setAutoCommit(false);

                    try (PreparedStatement ps1 = conn.prepareStatement("""
                        UPDATE detail_transaksi SET jumlah_barang = jumlah_barang + 1, total_harga = total_harga + ?
                        WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?
                    """)) {
                        ps1.setLong(1, harga);
                        ps1.setString(2, transaksiId);
                        ps1.setString(3, produkId);
                        ps1.executeUpdate();
                    }

                    if (orderId != null) {
                        try (PreparedStatement ps2 = conn.prepareStatement("""
                            UPDATE pembayaran SET total = total + ? WHERE order_id = ?
                        """)) {
                            ps2.setLong(1, harga);
                            ps2.setString(2, orderId);
                            ps2.executeUpdate();
                        }
                    }

                    conn.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    return false;
}

public static boolean updateJumlahBarang(String customerId, String produkId, int jumlahBaru) throws Exception {
    if (jumlahBaru <= 0) {
        throw new IllegalArgumentException("Jumlah tidak boleh kosong atau negatif.");
    }

    try (Connection conn = DBKoneksi.getConnection()) {
        String sql = """
            SELECT dt.transaksi_id_transaksi, dt.jumlah_barang, dt.harga, p.stok, pb.order_id
            FROM detail_transaksi dt
            JOIN produk p ON dt.produk_id_produk = p.id_produk
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            LEFT JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id
            WHERE (pb.status IS NULL OR pb.status = 'pending') 
              AND t.customer_id = ? 
              AND dt.produk_id_produk = ?
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setString(2, produkId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String transaksiId = rs.getString("transaksi_id_transaksi");
                    int jumlahLama = rs.getInt("jumlah_barang");
                    int stok = rs.getInt("stok");
                    long harga = rs.getLong("harga");
                    String orderId = rs.getString("order_id");

                    if (jumlahBaru > stok) {
                        throw new IllegalArgumentException("Stok tidak mencukupi.");
                    }

                    long selisih = harga * (jumlahBaru - jumlahLama);

                    conn.setAutoCommit(false);

                    try (PreparedStatement ps1 = conn.prepareStatement("""
                        UPDATE detail_transaksi SET jumlah_barang = ?, total_harga = ?
                        WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?
                    """)) {
                        ps1.setInt(1, jumlahBaru);
                        ps1.setLong(2, harga * jumlahBaru);
                        ps1.setString(3, transaksiId);
                        ps1.setString(4, produkId);
                        ps1.executeUpdate();
                    }

                    if (orderId != null && selisih != 0) {
                        try (PreparedStatement ps2 = conn.prepareStatement("""
                            UPDATE pembayaran SET total = total + ? WHERE order_id = ?
                        """)) {
                            ps2.setLong(1, selisih);
                            ps2.setString(2, orderId);
                            ps2.executeUpdate();
                        }
                    }

                    conn.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    return false;
}


public static boolean hapusItem(String customerId, String produkId) throws Exception {
    try (Connection conn = DBKoneksi.getConnection()) {
        String getSql = """
            SELECT dt.total_harga, dt.transaksi_id_transaksi, dt.pembayaran_order_id
            FROM detail_transaksi dt
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id
            JOIN transaksi t ON dt.transaksi_id_transaksi = t.id_transaksi
            WHERE pb.status = 'pending' AND t.customer_id = ? AND dt.produk_id_produk = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(getSql)) {
            ps.setString(1, customerId);
            ps.setString(2, produkId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long total = rs.getLong("total_harga");
                    String transaksiId = rs.getString("transaksi_id_transaksi");
                    String pembayaranId = rs.getString("pembayaran_order_id");

                    conn.setAutoCommit(false);

                    try (PreparedStatement ps1 = conn.prepareStatement("""
                            DELETE FROM detail_transaksi 
                            WHERE transaksi_id_transaksi = ? AND produk_id_produk = ?
                        """)) {
                        ps1.setString(1, transaksiId);
                        ps1.setString(2, produkId);
                        ps1.executeUpdate();
                    }

                    boolean adaSisaItem = false;
                    try (PreparedStatement ps2 = conn.prepareStatement("""
                            SELECT 1 FROM detail_transaksi WHERE transaksi_id_transaksi = ? LIMIT 1
                        """)) {
                        ps2.setString(1, transaksiId);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            adaSisaItem = rs2.next();
                        }
                    }

                    if (adaSisaItem) {
                        try (PreparedStatement ps3 = conn.prepareStatement("""
                                UPDATE pembayaran SET total = total - ? WHERE order_id = ?
                            """)) {
                            ps3.setLong(1, total);
                            ps3.setString(2, pembayaranId);
                            ps3.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement ps4 = conn.prepareStatement("DELETE FROM pembayaran WHERE order_id = ?")) {
                            ps4.setString(1, pembayaranId);
                            ps4.executeUpdate();
                        }
                        try (PreparedStatement ps5 = conn.prepareStatement("DELETE FROM transaksi WHERE id_transaksi = ?")) {
                            ps5.setString(1, transaksiId);
                            ps5.executeUpdate();
                        }
                    }

                    conn.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    return false;
}

public static boolean hapusSemua(String customerId) throws Exception {
    try (Connection conn = DBKoneksi.getConnection()) {
        String getTransaksi = """
            SELECT DISTINCT t.id_transaksi 
            FROM transaksi t
            JOIN detail_transaksi dt ON t.id_transaksi = dt.transaksi_id_transaksi
            JOIN pembayaran p ON dt.pembayaran_order_id = p.order_id
            WHERE t.customer_id = ? AND p.status = 'pending'
            LIMIT 1
        """;

        String transaksiId = null;
        try (PreparedStatement ps = conn.prepareStatement(getTransaksi)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    transaksiId = rs.getString("id_transaksi");
                }
            }
        }

        if (transaksiId == null) return false;

        conn.setAutoCommit(false);

        try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM detail_transaksi WHERE transaksi_id_transaksi = ?")) {
            ps1.setString(1, transaksiId);
            ps1.executeUpdate();
        }

        try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM pembayaran WHERE order_id = ?")) {
            ps2.setString(1, transaksiId); // karena pembayaran_order_id = transaksi_id
            ps2.executeUpdate();
        }

        try (PreparedStatement ps3 = conn.prepareStatement("DELETE FROM transaksi WHERE id_transaksi = ?")) {
            ps3.setString(1, transaksiId);
            ps3.executeUpdate();
        }

        conn.commit();
        return true;
    } catch (Exception e) {
        throw e;
    }
}


}
