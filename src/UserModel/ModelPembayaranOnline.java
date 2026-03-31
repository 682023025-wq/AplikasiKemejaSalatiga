package UserModel;

public class ModelPembayaranOnline {
    private String snapToken;
    private String redirectUrl;

    public ModelPembayaranOnline() {}

    public ModelPembayaranOnline(String snapToken, String redirectUrl) {
        this.snapToken = snapToken;
        this.redirectUrl = redirectUrl;
    }

    public String getSnapToken() {
        return snapToken;
    }

    public void setSnapToken(String snapToken) {
        this.snapToken = snapToken;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
