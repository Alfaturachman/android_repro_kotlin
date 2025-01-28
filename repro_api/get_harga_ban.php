<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Query untuk mengambil data harga ban bekas
$sql = "SELECT 
            id, 
            jenis, 
            harga, 
            ins_time 
        FROM `harga_ban_bekas`";

$result = $conn->query($sql);

// Periksa hasil query
if ($result->num_rows > 0) {
    $harga_ban_list = [];

    // Iterasi hasil query
    while ($row = $result->fetch_assoc()) {
        $harga_ban_list[] = $row;
    }

    // Kirimkan respons dengan data harga ban
    echo json_encode([
        "status" => true,
        "message" => "success",
        "data" => $harga_ban_list
    ]);
} else {
    // Jika tidak ada data
    echo json_encode([
        "status" => false,
        "message" => "No tire prices found"
    ]);
}

// Tutup koneksi
$conn->close();
