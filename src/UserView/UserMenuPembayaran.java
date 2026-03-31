/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UserView;

import Koneksi.DBKoneksi;
import UserController.UserKeranjang;
import UserController.UserLogin;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import UserController.UserPembayaran;
import UserModel.ModelPembayaran;
import java.sql.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.JFrame;

/**
 *
 * @author AGIL
 */
public class UserMenuPembayaran extends javax.swing.JFrame {
    private ModelPembayaran currentPembayaran;

    public UserMenuPembayaran() {
        initComponents();
        PembayaranCash.setVisible(false); 
        loadPembayaranPending();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void loadPembayaranPending() {
    try (Connection conn = DBKoneksi.getConnection()) {
        String userId = UserLogin.getSessionUser().getId();  // Sudah pasti user login

        currentPembayaran = UserController.UserPembayaran.getPembayaranPending(conn, userId);

        if (currentPembayaran != null) {
            txttotal.setText(formatRupiah(currentPembayaran.getTotal())); // Menampilkan total tagihan
            PembayaranCash.setVisible(false);  // Sembunyikan tombol jika perlu
            pilihanbayar.setSelectedIndex(0);  // Reset pilihan pembayaran
        } else {
            txttotal.setText("Rp 0");
            PembayaranCash.setVisible(false);
            pilihanbayar.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this, "Tidak ada pembayaran pending dari user ini");
        }
        String OrderId = UserKeranjang.getPendingOrderId(conn,userId);
        Date tanggal = UserKeranjang.getTanggalTransaksi(conn, userId);
        int jumlahItem = UserKeranjang.getJumlahItem(conn, userId);
        int totalBarang = UserKeranjang.getTotalBarang(conn, userId);
        long totalHarga = UserKeranjang.getTotalHarga(conn, userId);
        txtidtransaksi.setText(OrderId);
        txttanggal.setText(formattanggal(tanggal));
        txtjumlahitem.setText("Jumlah Item : " + jumlahItem);
        txttotalbarang.setText("Total Barang : " + totalBarang);
        txttotal.setText("Total : " + formatRupiah(totalHarga));

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat pembayaran: " + e.getMessage());
        e.printStackTrace();
    }
}

private String formattanggal(Date tanggal) {
    if (tanggal == null) {
        return "Tanggal tidak tersedia"; // atau bisa juga return "" atau "-";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
    return sdf.format(tanggal);
}



    private String formatRupiah(long nominal) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
        return formatter.format(nominal);
    }
   
    private String mapMetodeToMidtrans(String metode) {
    switch (metode) {
        case "Transfer Bank (bank_transfer)":
            return "bank_transfer";
        case "GoPay (gopay)":
            return "gopay";
        case "Alfamat":
            return "alfamart";
        case "Indomaret":
            return "indomaret";
        case "Kartu Kredit (credit_card)":
            return "credit_card";
        default:
            return null;
    }
}


private void prosesMidtrans(Connection conn, String orderId, String metode, boolean updateMetode) throws Exception {
    long total = UserPembayaran.getTotalPembayaran(conn, orderId);
        String metodeMidtrans;

        if (updateMetode || metode == null || metode.isEmpty()) {
            // Jika updateMetode true atau metode kosong (misalnya pertama kali),
            // mapping ke format Midtrans
            metodeMidtrans = mapMetodeToMidtrans(metode);
        } else {
            // metode sudah dalam format Midtrans (misal "credit_card", "gopay", dll)
            metodeMidtrans = metode;
        }


    UserController.UserPembayaranOnline.MidtransResponse response =
        UserController.UserPembayaranOnline.getSnapToken(orderId, total, metodeMidtrans);
    if (response == null || response.snapToken == null || response.redirectUrl == null) {
        throw new Exception("Gagal mendapatkan snapToken atau redirectUrl dari Midtrans");
    }

    PreparedStatement ps = conn.prepareStatement(
        "UPDATE pembayaran SET snap_token = ?, payment_url = ?, status = 'pending', metode_pembayaran = ? WHERE order_id = ?");
    ps.setString(1, response.snapToken);
    ps.setString(2, response.redirectUrl);
    ps.setString(3, metodeMidtrans);  // tetap isi ulang meskipun tidak ganti
    ps.setString(4, orderId);
    ps.executeUpdate();
    ps.close();

    UserView.UserMenuLinkPembayaran web = new UserView.UserMenuLinkPembayaran(response.redirectUrl);
    web.setVisible(true);
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnkembali = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtidtransaksi = new javax.swing.JLabel();
        txttanggal = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txttotalbarang = new javax.swing.JLabel();
        txtjumlahitem = new javax.swing.JLabel();
        txttotal = new javax.swing.JLabel();
        PembayaranCash = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        boxuang = new javax.swing.JTextField();
        txtkembalian = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        pilihanbayar = new javax.swing.JComboBox<>();
        btnbayar = new javax.swing.JButton();
        btnstruk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("MENU PEMBAYARAN");

        btnkembali.setText("Kembali");
        btnkembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnkembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(372, 372, 372)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(81, 81, 81)
                .addComponent(btnkembali)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnkembali)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(24, 24, 24))))
        );

        txtidtransaksi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtidtransaksi.setText("ID TRANSAKSI");

        txttanggal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txttanggal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txttanggal.setText("Tanggal ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(txtidtransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txttanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtidtransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(txttanggal))
                .addContainerGap())
        );

        txttotalbarang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txttotalbarang.setText("Total Barang");

        txtjumlahitem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtjumlahitem.setText("Jumlah Item");

        txttotal.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txttotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txttotal.setText("Total");

        PembayaranCash.setBackground(new java.awt.Color(204, 255, 255));

        jLabel2.setText("Masukkan Nominal");

        boxuang.setText("Pembayaran Cash Only");

        txtkembalian.setText("Kembalian :");

        javax.swing.GroupLayout PembayaranCashLayout = new javax.swing.GroupLayout(PembayaranCash);
        PembayaranCash.setLayout(PembayaranCashLayout);
        PembayaranCashLayout.setHorizontalGroup(
            PembayaranCashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PembayaranCashLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PembayaranCashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtkembalian)
                    .addComponent(boxuang, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PembayaranCashLayout.setVerticalGroup(
            PembayaranCashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PembayaranCashLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(boxuang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtkembalian)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txttotalbarang, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtjumlahitem, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txttotal, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(PembayaranCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(txtjumlahitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txttotalbarang)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txttotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(PembayaranCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Metode Pembayaran");

        pilihanbayar.setModel(new DefaultComboBoxModel<>(new String[]{
            "Pilih Metode Pembayaran",
            "Cash",
            "Transfer Bank (bank_transfer)",
            "GoPay (gopay)",
            "Alfamat",
            "Indomaret",
            "Kartu Kredit (credit_card)"
        }));
        pilihanbayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihanbayarActionPerformed(evt);
            }
        });

        btnbayar.setText("BAYAR SEKARANG");
        btnbayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbayarActionPerformed(evt);
            }
        });

        btnstruk.setText("CETAK STRUK");
        btnstruk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnstrukActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnstruk, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnbayar))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(459, 459, 459)
                        .addComponent(pilihanbayar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(pilihanbayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnstruk, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnkembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnkembaliActionPerformed
    try (Connection conn = DBKoneksi.getConnection()) {
        String orderId = UserPembayaran.getOrderIdPending(conn);

        if (orderId == null) {
            PreparedStatement psLast = conn.prepareStatement(
                "SELECT order_id FROM pembayaran ORDER BY tanggal DESC LIMIT 1"
            );
            ResultSet rsLast = psLast.executeQuery();
            if (rsLast.next()) {
                orderId = rsLast.getString("order_id");
            }
            rsLast.close();
            psLast.close();

            if (orderId == null) {
                JOptionPane.showMessageDialog(this, "Tidak ditemukan transaksi apapun.");
                new UserView.UserMenuKasir().setVisible(true);
                return;
            }
        }

        String status = null;
        PreparedStatement ps = conn.prepareStatement("SELECT status FROM pembayaran WHERE order_id = ?");
        ps.setString(1, orderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            status = rs.getString("status");
        }
        rs.close();
        ps.close();

        this.dispose();

        if ("lunas".equalsIgnoreCase(status)) {
            new UserView.UserMenuKasir().setVisible(true);
        } else {
            new UserView.UserMenuKeranjang().setVisible(true);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat kembali: " + e.getMessage());
    }
    }//GEN-LAST:event_btnkembaliActionPerformed

    private void pilihanbayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihanbayarActionPerformed
    String metode = (String) pilihanbayar.getSelectedItem();
    if ("Cash".equalsIgnoreCase(metode)) {
        PembayaranCash.setVisible(true);
        boxuang.setText("");
        txtkembalian.setText("Uang Kembali :");
    } else {
        PembayaranCash.setVisible(false);
    }
    }//GEN-LAST:event_pilihanbayarActionPerformed

    private void btnbayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbayarActionPerformed
    String userId = UserLogin.getSessionUser().getId();
    String metodeBaru = (String) pilihanbayar.getSelectedItem();

    try (Connection conn = DBKoneksi.getConnection()) {
        ModelPembayaran pembayaran = UserPembayaran.getPembayaranPending(conn, userId);
        if (pembayaran == null) {
            JOptionPane.showMessageDialog(this, "Transaksi ini sudah Lunas");
            return;
        }

        String orderId = pembayaran.getOrderId();
        String metodeLama = pembayaran.getMetodePembayaran();

        // === CEK STOK CUKUP UNTUK SEMUA METODE ===
        try (PreparedStatement ps = conn.prepareStatement(
           "SELECT dp.jumlah_barang, p.stok, p.id_produk, p.nama " +
           "FROM detail_transaksi dp " +
           "JOIN produk p ON dp.produk_id_produk = p.id_produk " +
           "WHERE dp.pembayaran_order_id = ?")) {

            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int jumlah     = rs.getInt("jumlah_barang");
                    int stok       = rs.getInt("stok");
                    String idProduk   = rs.getString("id_produk");
                    String namaProduk = rs.getString("nama");  // pakai "nama", bukan "nama_produk"

                    if (stok < jumlah) {
                        JOptionPane.showMessageDialog(this,
                            "Transaksi gagal. Stok produk tidak mencukupi.\n\n" +
                            "Detail Produk:\n" +
                            "ID Produk     : " + idProduk + "\n" +
                            "Nama Produk   : " + namaProduk + "\n" +
                            "Stok Saat Ini : " + stok + "\n" +
                            "Jumlah Dibeli : " + jumlah + "\n\n" +
                            "Silakan perbarui keranjang atau hubungi admin.");
                        return;
                    }
                }
            }
        }


        // === 1. UserPembayaran Cash ===
        if ("Cash".equalsIgnoreCase(metodeBaru)) {
            long total = pembayaran.getTotal();
            long nominal;

            try {
                nominal = Long.parseLong(boxuang.getText().replaceAll("[^\\d]", ""));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Masukkan nominal pembayaran yang valid.");
                return;
            }

            if (nominal < total) {
                JOptionPane.showMessageDialog(this, "Masukkan nominal yang benar, kurang dari total.");
                return;
            }

            try {
                conn.setAutoCommit(false);

                UserPembayaran.kurangiStokProduk(conn, orderId, userId);

                boolean updated = UserPembayaran.updateStatusPembayaran(conn, orderId, "lunas", "Cash");
                if (!updated) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Gagal update status pembayaran.");
                    return;
                }

                conn.commit();
                conn.setAutoCommit(true);

                long kembalian = nominal - total;
                txtkembalian.setText("Uang Kembali : " + formatRupiah(kembalian));
                UserMenuStruk.setLastCashPayment(nominal, kembalian);

                JOptionPane.showMessageDialog(this, "Pembayaran berhasil! Status lunas dan stok diperbarui.");

            } catch (Exception e) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Transaksi gagal: " + e.getMessage());
            }
            return;
        }

        // === 2. Pertama kali pilih metode (belum ada metode sebelumnya)
        if (metodeLama == null || metodeLama.isEmpty()) {
            String metodeMidtransBaru = mapMetodeToMidtrans(metodeBaru);
            if (metodeMidtransBaru == null) {
                JOptionPane.showMessageDialog(this, "Metode pembayaran tidak valid atau belum dipilih.");
                return;
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(
                "UPDATE pembayaran SET metode_pembayaran = ?, snap_token = NULL, payment_url = NULL WHERE order_id = ?")) {
                psUpdate.setString(1, metodeMidtransBaru);
                psUpdate.setString(2, orderId);
                psUpdate.executeUpdate();
            }

            prosesMidtrans(conn, orderId, metodeBaru, true);
            return;
        }

        // === 3. Metode sama, proses ulang Midtrans
        if (metodeBaru.equalsIgnoreCase(metodeLama)) {
            prosesMidtrans(conn, orderId, metodeBaru, false);
            return;
        }

        // === 4. Konfirmasi ganti metode
        int confirm = JOptionPane.showConfirmDialog(this,
            "Metode pembayaran saat ini adalah '" + metodeLama + "'.\n" +
            "Apakah Anda ingin mengganti metode pembayaran ke '" + metodeBaru + "'?",
            "Konfirmasi Ganti Metode Pembayaran",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.NO_OPTION) {
            prosesMidtrans(conn, orderId, metodeLama, false);
            return;
        }

        // === 5. Update dengan order_id baru, metode baru
        String orderIdBaru = UserPembayaran.generateOrderIdByTimestamp(userId);
        String metodeMidtransBaru = mapMetodeToMidtrans(metodeBaru);
        if (metodeMidtransBaru == null) {
            JOptionPane.showMessageDialog(this, "Metode pembayaran tidak valid.");
            return;
        }

        try (PreparedStatement psUpdate = conn.prepareStatement(
            "UPDATE pembayaran SET order_id = ?, metode_pembayaran = ?, snap_token = NULL, payment_url = NULL, status = 'pending' WHERE order_id = ?")) {
            psUpdate.setString(1, orderIdBaru);
            psUpdate.setString(2, metodeMidtransBaru);
            psUpdate.setString(3, orderId);
            int updated = psUpdate.executeUpdate();
            if (updated == 0) {
                JOptionPane.showMessageDialog(this, "Gagal update order_id/metode pembayaran.");
                return;
            }
        }

        prosesMidtrans(conn, orderIdBaru, metodeBaru, true) ;

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memproses pembayaran: " + e.getMessage());
    }
    }//GEN-LAST:event_btnbayarActionPerformed

    private void btnstrukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnstrukActionPerformed
        try (Connection conn = DBKoneksi.getConnection()) {
            String orderId = UserPembayaran.getOrderIdPendingOrLunas(conn);

            if (!UserController.UserStruk.isLunas(conn, orderId)) {
                JOptionPane.showMessageDialog(this, "Pembayaran belum lunas. Tidak bisa cetak struk.");
                return;
            }

            Long nominal = UserMenuStruk.getLastNominalCash();
            Long kembalian = UserMenuStruk.getLastKembalian();

            String isiStruk = UserController.UserStruk.generateIsiStruk(conn, orderId, nominal, kembalian);

            UserView.UserMenuStruk vstruk = new UserView.UserMenuStruk();
            vstruk.setStrukText(isiStruk);
            vstruk.setLocationRelativeTo(null);
            vstruk.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }//GEN-LAST:event_btnstrukActionPerformed

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PembayaranCash;
    private javax.swing.JTextField boxuang;
    private javax.swing.JButton btnbayar;
    private javax.swing.JButton btnkembali;
    private javax.swing.JButton btnstruk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JComboBox<String> pilihanbayar;
    private javax.swing.JLabel txtidtransaksi;
    private javax.swing.JLabel txtjumlahitem;
    private javax.swing.JLabel txtkembalian;
    private javax.swing.JLabel txttanggal;
    private javax.swing.JLabel txttotal;
    private javax.swing.JLabel txttotalbarang;
    // End of variables declaration//GEN-END:variables
}
