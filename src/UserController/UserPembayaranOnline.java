package UserController;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UserPembayaranOnline {
    public static class MidtransResponse {
        public String snapToken;
        public String redirectUrl;
    }

public static MidtransResponse getSnapToken(String orderId, long amount, String metodePembayaran) throws Exception {
    String serverKey = "SB-Mid-server-33P7SdeJwYtr_03zNmyYUwFz";
    String auth = Base64.getEncoder().encodeToString((serverKey + ":").getBytes());

    URL url = new URL("https://app.sandbox.midtrans.com/snap/v1/transactions");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setRequestProperty("Accept", "application/json");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Authorization", "Basic " + auth);

    JsonObject json = new JsonObject();

    JsonObject transactionDetails = new JsonObject();
    transactionDetails.addProperty("order_id", orderId);
    transactionDetails.addProperty("gross_amount", amount);

    json.add("transaction_details", transactionDetails);

    JsonArray payments = new JsonArray();
    payments.add(metodePembayaran); 
    json.add("enabled_payments", payments);

    try (OutputStream os = conn.getOutputStream()) {
        byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
        os.write(input);
    }

    int status = conn.getResponseCode();
    if (status != 201) {
        throw new RuntimeException("Gagal koneksi ke Midtrans, status: " + status);
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
        String response = br.readLine();
        JsonObject obj = JsonParser.parseString(response).getAsJsonObject();

        MidtransResponse hasil = new MidtransResponse();
        hasil.snapToken = obj.get("token").getAsString();
        hasil.redirectUrl = obj.get("redirect_url").getAsString();
        return hasil;
    }
}


}
