package UserModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModelStruk {
    private String namaProduk;
    private int jumlah;
    private long hargaSatuan;
    private long totalHarga;

    public ModelStruk(String namaProduk, int jumlah, long hargaSatuan, long totalHarga) {
        this.namaProduk = namaProduk;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.totalHarga = totalHarga;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public long getHargaSatuan() {
        return hargaSatuan;
    }

    public long getTotalHarga() {
        return totalHarga;
    }
}