/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UserView;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import UserModel.ModelProductManagement;
import UserController.UserProductManagement;
import CustomUI.Toast;
import java.sql.Blob;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;



/**
 *
 * @author Heptalogue
 */
public class UserMenuProduct_Management extends javax.swing.JFrame {

    /**
     * Creates new form ModelProductManagement
     */
    // Tambahkan ini di class-level (atas, di luar constructor)
private List<ModelProductManagement> produkList;

public UserMenuProduct_Management() {
    initComponents();
    loadTableProduk();
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    btnaddimage.setEnabled(false);
    btnremoveimage.setEnabled(false);
    btnnew.setEnabled(true);
    btnsave.setEnabled(false);
    btndelete.setEnabled(false);
    btnaddimage.setEnabled(false);

}

private void loadTableProduk() {
    btnnew.setEnabled(false);
    UserProductManagement controller = new UserProductManagement();
    produkList = controller.getAllProducts();

    String[] kolom = {"ID", "Nama", "Kategori", "Ukuran", "Harga", "Stok"};
    DefaultTableModel tableModel = new DefaultTableModel(kolom, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    for (ModelProductManagement p : produkList) {
        Object[] rowData = {
            p.getId(),
            p.getNama(),
            p.getKategori(),
            p.getUkuran(),
            p.getHarga(),
            p.getStok()
        };
        tableModel.addRow(rowData);
    }

    tablesearch.setModel(tableModel);
    cekDanTampilkanStokTipis();
}


public void cekDanTampilkanStokTipis() {
    btnnew.setEnabled(false);
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

        new Toast(this, pesan.toString(), "info", () -> {
            // Filter tabel hanya stok < 10 saat toast diklik
            String[] kolom = {"ID", "Nama", "Kategori", "Ukuran", "Harga", "Stok"};
            DefaultTableModel filteredModel = new DefaultTableModel(kolom, 0);
            for (ModelProductManagement p : stokTipisList) {

                Object[] rowData = {
                    p.getId(),
                    p.getNama(),
                    p.getKategori(),
                    p.getUkuran(),
                    p.getHarga(),
                    p.getStok()
                };
                filteredModel.addRow(rowData);
            }
            this.produkList = stokTipisList;
            tablesearch.setModel(filteredModel);
        });
    }
}

private void loadAndFilterProduk(List<ModelProductManagement> fullList, String keyword) {
    btnnew.setEnabled(false);
    this.produkList = new ArrayList<>(); 
    btnnew.setEnabled(false);

    String[] kolom = {"ID", "Nama", "Kategori", "Ukuran", "Harga", "Stok"};
    DefaultTableModel tableModel = new DefaultTableModel(kolom, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    keyword = keyword.trim().toLowerCase();

    for (ModelProductManagement p : fullList) {
        cmbkategori.setSelectedItem(p.getKategori());

        if (keyword.isEmpty() || 
            p.getNama().toLowerCase().contains(keyword) ||
            p.getKategori().toLowerCase().contains(keyword) ||
            p.getId().toLowerCase().contains(keyword)) {
            
            Object[] rowData = {
                
                p.getId(), p.getNama(), p.getKategori(),
                p.getUkuran(), p.getHarga(), p.getStok()
            };
            tableModel.addRow(rowData);
            this.produkList.add(p); // sinkronkan list yang ditampilkan
        }
    }

    tablesearch.setModel(tableModel);
}

// 1. Tambahkan di class UserMenuProduct_Management:
private boolean validasiForm() {
    // Cek nama produk
    if (fieldnamaproduk.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama produk wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        fieldnamaproduk.requestFocus();
        return false;
    }
    // Cek kategori
    if (cmbkategori.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Silakan pilih kategori!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        cmbkategori.requestFocus();
        return false;
    }
    // Cek ukuran
    if (cmbukuran.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Silakan pilih ukuran!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        cmbukuran.requestFocus();
        return false;
    }
    // Cek harga
    String hargaStr = fieldharga.getText().trim();
    if (hargaStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Harga wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        fieldharga.requestFocus();
        return false;
    }
    try {
        Integer.parseInt(hargaStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        fieldharga.requestFocus();
        return false;
    }
    // Cek stok
    String stokStr = fieldstok.getText().trim();
    if (stokStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Stok wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        fieldstok.requestFocus();
        return false;
    }
    try {
        Integer.parseInt(stokStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        fieldstok.requestFocus();
        return false;
    }
    btnaddimage.setEnabled(false);
    btnremoveimage.setEnabled(false);
    return true;
    
}




    
    

    //Load Data Search//


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        fieldsearch = new javax.swing.JTextField();
        btnreset = new javax.swing.JButton();
        btnkembali = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablesearch = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        fieldidproduk = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fieldstok = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        fieldharga = new javax.swing.JTextField();
        fieldnamaproduk = new javax.swing.JTextField();
        autoincrementbtn = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        textgambar = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnaddimage = new javax.swing.JButton();
        btnremoveimage = new javax.swing.JButton();
        cmbkategori = new javax.swing.JComboBox<>();
        cmbukuran = new javax.swing.JComboBox<>();
        btndelete = new javax.swing.JButton();
        btnnew = new javax.swing.JButton();
        btnsave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(41, 128, 185));

        jLabel1.setBackground(new java.awt.Color(51, 51, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PRODUCT MANAGEMENT");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Search :");

        fieldsearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldsearchKeyReleased(evt);
            }
        });

        btnreset.setBackground(new java.awt.Color(102, 102, 102));
        btnreset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnreset.setText("Reset");
        btnreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnresetActionPerformed(evt);
            }
        });
        btnreset.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnresetKeyPressed(evt);
            }
        });

        btnkembali.setText("Kembali");
        btnkembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnkembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnreset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnkembali)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnreset)
                    .addComponent(btnkembali))
                .addContainerGap())
        );

        tablesearch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nama", "Kategori", "Ukuran", "Stok", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablesearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablesearchMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablesearch);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Produk ID :");

        fieldidproduk.setEnabled(false);
        fieldidproduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldidprodukActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Kategori :");

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Nama Produk: ");

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Ukuran : ");

        fieldstok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldstokActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Harga :");

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Stok :");

        fieldharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldhargaActionPerformed(evt);
            }
        });

        fieldnamaproduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldnamaprodukActionPerformed(evt);
            }
        });

        autoincrementbtn.setBackground(new java.awt.Color(0, 255, 51));
        autoincrementbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        autoincrementbtn.setForeground(new java.awt.Color(255, 255, 255));
        autoincrementbtn.setText("Auto");
        autoincrementbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoincrementbtnActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textgambar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textgambar, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
        );

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Gambar");

        btnaddimage.setBackground(new java.awt.Color(51, 102, 255));
        btnaddimage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnaddimage.setText("Add Image");
        btnaddimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddimageActionPerformed(evt);
            }
        });

        btnremoveimage.setBackground(new java.awt.Color(255, 0, 51));
        btnremoveimage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnremoveimage.setText("Remove Image");
        btnremoveimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnremoveimageActionPerformed(evt);
            }
        });

        cmbkategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Pilih Kategori", "Celana Pendek", "Kaos Lengan Panjang", "Kemeja Lengan Pendek", "Celana Panjang", "Jeans", "Kaos Lengan Pendek", "Kemeja Lengan Panjang" }));
        cmbkategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbkategoriActionPerformed(evt);
            }
        });

        cmbukuran.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Ukuran", "S", "M", "L", "XL", "XXL", "XXXL" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(130, 130, 130))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(fieldstok, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fieldnamaproduk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fieldharga, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(fieldidproduk, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoincrementbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cmbukuran, javax.swing.GroupLayout.Alignment.LEADING, 0, 244, Short.MAX_VALUE)
                        .addComponent(cmbkategori, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(btnaddimage, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnremoveimage))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(fieldidproduk)
                                    .addComponent(autoincrementbtn)))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(fieldnamaproduk, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbkategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbukuran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(fieldharga, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(fieldstok, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnaddimage, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnremoveimage, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btndelete.setBackground(new java.awt.Color(255, 0, 51));
        btndelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btndelete.setText("Delete");
        btndelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndeleteActionPerformed(evt);
            }
        });

        btnnew.setBackground(new java.awt.Color(0, 153, 51));
        btnnew.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnnew.setText("New");
        btnnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnewActionPerformed(evt);
            }
        });

        btnsave.setBackground(new java.awt.Color(41, 128, 185));
        btnsave.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnsave.setText("Save");
        btnsave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnnew, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(btnsave, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btndelete, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(1, 1, 1)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnnew)
                    .addComponent(btndelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnsave)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(335, 335, 335)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addGap(280, 280, 280))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnewActionPerformed
    if (!validasiForm()) {
        return;
    }

    ModelProductManagement product = new ModelProductManagement();
    btnnew.setEnabled(false);

    product.setId(fieldidproduk.getText());
    product.setNama(fieldnamaproduk.getText());
    product.setKategori(cmbkategori.getSelectedItem().toString());
    product.setUkuran(cmbukuran.getSelectedItem().toString());
    product.setHarga(Integer.parseInt(fieldharga.getText().trim()));
    product.setStok(Integer.parseInt(fieldstok.getText().trim()));

    UserProductManagement controller = new UserProductManagement();
    boolean success = controller.saveProduct(product);

    if (success) {
        JOptionPane.showMessageDialog(this, "Produk berhasil disimpan!");
        loadTableProduk();
        fieldidproduk.setText("");
        fieldnamaproduk.setText("");
        cmbkategori.setSelectedIndex(0);   // <-- reset combobox
        cmbukuran.setSelectedIndex(0);
        fieldharga.setText("");
        fieldstok.setText("");
        fieldsearch.setText("");

    } else {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan produk.");
        btnnew.setEnabled(true);
        loadTableProduk();
    }
    }//GEN-LAST:event_btnnewActionPerformed

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
        btnnew.setEnabled(false);

        String id = fieldidproduk.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data dari tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus produk dengan ID: " + id + "?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            UserProductManagement controller = new UserProductManagement();
            boolean berhasil = controller.deleteProduct(id);

            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTableProduk();
                fieldidproduk.setText("");
                fieldnamaproduk.setText("");
                cmbkategori.setSelectedIndex(0); 
                cmbukuran.setSelectedIndex(0);
                fieldharga.setText("");
                fieldstok.setText("");
                fieldsearch.setText("");
                btnsave.setEnabled(false);
                btndelete.setEnabled(false);
                btnaddimage.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus produk!", "Error", JOptionPane.ERROR_MESSAGE);
                btndelete.setEnabled(true);
            }
        }
    }//GEN-LAST:event_btndeleteActionPerformed

    private void fieldstokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldstokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldstokActionPerformed

    private void btnsaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsaveActionPerformed
        btnnew.setEnabled(false);
        

        if (fieldidproduk.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
        }
        

        if (!validasiForm()) {
            return;
        }

        ModelProductManagement produk = new ModelProductManagement();
        produk.setId(fieldidproduk.getText());
        produk.setNama(fieldnamaproduk.getText());
        produk.setKategori(cmbkategori.getSelectedItem().toString());
        produk.setUkuran(cmbukuran.getSelectedItem().toString());
        produk.setHarga(Integer.parseInt(fieldharga.getText().trim()));
        produk.setStok(Integer.parseInt(fieldstok.getText().trim()));
        UserProductManagement controller = new UserProductManagement();
        boolean berhasil = controller.updateProduct(produk);

        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTableProduk();
            fieldidproduk.setText("");
            fieldnamaproduk.setText("");
            cmbkategori.setSelectedIndex(0); 
            cmbukuran.setSelectedIndex(0);
            fieldharga.setText("");
            fieldstok.setText("");
            fieldsearch.setText("");
            btnsave.setEnabled(false);
            btndelete.setEnabled(false);
            btnaddimage.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data!", "Error", JOptionPane.ERROR_MESSAGE);
            btnsave.setEnabled(false);
        }
    }//GEN-LAST:event_btnsaveActionPerformed

    private void fieldhargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldhargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldhargaActionPerformed

    private void btnresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnresetActionPerformed
        loadTableProduk();
        btnnew.setEnabled(true);
        fieldidproduk.setText("");
        fieldnamaproduk.setText("");
        cmbkategori.setSelectedIndex(0);   // <-- reset combobox
        cmbukuran.setSelectedIndex(0);
        fieldharga.setText("");
        fieldstok.setText("");
        fieldsearch.setText("");
        textgambar.setIcon(null);
        btnaddimage.setEnabled(true);
        btnremoveimage.setEnabled(false);
        btnsave.setEnabled(false);
        btndelete.setEnabled(false);
    }//GEN-LAST:event_btnresetActionPerformed

    private void fieldnamaprodukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldnamaprodukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldnamaprodukActionPerformed

    private void fieldidprodukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldidprodukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldidprodukActionPerformed

    private void autoincrementbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoincrementbtnActionPerformed
        btnaddimage.setEnabled(true);
        btnsave.setEnabled(false);
        btndelete.setEnabled(false);
        UserProductManagement controller = new UserProductManagement();
        btnnew.setEnabled(true);
        String newId = controller.generateNextProductId();
        fieldidproduk.setText(newId);
        fieldnamaproduk.setText("");
        cmbkategori.setSelectedIndex(0);
        cmbukuran.setSelectedIndex(0);
        fieldharga.setText("");
        fieldstok.setText("");
        fieldsearch.setText("");
        btnaddimage.setEnabled(false);
        btnremoveimage.setEnabled(false);
        textgambar.setIcon(null);
    }//GEN-LAST:event_autoincrementbtnActionPerformed

    private void btnremoveimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnremoveimageActionPerformed
        btnnew.setEnabled(false);

        String idProduk = fieldidproduk.getText();
        if (idProduk != null && !idProduk.isEmpty()) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus gambar dari produk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            UserProductManagement controller = new UserProductManagement();
            boolean success = controller.deleteImageById(idProduk);
            if (success) {
                JOptionPane.showMessageDialog(this, "Gambar berhasil dihapus dari database.");
                loadTableProduk();
                textgambar.setIcon(null);
                btnaddimage.setEnabled(true);
                btnremoveimage.setEnabled(false);

            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus gambar.");
                    }
            }
            } else {
                JOptionPane.showMessageDialog(this, "ID Produk tidak valid.");
            }   
    }//GEN-LAST:event_btnremoveimageActionPerformed

    private void btnaddimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddimageActionPerformed
    btnnew.setEnabled(false);

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Pilih Gambar");

    // Universal path relatif terhadap root project
    File defaultFolder = new File(System.getProperty("user.dir"), "assets/imgproduk");
    if (defaultFolder.exists() && defaultFolder.isDirectory()) {
        fileChooser.setCurrentDirectory(defaultFolder);
    } else {
        // Fallback jika folder tidak ditemukan
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    }

    int userSelection = fileChooser.showOpenDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();

        try {
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] imageBytes = fis.readAllBytes();

            // Simpan ke database
            UserProductManagement controller = new UserProductManagement();
            String id = fieldidproduk.getText();
            controller.updateProductImage(id, imageBytes);

            // Tampilkan ke JLabel
            ImageIcon icon = new ImageIcon(imageBytes);
            Image img = icon.getImage().getScaledInstance(248, 269, Image.SCALE_SMOOTH);
            textgambar.setIcon(new ImageIcon(img));

            btnaddimage.setEnabled(false);
            btnremoveimage.setEnabled(true);

            JOptionPane.showMessageDialog(this, "Gambar berhasil ditambahkan!");
            loadTableProduk();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membaca file gambar.");
        }
    }

    }//GEN-LAST:event_btnaddimageActionPerformed

    private void tablesearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablesearchMouseClicked
        btnnew.setEnabled(false);
        btnsave.setEnabled(true);
        btndelete.setEnabled(true);
        btnaddimage.setEnabled(true);
        btnremoveimage.setEnabled(true);
        int selectedRow = tablesearch.getSelectedRow();
        if (selectedRow >= 0 && produkList != null && selectedRow < produkList.size()) {
            try {
                ModelProductManagement produk = produkList.get(selectedRow);

                fieldidproduk.setText(produk.getId());
                fieldnamaproduk.setText(produk.getNama());
                cmbkategori.setSelectedItem(produk.getKategori());   // <-- disini
                cmbukuran.setSelectedItem(produk.getUkuran());
                fieldharga.setText(String.valueOf(produk.getHarga()));
                fieldstok.setText(String.valueOf(produk.getStok()));
                   // Cek & tampilkan gambar
                Blob blob = produk.getGambar();
                if (blob != null) {
                    byte[] imgBytes = blob.getBytes(1, (int) blob.length());
                    ImageIcon icon = new ImageIcon(imgBytes);
                    Image img = icon.getImage().getScaledInstance(248, 269, Image.SCALE_SMOOTH);
                    textgambar.setIcon(new ImageIcon(img));

                    btnaddimage.setEnabled(false);
                    btnremoveimage.setEnabled(true);
                } else {
                    textgambar.setIcon(null);

                    btnaddimage.setEnabled(true);
                    btnremoveimage.setEnabled(false);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                textgambar.setIcon(null);
                btnaddimage.setEnabled(true);
                btnremoveimage.setEnabled(false);
            }
        }
    }//GEN-LAST:event_tablesearchMouseClicked

    private void fieldsearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldsearchKeyReleased
        UserProductManagement controller = new UserProductManagement();
        List<ModelProductManagement> semuaProduk = controller.getAllProducts();

        String keyword = fieldsearch.getText();
        loadAndFilterProduk(semuaProduk, keyword);
    }//GEN-LAST:event_fieldsearchKeyReleased

    private void btnresetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnresetKeyPressed
    btnreset.doClick();
    }//GEN-LAST:event_btnresetKeyPressed

    private void btnkembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnkembaliActionPerformed
        new UserMenuUtama().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnkembaliActionPerformed

    private void cmbkategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbkategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbkategoriActionPerformed



    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton autoincrementbtn;
    private javax.swing.JButton btnaddimage;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btnkembali;
    private javax.swing.JButton btnnew;
    private javax.swing.JButton btnremoveimage;
    private javax.swing.JButton btnreset;
    private javax.swing.JButton btnsave;
    private javax.swing.JComboBox<String> cmbkategori;
    private javax.swing.JComboBox<String> cmbukuran;
    private javax.swing.JTextField fieldharga;
    private javax.swing.JTextField fieldidproduk;
    private javax.swing.JTextField fieldnamaproduk;
    private javax.swing.JTextField fieldsearch;
    private javax.swing.JTextField fieldstok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablesearch;
    private javax.swing.JLabel textgambar;
    // End of variables declaration//GEN-END:variables
}
