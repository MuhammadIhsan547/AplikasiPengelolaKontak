# AplikasiPengelolaKontak
 Latihan3 - Muhammad Ihsan - 2210010286


---

## Deskripsi Program
Aplikasi Pengelola Kontak adalah aplikasi berbasis Java yang digunakan untuk menyimpan dan mengelola informasi kontak, seperti nama, nomor telepon, dan kategori. Data kontak disimpan di dalam database SQLite, memungkinkan pengguna untuk:
- Menambah, mengedit, dan menghapus kontak.
- Mengelompokkan kontak berdasarkan kategori (Keluarga, Teman, Kerja).
- Mencari kontak berdasarkan nama atau nomor telepon.
- Mengekspor daftar kontak ke file CSV.
- Mengimpor data kontak dari file CSV.

---

## Fitur Utama
1. **Manajemen Kontak:**
   - Tambah, ubah, dan hapus kontak.
   - Validasi input, termasuk memastikan nomor telepon hanya berisi angka dengan panjang 10-13 digit.

2. **Kategori Kontak:**
   - Pilih kategori (Keluarga, Teman, Kerja) menggunakan JComboBox.
   - Tampilkan kontak berdasarkan kategori.

3. **Pencarian Kontak:**
   - Cari kontak berdasarkan nama atau nomor telepon.
   - Hasil pencarian ditampilkan dalam JTable.

4. **Ekspor dan Impor CSV:**
   - Ekspor daftar kontak ke file CSV.
   - Impor data kontak dari file CSV untuk memperbarui database.

---

## Struktur Program
Program ini terdiri dari dua kelas utama:
1. **AplikasiPengelolaKontak:**  
   Bertanggung jawab untuk antarmuka pengguna dan logika operasi CRUD.
   
2. **DatabaseKontak:**  
   Bertanggung jawab untuk pengelolaan database SQLite, termasuk membuat tabel dan melakukan operasi data.

---

## Persyaratan Sistem
- **Java Development Kit (JDK)** 8 atau lebih tinggi.
- **SQLite JDBC Driver**.
- File database SQLite (`DatabaseKontak.db`) untuk penyimpanan data.

---

## Indikator Penilaian

| **No** | **Komponen**          | **Persentase** |
|--------|------------------------|----------------|
| 1      | Komponen GUI          | 20%            |
| 2      | Logika Program         | 30%            |
| 3      | Event Handling         | 15%            |
| 4      | Kesesuaian UI          | 15%            |
| 5      | Memenuhi variasi fitur | 20%            |

**Total:** 100%

---

## Penjelasan Fitur dan Kode Terkait

### **1. Operasi CRUD**

#### **a. Menambah Kontak**
Pengguna dapat menambahkan kontak baru dengan memasukkan nama, nomor telepon, dan kategori. Data tersebut akan disimpan dalam database.

**Kode terkait (GUI):**
```java
private void TombolTambahKontakActionPerformed(java.awt.event.ActionEvent evt) {
    String nama = FieldMasukkanNama.getText();
    String noTelepon = FieldMasukkanNomor.getText();
    String kategori = ComboBoxKategori.getSelectedItem().toString();

    if (nama.isEmpty() || noTelepon.isEmpty() || kategori.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua data harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    DatabaseKontak.MenambahKontak(nama, noTelepon, kategori);
    FieldMasukkanNama.setText("");
    FieldMasukkanNomor.setText("");
    ComboBoxKategori.setSelectedIndex(0);
    MenampilkanKontak();
}
```

**Kode terkait (Database):**
```java
public static void MenambahKontak(String nama, String noTelepon, String kategori) {
    String sql = "INSERT INTO kontak (nama, no_telepon, kategori) VALUES (?, ?, ?)";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nama);
        pstmt.setString(2, noTelepon);
        pstmt.setString(3, kategori);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

---

#### **b. Membaca Data Kontak**
Semua data kontak yang tersimpan ditampilkan dalam tabel.

**Kode terkait:**
```java
private void MenampilkanKontak() {
    List<Map<String, String>> kontak = DatabaseKontak.DapatkanKontak();
    DefaultTableModel model = (DefaultTableModel) TabelDaftarKontak.getModel();
    model.setRowCount(0); // Bersihkan tabel

    for (Map<String, String> data : kontak) {
        model.addRow(new Object[] {
            data.get("id"), 
            data.get("nama"), 
            data.get("no_telepon"), 
            data.get("kategori")
        });
    }
}
```

**Kode untuk mendapatkan data dari database:**
```java
public static List<Map<String, String>> DapatkanKontak() {
    List<Map<String, String>> kontak = new ArrayList<>();
    String sql = "SELECT * FROM kontak";
    try (Connection conn = KoneksiDatabase();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            Map<String, String> data = new HashMap<>();
            data.put("id", String.valueOf(rs.getInt("id")));
            data.put("nama", rs.getString("nama"));
            data.put("no_telepon", rs.getString("no_telepon"));
            data.put("kategori", rs.getString("kategori"));
            kontak.add(data);
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return kontak;
}
```

---

#### **c. Mengedit Kontak**
Pengguna dapat mengedit data kontak tertentu.

**Kode terkait (GUI):**
```java
private void TombolEditKontakActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = TabelDaftarKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diedit", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int id = Integer.parseInt(TabelDaftarKontak.getValueAt(selectedRow, 0).toString());
    String nama = FieldMasukkanNama.getText();
    String noTelepon = FieldMasukkanNomor.getText();
    String kategori = ComboBoxKategori.getSelectedItem().toString();

    DatabaseKontak.MemperbaruiKontak(id, nama, noTelepon, kategori);
    MenampilkanKontak();
}
```

**Kode untuk memperbarui data di database:**
```java
public static void MemperbaruiKontak(int id, String nama, String noTelepon, String kategori) {
    String sql = "UPDATE kontak SET nama = ?, no_telepon = ?, kategori = ? WHERE id = ?";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nama);
        pstmt.setString(2, noTelepon);
        pstmt.setString(3, kategori);
        pstmt.setInt(4, id);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

---

#### **d. Menghapus Kontak**
Pengguna dapat menghapus data kontak dari database.

**Kode terkait (GUI):**
```java
private void TombolHapusKontakActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = TabelDaftarKontak.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int id = Integer.parseInt(TabelDaftarKontak.getValueAt(selectedRow, 0).toString());
    DatabaseKontak.MenghapusKontak(id);
    MenampilkanKontak();
}
```

**Kode untuk menghapus data dari database:**
```java
public static void MenghapusKontak(int id) {
    String sql = "DELETE FROM kontak WHERE id = ?";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

---

### **2. Ekspor dan Impor CSV**

#### **a. Ekspor Kontak ke CSV**
**Kode terkait:**
```java
private void TombolEksporKontakActionPerformed(java.awt.event.ActionEvent evt) {
    try (FileWriter csvWriter = new FileWriter("DaftarKontak.csv")) {
        csvWriter.append("ID,Nama,Nomor Telepon,Kategori\n");
        DefaultTableModel model = (DefaultTableModel) TabelDaftarKontak.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            csvWriter.append(model.getValueAt(i, 0) + ",");
            csvWriter.append(model.getValueAt(i, 1) + ",");
            csvWriter.append(model.getValueAt(i, 2) + ",");
            csvWriter.append(model.getValueAt(i, 3) + "\n");
        }
        JOptionPane.showMessageDialog(this,

 "Data berhasil diekspor ke file CSV!");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saat ekspor data: " + e.getMessage());
    }
}
```

#### **b. Impor Kontak dari CSV**
**Kode terkait:**
```java
private void TombolImporKontakActionPerformed(java.awt.event.ActionEvent evt) {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                DatabaseKontak.MenambahKontak(data[1], data[2], data[3]);
            }
            MenampilkanKontak();
            JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari file CSV!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saat impor data: " + e.getMessage());
        }
    }
}
```


---
## Tampilan Pada Saat Aplikasi Di Jalankan

![Latihan3](https://github.com/user-attachments/assets/29c85996-d97e-45b8-b8ce-4f07522bbad6)


--- 

### Pembuat
- **Nama:** Muhammad Ihsan  
- **NPM:** 2210010286  
- **Kelas:** 5A Ti Reg Pagi BJM  


