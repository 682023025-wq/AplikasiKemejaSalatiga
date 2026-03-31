/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UserModel;

import java.util.Date;

/**
 *
 * @author LAPTOP LENOVO
 */
public class ModelSalesReport {
   
    private String ID_Transaksi;
    private Date Tanggal_transaksi;
    private String nama;
    private int jumlah_barang;
    private double harga_barang;
    private double total_harga;

    public String getID_Transaksi() {
        return ID_Transaksi;
    }

    public void setID_Transaksi(String ID_Transaksi) {
        this.ID_Transaksi = ID_Transaksi;
    }

    public Date getTanggal_transaksi() {
        return Tanggal_transaksi;
    }

    public void setTanggal_transaksi(Date Tanggal_transaksi) {
        this.Tanggal_transaksi = Tanggal_transaksi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getJumlah_barang() {
        return jumlah_barang;
    }

    public void setJumlah_barang(int jumlah_barang) {
        this.jumlah_barang = jumlah_barang;
    }

    public double getHarga_barang() {
        return harga_barang;
    }

    public void setHarga_barang(double harga_barang) {
        this.harga_barang = harga_barang;
    }

    public double getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(double total_harga) {
        this.total_harga = total_harga;
    }
}


