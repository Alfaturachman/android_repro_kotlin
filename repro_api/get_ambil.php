<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Query untuk mengambil data gabungan dari tabel
$sql = "
    SELECT 
        ambil.id AS id_ambil,
        ambil.tanggal AS tanggal_ambil,
        ambil.jumlah_stok AS jumlah_stok_ambil,
        ambil.keterangan AS keterangan_ambil,
        pemasok.id AS id_pemasok,
        pemasok.nama AS nama_pemasok,
        pemasok.nama_usaha AS nama_usaha_pemasok,
        pemasok.alamat AS alamat_pemasok,
        pemasok.no_hp AS no_hp_pemasok,
        mitra_pengelola.id AS id_pengelola,
        mitra_pengelola.nama AS nama_pengelola,
        mitra_pengelola.nama_usaha AS nama_usaha_pengelola,
        mitra_pengelola.alamat AS alamat_pengelola,
        status_stok.id AS id_stok,
        status_stok.tanggal AS tanggal_stok,
        status_stok.jenis AS jenis_stok,
        status_stok.jumlah_stok AS jumlah_stok_status,
        status_stok.harga AS harga_stok,
        status_stok.total_harga AS total_harga_stok,
        status_stok.status AS status_stok
    FROM 
        ambil
    LEFT JOIN pemasok ON ambil.id_pemasok = pemasok.id
    LEFT JOIN mitra_pengelola ON ambil.id_pengelola = mitra_pengelola.id
    LEFT JOIN status_stok ON ambil.id_stok = status_stok.id;
";

// Jalankan query
$result = $conn->query($sql);

if (!$result) {
    // Jika terjadi kesalahan saat menjalankan query
    echo json_encode([
        "status" => false,
        "message" => "Terjadi kesalahan saat mengambil data: " . $conn->error
    ]);
    $conn->close();
    exit();
}

// Siapkan data
$data = [];
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }

    // Jika data ditemukan, kirimkan dalam format JSON
    echo json_encode([
        "status" => true,
        "message" => "Data berhasil diambil.",
        "data" => $data
    ]);
} else {
    // Jika tidak ada data ditemukan
    echo json_encode([
        "status" => false,
        "message" => "Tidak ada data ditemukan.",
        "data" => []
    ]);
}

// Tutup koneksi database
$conn->close();
