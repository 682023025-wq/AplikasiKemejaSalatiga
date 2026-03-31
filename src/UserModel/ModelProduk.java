package UserModel;

public class ModelProduk {
    private String idProduk;
    private String nama;
    private String kategori;
    private String ukuran;
    private long harga;
    private int stok;
    private String gambarUrl;
    private String idKategori;
    private String idUkuran;

    public ModelProduk() {}

    public ModelProduk(String idProduk, String nama, String kategori, String ukuran, long harga, int stok,
                       String gambarUrl, String idKategori, String idUkuran) {
        this.idProduk = idProduk;
        this.nama = nama;
        this.kategori = kategori;
        this.ukuran = ukuran;
        this.harga = harga;
        this.stok = stok;
        this.gambarUrl = gambarUrl;
        this.idKategori = idKategori;
        this.idUkuran = idUkuran;
    }

    // Getter dan Setter
    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }

    public void setGambarUrl(String gambarUrl) {
        this.gambarUrl = gambarUrl;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getIdUkuran() {
        return idUkuran;
    }

    public void setIdUkuran(String idUkuran) {
        this.idUkuran = idUkuran;
    }
}
