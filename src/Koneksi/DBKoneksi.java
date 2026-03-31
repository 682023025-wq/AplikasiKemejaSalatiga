package Koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBKoneksi {

    private static final String URL = "jdbc:mysql://db_host:3306/toko_baju";
    private static final String USER = "username";
    private static final String PASSWORD = "password";


    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.out.println("Gagal koneksi: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
}
