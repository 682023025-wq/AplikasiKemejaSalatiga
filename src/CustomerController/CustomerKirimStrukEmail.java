package CustomerController;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.sql.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

public class CustomerKirimStrukEmail {


    public static void kirimStrukLangsung(String kepada, String subject, String isiStruk) throws Exception {
        final String dari = "raihanagilm@gmail.com";
        final String appPassword = "ygma idrl knhl rnqa"; 
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(dari, appPassword);
            }
        });

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        document.add(new Paragraph(isiStruk, font));
        document.close();

        String previewPath = "assets/preview_struk/preview_struk_" + System.currentTimeMillis() + ".pdf";
        try (FileOutputStream fos = new FileOutputStream(previewPath)) {
            fos.write(outputStream.toByteArray());
            System.out.println(" PDF preview berhasil disimpan di: " + new File(previewPath).getAbsolutePath());
        }

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(dari));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(kepada));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Berikut struk pembayaran Anda dalam format PDF.", "utf-8");

        MimeBodyPart pdfPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");
        pdfPart.setDataHandler(new DataHandler(source));
        pdfPart.setFileName("struk_" + System.currentTimeMillis() + ".pdf");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(pdfPart);
        message.setContent(multipart);

        System.out.println(" Email struk berhasil dikirim ke: " + kepada);
    }
}
