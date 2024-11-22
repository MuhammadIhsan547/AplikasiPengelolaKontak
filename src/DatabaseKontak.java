
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseKontak {
    public static Connection KoneksiDatabase() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:C:E:\\Keluargaku\\Muhammad Ihsan\\Kuliah\\PBO2\\Latihan3\\DatabaseKontak.db");
            System.out.println("Koneksi berhasil!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver SQLite tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }

    public static void MembuatTabelKontak() {
        String sql = "CREATE TABLE IF NOT EXISTS kontak (\n"
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + " nama TEXT NOT NULL,\n"
                    + " no_telepon TEXT NOT NULL,\n"
                    + " kategori TEXT NOT NULL\n"
                    + ");";
        try (Connection conn = KoneksiDatabase();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void MenambahKontak(String nama, String noTelepon, String kategori) {
        String sql = "INSERT INTO kontak(nama, no_telepon, kategori) VALUES(?, ?, ?)";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, noTelepon);
            pstmt.setString(3, kategori);
            pstmt.executeUpdate();
            System.out.println("Kontak berhasil ditambahkan.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Map<String, String>> DapatkanKontak() {
        List<Map<String, String>> kontak = new ArrayList<>();
        String sql = "SELECT * FROM kontak";

        try (Connection conn = KoneksiDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> dataKontak = new HashMap<>();
                dataKontak.put("id", String.valueOf(rs.getInt("id")));
                dataKontak.put("nama", rs.getString("nama"));
                dataKontak.put("no_telepon", rs.getString("no_telepon"));
                dataKontak.put("kategori", rs.getString("kategori"));
                kontak.add(dataKontak);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return kontak;
    }

    public static void MemperbaruiKontak(int id, String nama, String noTelepon, String kategori) {
        String sql = "UPDATE kontak SET nama = ?, no_telepon = ?, kategori = ? WHERE id = ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, noTelepon);
            pstmt.setString(3, kategori);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("Kontak berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void MenghapusKontak(int id) {
        String sql = "DELETE FROM kontak WHERE id = ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Kontak berhasil dihapus.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static List<Map<String, String>> MencariKontak(String keyword) {
    List<Map<String, String>> ListKontak = new ArrayList<>();
    String sql = "SELECT * FROM kontak WHERE nama LIKE ? OR no_telepon LIKE ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Gunakan '%' untuk pencarian mirip (LIKE)
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, String> kontak = new HashMap<>();
                kontak.put("id", String.valueOf(rs.getInt("id")));
                kontak.put("nama", rs.getString("nama"));
                kontak.put("no_telepon", rs.getString("no_telepon"));
                kontak.put("kategori", rs.getString("kategori"));
                ListKontak.add(kontak);
            }
        } catch (SQLException e) {
            System.out.println("Error saat mencari kontak: " + e.getMessage());
        }
        return ListKontak;
    }
}
