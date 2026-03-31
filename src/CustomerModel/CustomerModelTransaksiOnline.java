package CustomerModel;

import java.util.Date;

public class CustomerModelTransaksiOnline {
    private String idTransaksi;      // Misalnya: TCUS001
    private String customerId;       // ID customer, contoh: C001
    private Date tanggalTransaksi;   // Tanggal transaksi (java.util.Date)

    // Konstruktor kosong
    public CustomerModelTransaksiOnline() {
    }

    // Konstruktor lengkap
    public CustomerModelTransaksiOnline(String idTransaksi, String customerId, Date tanggalTransaksi) {
        this.idTransaksi = idTransaksi;
        this.customerId = customerId;
        this.tanggalTransaksi = tanggalTransaksi;
    }

    // Getter dan Setter
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Date tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }
}
