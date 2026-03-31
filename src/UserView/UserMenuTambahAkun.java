/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UserView;
import Koneksi.DBKoneksi;
import UserController.UserLogin;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import UserController.UserTambahAkun;
import UserModel.ModelTambahAkun;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bryan
 */
public class UserMenuTambahAkun extends javax.swing.JFrame {
    private javax.swing.Timer debounceTimer;
    private File selectedFile = null;
    private boolean hapusGambar = false;
    private byte[] selectedImageBytes = null; 
    private UserTambahAkun controller = new UserTambahAkun();
    /**
     * Creates new form MenuTambahAkun
     */
    public UserMenuTambahAkun() {
        initComponents();
        tampilkanAutoID();   
        txtid.setEditable(false);
        searchLive();
        loadTableData();
    }
   

public void loadTableData() {
    String sql = "SELECT id, auth_type, username, password FROM user ORDER BY id ASC";

    try (Connection conn = DBKoneksi.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        // Override DefaultTableModel agar tidak bisa diedit
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Role", "Username", "Password"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua sel tidak bisa diedit
            }
        };

        while (rs.next()) {
            Object[] row = {
                rs.getString("id"),
                rs.getString("auth_type"),
                rs.getString("username"),
                rs.getString("password")
            };
            model.addRow(row);
        }

        tablesearch.setModel(model); 
        sembunyikanPasswordDiTable();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat data akun: " + e.getMessage(),
                "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
}

        private void tampilkanAutoID() {
        String autoId = controller.generateAutoID();
        txtid.setText(autoId);
    }

    private void resetForm() {
        txtid.setText("");
        txtrole.setSelectedItem("");
        txtusername.setText("");
        txtpassword.setText("");
        textgambar.setIcon(null);
        selectedFile = null;
        btnaddimage.setEnabled(true);
        btnremoveimage.setEnabled(false);
        tampilkanAutoID(); 
        sembunyikanPasswordDiTable();
    }
    
    private void sembunyikanPasswordDiTable() {
        int passwordColumnIndex = 3;
        tablesearch.getColumnModel().getColumn(passwordColumnIndex).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (value != null) {
                     String hidden = "*".repeat(value.toString().length()); // Buat karakter bintang sesuai panjang password
                     setText(hidden);
                    } else {
                     setText("");
                 }
                 return this;
             }
         });
    }
    
    private void searchLive() {
        debounceTimer = new javax.swing.Timer(400, e -> {
            String keyword = fieldsearch.getText().trim();
             if (keyword.isEmpty()) {
                 loadTableData();
                 sembunyikanPasswordDiTable();
            } else {
                controller.searchUser(tablesearch, keyword);
             }
             sembunyikanPasswordDiTable();
                });
                 debounceTimer.setRepeats(false); 
                    fieldsearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            restartDebounce();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            restartDebounce();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            restartDebounce();
        }

        private void restartDebounce() {
            if (debounceTimer.isRunning()) {
                debounceTimer.restart();
            } else {
                debounceTimer.start(); 
            }
            
        }
    });
    }
   
public void hapusGambarById(String id) {
    try (Connection conn = DBKoneksi.getConnection()) {
        String sql = "UPDATE user SET gambar = NULL WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, id);
        ps.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneltengah = new javax.swing.JPanel();
        panelatas = new CustomUI.PanelGradient1();
        jLabel5 = new javax.swing.JLabel();
        buttonOutLine1 = new CustomUI.ButtonOutLine();
        txtid = new CustomUI.TextFieldCustom();
        txtusername = new CustomUI.TextFieldCustom();
        txtpassword = new CustomUI.PasswordFieldCustom();
        panelgambar = new javax.swing.JPanel();
        textgambar = new javax.swing.JLabel();
        dataakun = new javax.swing.JLabel();
        btnaddimage = new CustomUI.Button();
        btnremoveimage = new CustomUI.Button();
        fotoprofile = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        role = new javax.swing.JLabel();
        password = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        btnsimpan = new CustomUI.Button();
        txtrole = new javax.swing.JComboBox<>();
        btnhapus = new CustomUI.Button();
        scrolltabel = new javax.swing.JScrollPane();
        tablesearch = new CustomUI.Table();
        panelsearch = new javax.swing.JPanel();
        fieldsearch = new CustomUI.SearchText();
        show = new javax.swing.JCheckBox();
        btnclear = new CustomUI.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        paneltengah.setBackground(new java.awt.Color(255, 255, 255));

        panelatas.setColorEnd(new java.awt.Color(204, 0, 51));
        panelatas.setColorStart(new java.awt.Color(255, 102, 102));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TAMBAH AKUN");

        buttonOutLine1.setForeground(new java.awt.Color(255, 255, 255));
        buttonOutLine1.setText("KEMBALI ");
        buttonOutLine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOutLine1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelatasLayout = new javax.swing.GroupLayout(panelatas);
        panelatas.setLayout(panelatasLayout);
        panelatasLayout.setHorizontalGroup(
            panelatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelatasLayout.createSequentialGroup()
                .addGap(455, 455, 455)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonOutLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        panelatasLayout.setVerticalGroup(
            panelatasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelatasLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel5)
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelatasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonOutLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        txtpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpasswordActionPerformed(evt);
            }
        });

        panelgambar.setBackground(new java.awt.Color(204, 204, 204));
        panelgambar.setPreferredSize(new java.awt.Dimension(75, 75));

        javax.swing.GroupLayout panelgambarLayout = new javax.swing.GroupLayout(panelgambar);
        panelgambar.setLayout(panelgambarLayout);
        panelgambarLayout.setHorizontalGroup(
            panelgambarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textgambar, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        panelgambarLayout.setVerticalGroup(
            panelgambarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textgambar, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        dataakun.setBackground(new java.awt.Color(153, 153, 153));
        dataakun.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        dataakun.setForeground(new java.awt.Color(153, 153, 153));
        dataakun.setText(" DATA AKUN ");

        btnaddimage.setBackground(new java.awt.Color(204, 0, 51));
        btnaddimage.setForeground(new java.awt.Color(255, 255, 255));
        btnaddimage.setText("ADD IMAGE");
        btnaddimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddimageActionPerformed(evt);
            }
        });

        btnremoveimage.setBackground(new java.awt.Color(0, 204, 255));
        btnremoveimage.setForeground(new java.awt.Color(255, 255, 255));
        btnremoveimage.setText("REMOVE IMAGE");
        btnremoveimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnremoveimageActionPerformed(evt);
            }
        });

        fotoprofile.setBackground(new java.awt.Color(153, 153, 153));
        fotoprofile.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fotoprofile.setForeground(new java.awt.Color(153, 153, 153));
        fotoprofile.setText("FOTO PROFILE");

        id.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        id.setForeground(new java.awt.Color(153, 153, 153));
        id.setText("ID");

        role.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        role.setForeground(new java.awt.Color(153, 153, 153));
        role.setText("Role");

        password.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        password.setForeground(new java.awt.Color(153, 153, 153));
        password.setText("Password");

        username.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        username.setForeground(new java.awt.Color(153, 153, 153));
        username.setText("Username");

        btnsimpan.setBackground(new java.awt.Color(51, 204, 0));
        btnsimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnsimpan.setText("SIMPAN");
        btnsimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpanActionPerformed(evt);
            }
        });

        txtrole.setBackground(new java.awt.Color(153, 153, 153));
        txtrole.setForeground(new java.awt.Color(153, 153, 153));
        txtrole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "kasir", "gudang", "akuntan" }));
        txtrole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtroleActionPerformed(evt);
            }
        });

        btnhapus.setBackground(new java.awt.Color(255, 0, 51));
        btnhapus.setForeground(new java.awt.Color(255, 255, 255));
        btnhapus.setText("HAPUS");
        btnhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });

        tablesearch.setModel(new javax.swing.table.DefaultTableModel(
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
        tablesearch.getTableHeader().setReorderingAllowed(false);
        tablesearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablesearchMouseClicked(evt);
            }
        });
        scrolltabel.setViewportView(tablesearch);
        if (tablesearch.getColumnModel().getColumnCount() > 0) {
            tablesearch.getColumnModel().getColumn(0).setResizable(false);
            tablesearch.getColumnModel().getColumn(1).setResizable(false);
            tablesearch.getColumnModel().getColumn(2).setResizable(false);
            tablesearch.getColumnModel().getColumn(3).setResizable(false);
        }

        panelsearch.setBackground(new java.awt.Color(255, 255, 255));
        panelsearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        fieldsearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fieldsearchMouseClicked(evt);
            }
        });
        fieldsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldsearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelsearchLayout = new javax.swing.GroupLayout(panelsearch);
        panelsearch.setLayout(panelsearchLayout);
        panelsearchLayout.setHorizontalGroup(
            panelsearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelsearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 833, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        panelsearchLayout.setVerticalGroup(
            panelsearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelsearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldsearch, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addContainerGap())
        );

        show.setText("Show");
        show.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showActionPerformed(evt);
            }
        });

        btnclear.setBackground(new java.awt.Color(51, 51, 255));
        btnclear.setForeground(new java.awt.Color(255, 255, 255));
        btnclear.setText("CLEAR");
        btnclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneltengahLayout = new javax.swing.GroupLayout(paneltengah);
        paneltengah.setLayout(paneltengahLayout);
        paneltengahLayout.setHorizontalGroup(
            paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(paneltengahLayout.createSequentialGroup()
                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneltengahLayout.createSequentialGroup()
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(paneltengahLayout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(username)
                                    .addComponent(password))
                                .addGap(35, 35, 35))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneltengahLayout.createSequentialGroup()
                                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(id)
                                    .addComponent(role))
                                .addGap(49, 49, 49)))
                        .addGap(10, 10, 10)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtusername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtrole, 0, 407, Short.MAX_VALUE)
                            .addComponent(txtid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(paneltengahLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(panelsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fotoprofile, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(108, 108, 108))
            .addGroup(paneltengahLayout.createSequentialGroup()
                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneltengahLayout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneltengahLayout.createSequentialGroup()
                                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(show, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(paneltengahLayout.createSequentialGroup()
                                        .addComponent(btnsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnclear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnhapus, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(22, 22, 22))
                            .addComponent(txtpassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(paneltengahLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(scrolltabel, javax.swing.GroupLayout.PREFERRED_SIZE, 879, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneltengahLayout.createSequentialGroup()
                        .addComponent(panelgambar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneltengahLayout.createSequentialGroup()
                        .addComponent(btnaddimage, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(btnremoveimage, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))))
            .addGroup(paneltengahLayout.createSequentialGroup()
                .addGap(436, 436, 436)
                .addComponent(dataakun)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        paneltengahLayout.setVerticalGroup(
            paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltengahLayout.createSequentialGroup()
                .addComponent(panelatas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dataakun)
                .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneltengahLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(fotoprofile)
                        .addGap(18, 18, 18)
                        .addComponent(panelgambar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnaddimage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnremoveimage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(56, Short.MAX_VALUE))
                    .addGroup(paneltengahLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrolltabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtid, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(id))
                        .addGap(18, 18, 18)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtrole, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(role))
                        .addGap(18, 18, 18)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(username)
                            .addComponent(txtusername, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(password)
                            .addComponent(txtpassword, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(show)
                        .addGap(18, 18, 18)
                        .addGroup(paneltengahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnhapus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneltengah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneltengah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_formWindowOpened

    private void btnaddimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddimageActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Pilih Gambar");
    fileChooser.setCurrentDirectory(new File("assets/fotouser/"));

    int userSelection = fileChooser.showOpenDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        selectedFile = fileChooser.getSelectedFile(); // Simpan sementara

        try {
            // Baca gambar sebagai byte[]
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] imageBytes = fis.readAllBytes();

            // Tampilkan gambar ke JLabel (resize)
            ImageIcon icon = new ImageIcon(imageBytes);
            Image img = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            textgambar.setIcon(new ImageIcon(img));

            // Ubah status tombol
            btnaddimage.setEnabled(false);
            btnremoveimage.setEnabled(true);

            JOptionPane.showMessageDialog(this, "Gambar berhasil dimuat!");

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membaca gambar.");
        }
    }
    }//GEN-LAST:event_btnaddimageActionPerformed

    private void btnremoveimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnremoveimageActionPerformed
    if (textgambar.getIcon() == null) {
        JOptionPane.showMessageDialog(this, "Tidak ada gambar untuk dihapus.");
        return;
    }

    textgambar.setIcon(null);
    selectedFile = null;
    hapusGambar = true; // << tandai untuk hapus di database
    btnremoveimage.setEnabled(false);
    btnaddimage.setEnabled(true);

    JOptionPane.showMessageDialog(this, "Gambar berhasil dihapus.");
    }//GEN-LAST:event_btnremoveimageActionPerformed

    private void txtpasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpasswordActionPerformed

    private void btnsimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpanActionPerformed
    String username = txtusername.getText().trim();
    String password = new String(txtpassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        String id = txtid.getText();
        String role = txtrole.getSelectedItem().toString();

        ModelTambahAkun user = new ModelTambahAkun();
        user.setId(id);
        user.setAuthType(role);
        user.setUsername(username);
        user.setPassword(UserLogin.hashPassword(password)); // Hash password sebelum disimpan

        // Handle gambar
       
        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                user.setGambar(fis.readAllBytes());
            }
        } else if (hapusGambar) {
            user.setGambar(null);
            hapusGambarById(id);

        } else {
            user.setGambar(controller.getGambarById(id)); // tetap pakai gambar lama
        }


        UserTambahAkun controller = new UserTambahAkun();
        boolean isExistingUser = controller.isUserExist(id);
        boolean success;

        if (isExistingUser) {
            success = controller.updateUser(user);
            JOptionPane.showMessageDialog(this, success ? "Data user berhasil diubah." : "Gagal mengubah data.");
        } else {
            success = controller.saveUser(user);
            JOptionPane.showMessageDialog(this, success ? "Akun baru berhasil disimpan!" : "Gagal menyimpan akun baru.");
        }

        if (success) {
            resetForm();
            tampilkanAutoID();
            loadTableData();
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data.");
    }

    sembunyikanPasswordDiTable();
    hapusGambar = false;
    }//GEN-LAST:event_btnsimpanActionPerformed

    private void txtroleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtroleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtroleActionPerformed

    private void showActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showActionPerformed
     if (show.isSelected()) {
        txtpassword.setEchoChar((char) 0); // Tampilkan karakter
    } else {
        txtpassword.setEchoChar('\u2022'); // Sembunyikan dengan bullet
    }
    }//GEN-LAST:event_showActionPerformed

    private void tablesearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablesearchMouseClicked
            int selectedRow = tablesearch.getSelectedRow();
        if (selectedRow != -1) {
        // Ambil data dari tabel
        String id = tablesearch.getValueAt(selectedRow, 0).toString();
        String role = tablesearch.getValueAt(selectedRow, 1).toString();
        String username = tablesearch.getValueAt(selectedRow, 2).toString();
        String password = tablesearch.getValueAt(selectedRow, 3).toString();
        

        // Set ke form
        txtid.setText(id);
        txtrole.setSelectedItem(role);
        txtusername.setText(username);
        txtpassword.setText(password);
        

        // Ambil dan tampilkan gambar dari controller
        UserTambahAkun controller = new UserTambahAkun();
        byte[] imageBytes = controller.getGambarById(id);

        if (imageBytes != null) {
            ImageIcon icon = new ImageIcon(imageBytes);
            Image img = icon.getImage().getScaledInstance(
            textgambar.getWidth(), textgambar.getHeight(), Image.SCALE_SMOOTH);
            textgambar.setIcon(new ImageIcon(img));
            btnaddimage.setEnabled(false);
            btnremoveimage.setEnabled(true);
                    

        } else {
            textgambar.setIcon(null);
            btnaddimage.setEnabled(true);
            btnremoveimage.setEnabled(false);
            
        }
    }
    }//GEN-LAST:event_tablesearchMouseClicked

    private void btnhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusActionPerformed
    String id = txtid.getText().trim();
    String username = txtusername.getText().trim();
    String password = new String(txtpassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validasi: ID harus dipilih (biasanya dari tabel)
    if (id.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu dari tabel untuk dihapus.");
        return;
    }

    // Konfirmasi sebelum hapus
    int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus user dengan ID " + id + "?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

    if (konfirmasi == JOptionPane.YES_OPTION) {
        UserTambahAkun controller = new UserTambahAkun();
        boolean success = controller.deleteUser(id);

        if (success) {
            JOptionPane.showMessageDialog(this, "User berhasil dihapus.");
            resetForm(); // kosongkan form
            tampilkanAutoID(); // tampilkan ID baru
            loadTableData(); // refresh tabel
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus user.");
        }
        sembunyikanPasswordDiTable();
    }
    }//GEN-LAST:event_btnhapusActionPerformed

    private void btnclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearActionPerformed
        resetForm();
    }//GEN-LAST:event_btnclearActionPerformed

    private void buttonOutLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOutLine1ActionPerformed
        new UserMenuUtama().setVisible(true);
        dispose();
    }//GEN-LAST:event_buttonOutLine1ActionPerformed

    private void fieldsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldsearchActionPerformed

    private void fieldsearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fieldsearchMouseClicked
        fieldsearch.setText("");
    }//GEN-LAST:event_fieldsearchMouseClicked

    /**
     * @param args the command line arguments
     */
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private CustomUI.Button btnaddimage;
    private CustomUI.Button btnclear;
    private CustomUI.Button btnhapus;
    private CustomUI.Button btnremoveimage;
    private CustomUI.Button btnsimpan;
    private CustomUI.ButtonOutLine buttonOutLine1;
    private javax.swing.JLabel dataakun;
    private CustomUI.SearchText fieldsearch;
    private javax.swing.JLabel fotoprofile;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel5;
    private CustomUI.PanelGradient1 panelatas;
    private javax.swing.JPanel panelgambar;
    private javax.swing.JPanel panelsearch;
    private javax.swing.JPanel paneltengah;
    private javax.swing.JLabel password;
    private javax.swing.JLabel role;
    private javax.swing.JScrollPane scrolltabel;
    private javax.swing.JCheckBox show;
    private CustomUI.Table tablesearch;
    private javax.swing.JLabel textgambar;
    private CustomUI.TextFieldCustom txtid;
    private CustomUI.PasswordFieldCustom txtpassword;
    private javax.swing.JComboBox<String> txtrole;
    private CustomUI.TextFieldCustom txtusername;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
