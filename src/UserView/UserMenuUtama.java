/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UserView;

import Koneksi.DBKoneksi;
import UserController.UserKeranjang;
import UserController.UserLogin;
import UserController.UserProductManagement;
import CustomUI.ImageUtils;
import static CustomUI.ImageUtils.cropCircularImage;
import CustomUI.Toast;
import UserModel.ModelUser;
import UserModel.ModelProductManagement;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author AGIL
 */
public final class UserMenuUtama extends javax.swing.JFrame {
    /**
     * Creates new form MenuUtama
     */
    public UserMenuUtama() {
        initComponents();
        role();
        dt();
        times();
        lbgambar.setIcon(getUserCircularImage(UserLogin.getSessionUser().getId()));
        Dashboard();
        loadProdukTerjualHariIni();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        tampilkanNotifikasiStokTipis();
    }

private void role() {
    ModelUser currentUser = UserController.UserLogin.getSessionUser();
    
    if (currentUser != null) {
        String role = currentUser.getAuthType().toLowerCase();

        btnMenuKasir.setEnabled(false);
        btnMenuAkuntan.setEnabled(false);
        btnMenuGudang.setEnabled(false);
        btnTambahAkun.setEnabled(false);

        java.awt.Font defaultFont = btnMenuKasir.getFont().deriveFont(java.awt.Font.PLAIN);
        btnMenuKasir.setFont(defaultFont);
        btnMenuAkuntan.setFont(defaultFont);
        btnMenuGudang.setFont(defaultFont);
        btnTambahAkun.setFont(defaultFont);

        switch (role) {
            case "admin":
                btnMenuKasir.setEnabled(true);
                btnMenuAkuntan.setEnabled(true);
                btnMenuGudang.setEnabled(true);
                btnTambahAkun.setEnabled(true);
                btnMenuKasir.setFont(btnMenuKasir.getFont().deriveFont(java.awt.Font.BOLD));
                btnMenuAkuntan.setFont(btnMenuAkuntan.getFont().deriveFont(java.awt.Font.BOLD));
                btnMenuGudang.setFont(btnMenuGudang.getFont().deriveFont(java.awt.Font.BOLD));
                btnTambahAkun.setFont(btnTambahAkun.getFont().deriveFont(java.awt.Font.BOLD));

                break;

            case "akuntan":
                btnMenuAkuntan.setEnabled(true);
                btnMenuAkuntan.setFont(btnMenuAkuntan.getFont().deriveFont(java.awt.Font.BOLD));
                break;

            case "gudang":
                btnMenuGudang.setEnabled(true);
                btnMenuGudang.setFont(btnMenuGudang.getFont().deriveFont(java.awt.Font.BOLD));
                break;

            case "kasir":
                btnMenuKasir.setEnabled(true);
                btnMenuKasir.setFont(btnMenuKasir.getFont().deriveFont(java.awt.Font.BOLD));
                break;
        }

        NamaUser.setText(currentUser.getUsername().toUpperCase() + " (" + currentUser.getAuthType().toUpperCase() + ")");
    }
}

private ImageIcon getUserCircularImage(String userId) {
    lbgambar.setText("");
    BufferedImage finalImage = null;

    try (Connection conn = DBKoneksi.getConnection()) {
        String sql = "SELECT gambar FROM user WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes("gambar");
                    if (imageBytes != null && imageBytes.length > 0) {
                        BufferedImage userImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        if (userImage != null) {
                            finalImage = cropCircularImage(userImage);
                        }
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    if (finalImage == null) {
        try {
            BufferedImage defaultImg = ImageIO.read(new File("assets/fotouser/default.png"));
            finalImage = cropCircularImage(defaultImg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    return finalImage != null ? new ImageIcon(finalImage) : null;
}



public void tampilkanNotifikasiStokTipis() {
    UserProductManagement controller = new UserProductManagement();
    List<ModelProductManagement> semuaProduk = controller.getAllProducts();

    List<ModelProductManagement> stokTipisList = new ArrayList<>();
    for (ModelProductManagement p : semuaProduk) {
        if (p.getStok() < 10) {
            stokTipisList.add(p);
        }
    }

    if (!stokTipisList.isEmpty()) {
        StringBuilder pesan = new StringBuilder("<html>Stok menipis:<br>");
        for (ModelProductManagement p : stokTipisList) {
            pesan.append(String.format("- %s (%d)<br>", p.getNama(), p.getStok()));
        }
        pesan.append("</html>");

        // Hanya menampilkan toast, tanpa aksi filter tabel
        new Toast(this, pesan.toString(), "info");
    }
}


    public void dt() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy",new Locale("id", "ID"));
        Tanggal.setText(sdf.format(now));
    }

    public void times() {
        Timer t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Jam.setText(sdf.format(date));
            }
        });
        t.start();
    }
    

public String cekTransaksiPending(Connection conn, String userId) throws SQLException {
    String sql = """
        SELECT t.id_transaksi 
        FROM transaksi t
        JOIN detail_transaksi dt ON t.id_transaksi = dt.transaksi_id_transaksi
        JOIN pembayaran p ON dt.pembayaran_order_id = p.order_id
        WHERE t.user_id = ? AND p.status = 'pending'
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

public void hapusTransaksi(Connection conn, String transaksiId) throws SQLException {
    String deleteDetail = "DELETE FROM detail_transaksi WHERE transaksi_id_transaksi = ?";
    String deleteTransaksi = "DELETE FROM transaksi WHERE id_transaksi = ?";

    try (PreparedStatement delDetail = conn.prepareStatement(deleteDetail);
         PreparedStatement delTrans = conn.prepareStatement(deleteTransaksi)) {

        delDetail.setString(1, transaksiId);
        delDetail.executeUpdate();

        delTrans.setString(1, transaksiId);
        delTrans.executeUpdate();
    }
}

private void Dashboard() {
    try (Connection conn = DBKoneksi.getConnection()) {

        // 1. Jumlah pelanggan hari ini yang lunas
        String sqlPelanggan = """
            SELECT COUNT(DISTINCT t.customer_id) AS jumlah_pelanggan
            FROM transaksi t
            JOIN detail_transaksi dt ON t.id_transaksi = dt.transaksi_id_transaksi
            JOIN pembayaran p ON dt.pembayaran_order_id = p.order_id
            WHERE DATE(p.tanggal) = CURDATE()
              AND p.status = 'lunas'
        """;
        try (PreparedStatement ps = conn.prepareStatement(sqlPelanggan);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lbpelanggan.setText(rs.getString("jumlah_pelanggan"));
            }
        }

        // 2. Produk terjual
        String sqlProduk = """
            SELECT SUM(jumlah_barang) AS jumlah_produk_terjual
            FROM detail_transaksi dt
            JOIN pembayaran p ON dt.pembayaran_order_id = p.order_id
            WHERE DATE(p.tanggal) = CURDATE()
                AND p.status = 'lunas'
        """;
        try (PreparedStatement ps = conn.prepareStatement(sqlProduk);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lbprodukterjual.setText(rs.getString("jumlah_produk_terjual"));
            }
        }

        // 3. Jumlah karyawan
        String sqlUser = "SELECT COUNT(*) AS jumlah_user FROM user";
        try (PreparedStatement ps = conn.prepareStatement(sqlUser);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lbtotalkaryawan.setText(rs.getString("jumlah_user"));
            }
        }

        // 4. Total penjualan hari ini
        String sqlTotal = """
            SELECT SUM(total) AS total_penjualan
            FROM pembayaran
            WHERE DATE(tanggal) = CURDATE()
              AND status = 'lunas'
        """;
        try (PreparedStatement ps = conn.prepareStatement(sqlTotal);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double total = rs.getDouble("total_penjualan");
                lbtotalpenjualan.setText("Rp " + String.format("%,.0f", total));
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load data dashboard: " + e.getMessage());
        e.printStackTrace();
    }
}

private void loadProdukTerjualHariIni() {
    DefaultTableModel model = new DefaultTableModel(
        new String[] {"No", "Nama Produk", "Jumlah Terjual", "Total Harga"}, 0
    );

    try (Connection conn = DBKoneksi.getConnection()) {
        String sql = """
            SELECT 
                pr.nama AS nama_produk,
                SUM(dt.jumlah_barang) AS jumlah_terjual,
                SUM(dt.total_harga) AS total
            FROM detail_transaksi dt
            JOIN produk pr ON dt.produk_id_produk = pr.id_produk
            JOIN pembayaran pb ON dt.pembayaran_order_id = pb.order_id
            WHERE DATE(pb.tanggal) = CURDATE()
              AND pb.status = 'lunas'
            GROUP BY pr.nama
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int no = 1;
            while (rs.next()) {
                model.addRow(new Object[] {
                    no++,
                    rs.getString("nama_produk"),
                    rs.getInt("jumlah_terjual"),
                    "Rp " + String.format("%,.0f", rs.getDouble("total"))
                });
            }
        }

        lblogtransaksi.setModel(model);
        lblogtransaksi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // biar nggak diubah otomatis
        autoResizeColumnWidth(lblogtransaksi);


    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load produk terjual: " + e.getMessage());
    }
}



public void autoResizeColumnWidth(JTable table) {
    lblogtransaksi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    TableColumnModel columnModel = lblogtransaksi.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(40);
    columnModel.getColumn(1).setPreferredWidth(300);
    columnModel.getColumn(2).setPreferredWidth(100);
    columnModel.getColumn(3).setPreferredWidth(200);

    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

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
        panelRound1 = new CustomUI.PanelRound();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblogtransaksi = new CustomUI.Table();
        jLabel2 = new javax.swing.JLabel();
        panelRound2 = new CustomUI.PanelRound();
        panelGradient1 = new CustomUI.PanelGradient();
        jLabel4 = new javax.swing.JLabel();
        lbtotalpenjualan = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panelGradient5 = new CustomUI.PanelGradient();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbprodukterjual = new javax.swing.JLabel();
        panelGradient6 = new CustomUI.PanelGradient();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbtotalkaryawan = new javax.swing.JLabel();
        panelGradient9 = new CustomUI.PanelGradient();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lbpelanggan = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnkeluar = new javax.swing.JButton();
        btnMenuKasir = new javax.swing.JButton();
        btnMenuGudang = new javax.swing.JButton();
        btnMenuAkuntan = new javax.swing.JButton();
        btnTambahAkun = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        NamaUser = new javax.swing.JLabel();
        Tanggal = new javax.swing.JLabel();
        Jam = new javax.swing.JLabel();
        lbgambar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(62, 89, 142));

        panelRound1.setBackground(new java.awt.Color(255, 255, 255));
        panelRound1.setForeground(new java.awt.Color(255, 255, 255));
        panelRound1.setRoundBottomLeft(25);
        panelRound1.setRoundBottomRight(25);
        panelRound1.setRoundTopLeft(25);
        panelRound1.setRoundTopRight(25);

        lblogtransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lblogtransaksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(lblogtransaksi);
        if (lblogtransaksi.getColumnModel().getColumnCount() > 0) {
            lblogtransaksi.getColumnModel().getColumn(0).setResizable(false);
            lblogtransaksi.getColumnModel().getColumn(1).setResizable(false);
            lblogtransaksi.getColumnModel().getColumn(2).setResizable(false);
            lblogtransaksi.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("LOG TRANSAKSI HARI INI ");

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1)
                .addGap(21, 21, 21))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                .addGap(162, 162, 162)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(152, 152, 152))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(21, 21, 21))
        );

        panelRound2.setBackground(new java.awt.Color(255, 255, 255));
        panelRound2.setForeground(new java.awt.Color(255, 255, 255));
        panelRound2.setRoundBottomLeft(25);
        panelRound2.setRoundBottomRight(25);
        panelRound2.setRoundTopLeft(25);
        panelRound2.setRoundTopRight(25);

        panelGradient1.setColor1(new java.awt.Color(102, 51, 255));
        panelGradient1.setColor2(new java.awt.Color(153, 51, 255));
        panelGradient1.setPreferredSize(new java.awt.Dimension(200, 100));

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TOTAL PENJUALAN");

        lbtotalpenjualan.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lbtotalpenjualan.setForeground(new java.awt.Color(255, 255, 255));
        lbtotalpenjualan.setText("Rp. 0");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8_get_cash_70px.png"))); // NOI18N

        javax.swing.GroupLayout panelGradient1Layout = new javax.swing.GroupLayout(panelGradient1);
        panelGradient1.setLayout(panelGradient1Layout);
        panelGradient1Layout.setHorizontalGroup(
            panelGradient1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGradient1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGradient1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelGradient1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelGradient1Layout.createSequentialGroup()
                        .addComponent(lbtotalpenjualan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelGradient1Layout.setVerticalGroup(
            panelGradient1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGradient1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGroup(panelGradient1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGradient1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbtotalpenjualan)
                        .addGap(17, 17, 17))
                    .addGroup(panelGradient1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        panelGradient5.setColor1(new java.awt.Color(255, 0, 0));
        panelGradient5.setColor2(new java.awt.Color(255, 51, 51));
        panelGradient5.setPreferredSize(new java.awt.Dimension(200, 100));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8_box_70px.png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("TOTAL PRODUK");

        lbprodukterjual.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lbprodukterjual.setForeground(new java.awt.Color(255, 255, 255));
        lbprodukterjual.setText(" 0");

        javax.swing.GroupLayout panelGradient5Layout = new javax.swing.GroupLayout(panelGradient5);
        panelGradient5.setLayout(panelGradient5Layout);
        panelGradient5Layout.setHorizontalGroup(
            panelGradient5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGradient5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGradient5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(panelGradient5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelGradient5Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(lbprodukterjual)
                    .addContainerGap(163, Short.MAX_VALUE)))
        );
        panelGradient5Layout.setVerticalGroup(
            panelGradient5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGradient5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addGroup(panelGradient5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGradient5Layout.createSequentialGroup()
                    .addContainerGap(58, Short.MAX_VALUE)
                    .addComponent(lbprodukterjual)
                    .addGap(22, 22, 22)))
        );

        panelGradient6.setColor1(new java.awt.Color(0, 204, 0));
        panelGradient6.setColor2(new java.awt.Color(0, 204, 51));
        panelGradient6.setPreferredSize(new java.awt.Dimension(200, 100));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8_collaborator_male_70px.png"))); // NOI18N

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("USER KARYAWAN");

        lbtotalkaryawan.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lbtotalkaryawan.setForeground(new java.awt.Color(255, 255, 255));
        lbtotalkaryawan.setText(" 0");

        javax.swing.GroupLayout panelGradient6Layout = new javax.swing.GroupLayout(panelGradient6);
        panelGradient6.setLayout(panelGradient6Layout);
        panelGradient6Layout.setHorizontalGroup(
            panelGradient6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGradient6Layout.createSequentialGroup()
                .addGroup(panelGradient6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGradient6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10))
                    .addGroup(panelGradient6Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelGradient6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelGradient6Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(lbtotalkaryawan)
                    .addContainerGap(149, Short.MAX_VALUE)))
        );
        panelGradient6Layout.setVerticalGroup(
            panelGradient6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGradient6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(panelGradient6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGradient6Layout.createSequentialGroup()
                    .addContainerGap(55, Short.MAX_VALUE)
                    .addComponent(lbtotalkaryawan)
                    .addGap(22, 22, 22)))
        );

        panelGradient9.setColor1(new java.awt.Color(204, 204, 0));
        panelGradient9.setColor2(new java.awt.Color(153, 153, 0));
        panelGradient9.setPreferredSize(new java.awt.Dimension(200, 100));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8_conference_70px.png"))); // NOI18N

        jLabel19.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("PELANGGAN HARI INI");

        lbpelanggan.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lbpelanggan.setForeground(new java.awt.Color(255, 255, 255));
        lbpelanggan.setText(" 0");

        javax.swing.GroupLayout panelGradient9Layout = new javax.swing.GroupLayout(panelGradient9);
        panelGradient9.setLayout(panelGradient9Layout);
        panelGradient9Layout.setHorizontalGroup(
            panelGradient9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGradient9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelGradient9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lbpelanggan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addContainerGap())
        );
        panelGradient9Layout.setVerticalGroup(
            panelGradient9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGradient9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addGroup(panelGradient9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbpelanggan)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("DASHBOARD");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addComponent(panelGradient1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(panelGradient5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addComponent(panelGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(panelGradient9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(166, 166, 166))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel12)
                .addGap(20, 20, 20)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGradient5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(panelGradient1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGradient9, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(panelGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        btnkeluar.setText("Keluar");
        btnkeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnkeluarActionPerformed(evt);
            }
        });

        btnMenuKasir.setText("MENU KASIR");
        btnMenuKasir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuKasirActionPerformed(evt);
            }
        });

        btnMenuGudang.setText("MENU GUDANG");
        btnMenuGudang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuGudangActionPerformed(evt);
            }
        });

        btnMenuAkuntan.setText("MENU AKUNTAN");
        btnMenuAkuntan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuAkuntanActionPerformed(evt);
            }
        });

        btnTambahAkun.setText("TAMBAH AKUN");
        btnTambahAkun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahAkunActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(btnMenuKasir, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81)
                .addComponent(btnMenuGudang, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93)
                .addComponent(btnMenuAkuntan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTambahAkun)
                .addGap(82, 82, 82)
                .addComponent(btnkeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnkeluar, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(btnMenuKasir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMenuGudang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMenuAkuntan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambahAkun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );

        jPanel2.setBackground(new java.awt.Color(0, 204, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("APLIKASI TOKO");

        NamaUser.setText("USER");

        Tanggal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Tanggal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Tanggal.setText("Hari, Tanggal Bulan Tahun");

        Jam.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Jam.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        Jam.setText("00:00:00");

        lbgambar.setText("jLabel5");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbgambar)
                        .addGap(366, 366, 366)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(NamaUser, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Tanggal)))
                .addGap(18, 18, 18)
                .addComponent(Jam))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(lbgambar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jam)
                    .addComponent(Tanggal)
                    .addComponent(NamaUser))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnkeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnkeluarActionPerformed
        new UserMenuLogin().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnkeluarActionPerformed

    private void btnMenuGudangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuGudangActionPerformed
        String role = UserLogin.getSessionUser().getAuthType().toLowerCase();
        if (!role.equals("admin") && !role.equals("gudang")) {
            JOptionPane.showMessageDialog(this, "Akses ditolak. Hanya untuk role GUDANG atau ADMIN.");
            return;
        }

        new UserMenuProduct_Management().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuGudangActionPerformed

    private void btnMenuAkuntanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuAkuntanActionPerformed
        String role = UserLogin.getSessionUser().getAuthType().toLowerCase();

        if (!role.equals("admin") && !role.equals("akuntan")) {
            JOptionPane.showMessageDialog(this, "Akses ditolak. Hanya untuk role AKUNTAN atau ADMIN.");
            return;
        }

        new UserMenuSalesReport().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuAkuntanActionPerformed

    private void btnTambahAkunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahAkunActionPerformed
        String role = UserLogin.getSessionUser().getAuthType().toLowerCase();

        if (!role.equals("admin")) {
            JOptionPane.showMessageDialog(this, "Akses ditolak. Hanya untuk role ADMIN.");
            return;


        }
        new UserMenuTambahAkun().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnTambahAkunActionPerformed

    private void btnMenuKasirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuKasirActionPerformed
        String role = UserLogin.getSessionUser().getAuthType().toLowerCase();

        if (!role.equals("admin") && !role.equals("kasir")) {
            JOptionPane.showMessageDialog(this, "Akses ditolak. Hanya untuk role KASIR atau ADMIN.");
            return;
        }

        String userId = UserLogin.getSessionUser().getId();

        try (Connection conn = DBKoneksi.getConnection()) {
            String transaksiId = cekTransaksiPending(conn, userId); // panggil method baru

            if (transaksiId != null) {
                int opsi = JOptionPane.showConfirmDialog(this,
                    "Terdapat transaksi belum selesai (ID: " + transaksiId + ").\n" +
                    "Ingin hapus transaksi lama dan mulai baru?",
                    "Transaksi Belum Selesai",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (opsi == JOptionPane.YES_OPTION) {
                    hapusTransaksi(conn, transaksiId); // refactor ini juga ke method sendiri
                    JOptionPane.showMessageDialog(this, "Transaksi sebelumnya telah dihapus.");
                } else {
                    new UserMenuKeranjang().setVisible(true);
                    dispose();
                    return;
                }
            }

            new UserMenuKasir().setVisible(true);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal cek transaksi: " + e.getMessage());
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnMenuKasirActionPerformed

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jam;
    private javax.swing.JLabel NamaUser;
    private javax.swing.JLabel Tanggal;
    private javax.swing.JButton btnMenuAkuntan;
    private javax.swing.JButton btnMenuGudang;
    private javax.swing.JButton btnMenuKasir;
    private javax.swing.JButton btnTambahAkun;
    private javax.swing.JButton btnkeluar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbgambar;
    private CustomUI.Table lblogtransaksi;
    private javax.swing.JLabel lbpelanggan;
    private javax.swing.JLabel lbprodukterjual;
    private javax.swing.JLabel lbtotalkaryawan;
    private javax.swing.JLabel lbtotalpenjualan;
    private CustomUI.PanelGradient panelGradient1;
    private CustomUI.PanelGradient panelGradient5;
    private CustomUI.PanelGradient panelGradient6;
    private CustomUI.PanelGradient panelGradient9;
    private CustomUI.PanelRound panelRound1;
    private CustomUI.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables
}
