<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Periksa apakah request menggunakan metode PUT
if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
    // Ambil data dari body request
    $input = json_decode(file_get_contents("php://input"), true);

    // Validasi input
    if (!isset($input['id']) || !isset($input['jenis']) || !isset($input['harga'])) {
        echo json_encode([
            "status" => false,
            "message" => "ID, jenis, dan harga harus diisi."
        ]);
        exit();
    }

    $id = $input['id'];
    $jenis = $input['jenis'];
    $harga = $input['harga'];

    // Validasi nilai `jenis`
    if ($jenis !== 'Mobil' && $jenis !== 'Motor') {
        echo json_encode([
            "status" => false,
            "message" => "Jenis harus 'Mobil' atau 'Motor'."
        ]);
        exit();
    }

    // Query untuk mengupdate data di tabel
    $sql = "UPDATE `harga_ban_bekas` SET jenis = ?, harga = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sdi", $jenis, $harga, $id);

    if ($stmt->execute()) {
        // Periksa apakah ada baris yang diubah
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                "status" => true,
                "message" => "Data harga ban berhasil diupdate.",
                "data" => [
                    "id" => $id,
                    "jenis" => $jenis,
                    "harga" => $harga
                ]
            ]);
        } else {
            echo json_encode([
                "status" => false,
                "message" => "Tidak ada data yang diubah atau ID tidak ditemukan."
            ]);
        }
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Gagal mengupdate data harga ban."
        ]);
    }

    $stmt->close();
} else {
    // Jika metode bukan PUT
    echo json_encode([
        "status" => false,
        "message" => "Hanya mendukung metode PUT."
    ]);
}

// Tutup koneksi
$conn->close();
