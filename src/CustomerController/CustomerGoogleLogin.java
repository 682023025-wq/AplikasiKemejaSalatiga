package CustomerController;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import javax.json.*;

public class CustomerGoogleLogin {

    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:8888/";
    private static final int PORT = 8888;

    public static boolean loginDenganGoogleLangsung() {
        try {
            // 1. Buat URL untuk login
            String authURL = "https://accounts.google.com/o/oauth2/v2/auth?" +
                    "scope=email%20profile" +
                    "&access_type=offline" +
                    "&include_granted_scopes=true" +
                    "&response_type=code" +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") +
                    "&client_id=" + CLIENT_ID;

            // 2. Buka browser
            Desktop.getDesktop().browse(new URI(authURL));

            // 3. Jalankan server untuk menangkap redirect
            String code = tungguKodeDariRedirect();

            if (code == null) {
                System.err.println("Gagal mendapatkan authorization code.");
                return false;
            }

            // 4. Tukar code ke access token
            String token = tukarKodeDenganToken(code);

            if (token != null) {
                return ambilDataUser(token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String tungguKodeDariRedirect() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket socket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream os = socket.getOutputStream();

            String line;
            String kode = null;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("GET") && line.contains("code=")) {
                    int start = line.indexOf("code=") + 5;
                    int end = line.indexOf(" ", start);
                    String query = line.substring(start, end);
                    kode = URLDecoder.decode(query.split("&")[0], "UTF-8");
                }
                if (line.contains("HTTP/1.1")) break;
            }

            // Tampilkan halaman sukses
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n\r\n" +
                    "<h1>Login berhasil, silakan kembali ke aplikasi.</h1>";
            os.write(response.getBytes());

            reader.close();
            os.close();
            socket.close();

            return kode;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String tukarKodeDenganToken(String code) {
        try {
            URL url = new URL("https://oauth2.googleapis.com/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String params = "code=" + URLEncoder.encode(code, "UTF-8") +
                    "&client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") +
                    "&grant_type=authorization_code";

            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();

            if (conn.getResponseCode() == 200) {
                JsonReader reader = Json.createReader(conn.getInputStream());
                JsonObject json = reader.readObject();
                return json.getString("access_token");
            } else {
                System.err.println("Token exchange failed: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean ambilDataUser(String accessToken) {
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v2/userinfo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            JsonReader reader = Json.createReader(conn.getInputStream());
            JsonObject userInfo = reader.readObject();

            String email = userInfo.getString("email");
            String name = userInfo.getString("name");

            System.out.println("Login Google berhasil:");
            System.out.println("Nama: " + name);
            System.out.println("Email: " + email);

            return CustomerLogin.loginDenganGoogleSementara(name, email);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
