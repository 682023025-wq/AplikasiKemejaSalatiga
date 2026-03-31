/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UserModel;
import java.sql.Blob;
/**
 *
 * @author Heptalogue
 */
public class ModelProductManagement {

    private String id;
    private String Nama;
    private String Kategori;
    private String Ukuran;
    private int Harga;
    private int Stok;
    private Blob Gambar;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the Nama
     */
    public String getNama() {
        return Nama;
    }

    /**
     * @param Nama the Nama to set
     */
    public void setNama(String Nama) {
        this.Nama = Nama;
    }

    /**
     * @return the Kategori
     */
    public String getKategori() {
        return Kategori;
    }

    /**
     * @param Kategori the Kategori to set
     */
    public void setKategori(String Kategori) {
        this.Kategori = Kategori;
    }

    /**
     * @return the Ukuran
     */
    public String getUkuran() {
        return Ukuran;
    }

    /**
     * @param Ukuran the Ukuran to set
     */
    public void setUkuran(String Ukuran) {
        this.Ukuran = Ukuran;
    }

    /**
     * @return the Harga
     */
    public int getHarga() {
        return Harga;
    }

    /**
     * @param Harga the Harga to set
     */
    public void setHarga(int Harga) {
        this.Harga = Harga;
    }

    /**
     * @return the Stok
     */
    public int getStok() {
        return Stok;
    }

    /**
     * @param Stok the Stok to set
     */
    public void setStok(int Stok) {
        this.Stok = Stok;
    }
    public Blob getGambar() {
        return Gambar;
    }

    public void setGambar(Blob gambar) {
        this.Gambar = gambar;
    }
}

