<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Ambil raw data JSON dari body request
$data = json_decode(file_get_contents("php://input"), true);

// Validasi input
if (!isset($data['id_pemasok'])) {
    echo json_encode(["error" => "id_pemasok are required"]);
    $conn->close();
    exit();
}

// Ambil data dari JSON
$id_pemasok = $conn->real_escape_string($data['id_pemasok']);

// Query untuk mengambil data pemasok
$sql_pemasok = "SELECT DISTINCT 
    s.id AS id_status_stok, 
    s.id_pemasok, 
    s.tanggal AS tanggal_status_stok, 
    s.jenis, 
    s.jumlah_stok, 
    s.harga, 
    s.total_harga, 
    s.lokasi, 
    s.status, 
    a.id AS id_ambil, 
    a.id_pengelola, 
    a.tanggal AS tanggal_ambil, 
    a.jumlah_stok AS jumlah_ambil, 
    a.keterangan
FROM `status_stok` s
LEFT JOIN `ambil` a ON s.id_pemasok = a.id_pemasok
WHERE s.id_pemasok = $id_pemasok";


// Eksekusi query untuk pemasok
$result_pemasok = $conn->query($sql_pemasok);

// Periksa hasil query pemasok
if ($result_pemasok->num_rows > 0) {
    $pemasok_data = $result_pemasok->fetch_assoc();

    // Query untuk mengambil data status stok dan ambil
    $sql_status_stok = "SELECT 
                            s.id AS id_status_stok, s.id_pemasok, s.tanggal AS tanggal_status_stok, 
                            s.jenis, s.jumlah_stok, s.harga, s.total_harga, s.lokasi, s.status, 
                            a.id AS id_ambil, a.id_pengelola, a.tanggal AS tanggal_ambil, a.jumlah_stok AS jumlah_ambil, a.keterangan
                        FROM `status_stok` s
                        LEFT JOIN `ambil` a ON s.id_pemasok = a.id_pemasok
                        WHERE s.id_pemasok = $id_pemasok";

    // Eksekusi query untuk status stok dan ambil
    $result_status_stok = $conn->query($sql_status_stok);

    $status_stok_detail = [];
    if ($result_status_stok->num_rows > 0) {
        // Iterasi hasil query status stok
        while ($row = $result_status_stok->fetch_assoc()) {
            $status_stok_detail[] = $row;
        }
    }

    // Gabungkan data pemasok dengan data status stok dan ambil
    $response = [
        "status" => true,
        "message" => "success",
        "data" => [
            "pemasok" => $pemasok_data,
            "status_stok" => $status_stok_detail
        ]
    ];

    // Kirimkan respons dalam format JSON
    echo json_encode($response);
} else {
    // Jika tidak ada data pemasok
    echo json_encode([
        "status" => false,
        "message" => "No supplier found"
    ]);
}

// Tutup koneksi
$conn->close();
