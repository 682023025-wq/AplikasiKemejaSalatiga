/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CustomerModel;

/**
 *
 * @author AGIL
 */

public class CustomerModelStruk {
    private String transaksiId;
    private String orderIdCustomer;
    private String namaCustomer;
    private String emailCustomer;
    private String alamatCustomer;
    private String namaProduk;
    private int jumlah;
    private long hargaSatuan;
    private long totalHarga;

    public CustomerModelStruk(String transaksiId, String orderIdCustomer, String namaCustomer, String emailCustomer, String alamatCustomer,
                      String namaProduk, int jumlah, long hargaSatuan, long totalHarga) {
        this.transaksiId = transaksiId;
        this.orderIdCustomer = orderIdCustomer;
        this.namaCustomer = namaCustomer;
        this.emailCustomer = emailCustomer;
        this.alamatCustomer = alamatCustomer;
        this.namaProduk = namaProduk;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.totalHarga = totalHarga;
    }

    public String getTransaksiId() {
        return transaksiId;
    }

    public void setTransaksiId(String transaksiId) {
        this.transaksiId = transaksiId;
    }

    public String getOrderIdCustomer() {
        return orderIdCustomer;
    }

    public void setOrderIdCustomer(String orderIdCustomer) {
        this.orderIdCustomer = orderIdCustomer;
    }

    public String getNamaCustomer() {
        return namaCustomer;
    }

    public void setNamaCustomer(String namaCustomer) {
        this.namaCustomer = namaCustomer;
    }

    public String getEmailCustomer() {
        return emailCustomer;
    }

    public void setEmailCustomer(String emailCustomer) {
        this.emailCustomer = emailCustomer;
    }

    public String getAlamatCustomer() {
        return alamatCustomer;
    }

    public void setAlamatCustomer(String alamatCustomer) {
        this.alamatCustomer = alamatCustomer;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public long getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(long hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public long getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(long totalHarga) {
        this.totalHarga = totalHarga;
    }
}

