package CustomerModel;

public class CustomerModelPembayaran {
    private String orderId;
    private long total;
    private String metodePembayaran;
    private String tanggal;
    private String status;
    private String snapToken;
    private String paymentUrl;

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }
    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public String getTanggal() {
        return tanggal;
    }
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getSnapToken() {
        return snapToken;
    }
    public void setSnapToken(String snapToken) {
        this.snapToken = snapToken;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}
