<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Periksa apakah request menggunakan metode POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ambil data dari body request
    $input = json_decode(file_get_contents("php://input"), true);

    // Validasi input
    if (!isset($input['jenis']) || !isset($input['harga'])) {
        echo json_encode([
            "status" => false,
            "message" => "Jenis dan harga harus diisi."
        ]);
        exit();
    }

    $jenis = $input['jenis'];
    $harga = $input['harga'];
    $ins_time = date("Y-m-d H:i:s");

    // Validasi nilai `jenis`
    if ($jenis !== 'Mobil' && $jenis !== 'Motor') {
        echo json_encode([
            "status" => false,
            "message" => "Jenis harus 'Mobil' atau 'Motor'."
        ]);
        exit();
    }

    // Query untuk memasukkan data ke tabel
    $sql = "INSERT INTO `harga_ban_bekas` (jenis, harga, ins_time) VALUES (?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sds", $jenis, $harga, $ins_time);

    if ($stmt->execute()) {
        echo json_encode([
            "status" => true,
            "message" => "Data harga ban berhasil ditambahkan.",
            "data" => [
                "id" => $stmt->insert_id,
                "jenis" => $jenis,
                "harga" => $harga,
                "ins_time" => $ins_time
            ]
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Gagal menambahkan data harga ban."
        ]);
    }

    $stmt->close();
} else {
    // Jika metode bukan POST
    echo json_encode([
        "status" => false,
        "message" => "Hanya mendukung metode POST."
    ]);
}

// Tutup koneksi
$conn->close();
