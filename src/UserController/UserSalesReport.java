package UserController;

import Koneksi.DBKoneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import UserModel.ModelSalesReport;
import java.sql.Connection;
import java.util.Date;



public class UserSalesReport {
    public List<ModelSalesReport> getAllSalesReports() {
        List<ModelSalesReport> list = new ArrayList<>();

        String query = "SELECT " +
               "dt.transaksi_id_transaksi AS ID_Transaksi, " +
               "pb.tanggal AS Tanggal_transaksi, " +
               "p.nama AS Nama, " +
               "dt.jumlah_barang AS jumlah_barang, " +
               "dt.harga AS harga_barang, " +
               "dt.total_harga AS total_harga " +
               "FROM detail_transaksi dt " +
               "JOIN produk p ON dt.produk_id_produk = p.id_produk " +
               "JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id " +
               "WHERE pb.status = 'lunas'";

        try (Connection conn = DBKoneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ModelSalesReport report = new ModelSalesReport();
                report.setID_Transaksi(rs.getString("ID_Transaksi"));
                report.setTanggal_transaksi(rs.getDate("Tanggal_transaksi"));
                report.setNama(rs.getString("Nama"));
                report.setJumlah_barang(rs.getInt("jumlah_barang"));
                report.setHarga_barang(rs.getDouble("harga_barang"));
                report.setTotal_harga(rs.getDouble("total_harga"));

                list.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<ModelSalesReport> getSalesReportsByDate(Date tanggal) {
    List<ModelSalesReport> list = new ArrayList<>();
    
    String sql = "SELECT " +
           "dt.transaksi_id_transaksi AS id_transaksi, " +
           "pb.tanggal AS tanggal_transaksi, " +
           "p.nama AS nama_produk, " +
           "dt.jumlah_barang AS jumlah, " +
           "dt.harga AS harga, " +
           "dt.total_harga AS total_harga " +
           "FROM detail_transaksi dt " +
           "JOIN produk p ON dt.produk_id_produk = p.id_produk " +
           "JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id " +
           "WHERE pb.status = 'lunas' AND DATE(pb.tanggal) = ?";

    try (Connection conn = DBKoneksi.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDate(1, new java.sql.Date(tanggal.getTime()));
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            ModelSalesReport sr = new ModelSalesReport();
            sr.setID_Transaksi(rs.getString("id_transaksi"));
            sr.setNama(rs.getString("nama_produk"));
            sr.setJumlah_barang(rs.getInt("jumlah"));
            sr.setHarga_barang(rs.getDouble("harga"));
            sr.setTotal_harga(rs.getDouble("total_harga"));
            sr.setTanggal_transaksi(rs.getDate("tanggal_transaksi"));
            list.add(sr);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
    
    
}
