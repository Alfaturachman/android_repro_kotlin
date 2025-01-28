<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Periksa apakah request menggunakan metode DELETE
if ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
    // Ambil data dari body request
    $input = json_decode(file_get_contents("php://input"), true);

    // Validasi input
    if (!isset($input['id'])) {
        echo json_encode([
            "status" => false,
            "message" => "ID harus diisi."
        ]);
        exit();
    }

    $id = $input['id'];

    // Query untuk menghapus data dari tabel
    $sql = "DELETE FROM `harga_ban_bekas` WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $id);

    if ($stmt->execute()) {
        // Periksa apakah ada baris yang terhapus
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                "status" => true,
                "message" => "Data harga ban berhasil dihapus.",
                "data" => [
                    "id" => $id
                ]
            ]);
        } else {
            echo json_encode([
                "status" => false,
                "message" => "Data dengan ID tersebut tidak ditemukan."
            ]);
        }
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Gagal menghapus data harga ban."
        ]);
    }

    $stmt->close();
} else {
    // Jika metode bukan DELETE
    echo json_encode([
        "status" => false,
        "message" => "Hanya mendukung metode DELETE."
    ]);
}

// Tutup koneksi
$conn->close();
