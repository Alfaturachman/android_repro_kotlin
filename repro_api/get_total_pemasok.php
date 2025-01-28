<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Ambil raw data JSON dari body request
$data = json_decode(file_get_contents("php://input"), true);

// Ambil id_pemasok dari data JSON
$user_id = isset($data['user_id']) ? $conn->real_escape_string($data['user_id']) : '';

// Periksa apakah id_pemasok ada
if (empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "Parameter 'user_id' is required."
    ]);
    $conn->close();
    exit();
}

// Query SQL untuk menghitung total stok 'Belum diambil'
$sql_belum_diambil = "SELECT COALESCE(SUM(jumlah_stok), 0) AS jumlah_stok 
                      FROM status_stok 
                      WHERE status = 'Belum diambil' 
                      AND id_pemasok = '$user_id'";
$result_belum_diambil = $conn->query($sql_belum_diambil);

// Query SQL untuk menghitung total stok 'Sudah diambil'
$sql_sudah_diambil = "SELECT COALESCE(SUM(jumlah_stok), 0) AS jumlah_stok 
                      FROM status_stok 
                      WHERE status = 'Sudah diambil' 
                      AND id_pemasok = '$user_id'";
$result_sudah_diambil = $conn->query($sql_sudah_diambil);

// Periksa hasil query
$total_belum_diambil = ($result_belum_diambil && $result_belum_diambil->num_rows > 0)
    ? $result_belum_diambil->fetch_assoc()['jumlah_stok']
    : 0;

$total_sudah_diambil = ($result_sudah_diambil && $result_sudah_diambil->num_rows > 0)
    ? $result_sudah_diambil->fetch_assoc()['jumlah_stok']
    : 0;

// Gabungkan hasil dalam satu array respons
$response = [
    "status" => true,
    "message" => "Data berhasil diambil.",
    "data" => [
        "pemasok_id" => $user_id,
        "stok" => [
            "belum_diambil" => (float)$total_belum_diambil,
            "sudah_diambil" => (float)$total_sudah_diambil
        ]
    ]
];

// Kembalikan hasil dalam format JSON
echo json_encode($response, JSON_PRETTY_PRINT);

// Tutup koneksi
$conn->close();
