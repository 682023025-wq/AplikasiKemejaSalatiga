package CustomUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Toast extends JDialog {

    public Toast(JFrame parent, String pesan, String tipe) {
        this(parent, pesan, tipe, null);
    }

    public Toast(JFrame parent, String pesan, String tipe, Runnable onClickAction) {
        super(parent, false);
        setUndecorated(true);
        setAlwaysOnTop(true);

        // Panel latar belakang berbentuk kotak dengan sudut membulat
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(pesan);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.CENTER);

        // Warna berdasarkan tipe
        switch (tipe.toLowerCase()) {
            case "sukses":
                panel.setBackground(new Color(76, 175, 80));
                break;
            case "gagal":
                panel.setBackground(new Color(244, 67, 54));
                break;
            case "info":
            default:
                panel.setBackground(new Color(33, 150, 243));
                break;
        }

        // 🔥 Tambahkan aksi saat toast diklik
        if (onClickAction != null) {
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose();
                    onClickAction.run();
                }
            });
        }

        setContentPane(panel);
        pack();

        // Posisi pojok kanan bawah dari parent
        int x, y;
        if (parent != null && parent.isShowing()) {
            Point lokasi = parent.getLocationOnScreen();
            x = lokasi.x + parent.getWidth() - getWidth() - 20;
            y = lokasi.y + parent.getHeight() - getHeight() - 40;
        } else {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            x = screen.width - getWidth() - 20;
            y = screen.height - getHeight() - 50;
        }

        setLocation(x, y);

        // Timer hilang 10 detik
        Timer timer = new Timer(10000, e -> dispose());
        timer.setRepeats(false);
        timer.start();

        setVisible(true);
    }
}
