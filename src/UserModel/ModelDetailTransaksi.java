package UserModel;

public class ModelDetailTransaksi {
    private String transaksiIdTransaksi;
    private String produkIdProduk;
    private long harga;
    private int jumlahBarang;
    private long totalHarga;
    private String pembayaranOrderId; // optional, bisa null

    public ModelDetailTransaksi() {
    }

    public ModelDetailTransaksi(String transaksiIdTransaksi, String produkIdProduk, long harga, int jumlahBarang, long totalHarga, String pembayaranOrderId) {
        this.transaksiIdTransaksi = transaksiIdTransaksi;
        this.produkIdProduk = produkIdProduk;
        this.harga = harga;
        this.jumlahBarang = jumlahBarang;
        this.totalHarga = totalHarga;
        this.pembayaranOrderId = pembayaranOrderId;
    }

    public String getTransaksiIdTransaksi() {
        return transaksiIdTransaksi;
    }

    public void setTransaksiIdTransaksi(String transaksiIdTransaksi) {
        this.transaksiIdTransaksi = transaksiIdTransaksi;
    }

    public String getProdukIdProduk() {
        return produkIdProduk;
    }

    public void setProdukIdProduk(String produkIdProduk) {
        this.produkIdProduk = produkIdProduk;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public int getJumlahBarang() {
        return jumlahBarang;
    }

    public void setJumlahBarang(int jumlahBarang) {
        this.jumlahBarang = jumlahBarang;
    }

    public long getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(long totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getPembayaranOrderId() {
        return pembayaranOrderId;
    }

    public void setPembayaranOrderId(String pembayaranOrderId) {
        this.pembayaranOrderId = pembayaranOrderId;
    }
}
