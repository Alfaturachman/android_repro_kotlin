<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Query untuk mengambil data pemasok
$sql = "SELECT 
            p.id, 
            p.id_user As idUser, 
            p.nama,
            p.nama_usaha AS namaUsaha, 
            p.no_hp as noHp, 
            p.alamat, 
            p.lokasi, 
            p.ins_time, 
            u.email AS user_email 
        FROM `pemasok` p
        LEFT JOIN `user` u ON p.id_user = u.id";

$result = $conn->query($sql);

// Periksa hasil query
if ($result->num_rows > 0) {
    $pemasok_list = [];

    // Iterasi hasil query
    while ($row = $result->fetch_assoc()) {
        $pemasok_list[] = $row;
    }

    // Kirimkan respons dengan data pemasok
    echo json_encode([
        "status" => true,
        "message" => "success",
        "data" => $pemasok_list
    ]);
} else {
    // Jika tidak ada data
    echo json_encode([
        "success" => false,
        "message" => "No suppliers found"
    ]);
}

// Tutup koneksi
$conn->close();
