<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Periksa apakah request menggunakan metode POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Ambil data dari body request
    $input = json_decode(file_get_contents("php://input"), true);

    // Validasi input
    if (!isset($input['id_ambil']) || !isset($input['jumlah_stok']) || !isset($input['jumlah_mentah'])) {
        echo json_encode([
            "status" => false,
            "message" => "id_ambil, jumlah_stok, dan jumlah_mentah harus diisi."
        ]);
        exit();
    }

    $id_ambil = $input['id_ambil'];
    $jumlah_stok = $input['jumlah_stok'];
    $jumlah_mentah = $input['jumlah_mentah'];
    $tanggal = date("Y-m-d H:i:s");

    // Validasi apakah `id_ambil` ada di tabel `ambil`
    $check_sql = "SELECT id FROM ambil WHERE id = ?";
    $stmt_check = $conn->prepare($check_sql);
    $stmt_check->bind_param("i", $id_ambil);
    $stmt_check->execute();
    $result_check = $stmt_check->get_result();

    if ($result_check->num_rows === 0) {
        echo json_encode([
            "status" => false,
            "message" => "id_ambil tidak ditemukan di tabel ambil."
        ]);
        exit();
    }
    $stmt_check->close();

    // Query untuk memasukkan data ke tabel `olah`
    $sql = "INSERT INTO `olah` (id_ambil, tanggal, jumlah_stok, jumlah_mentah) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("isdd", $id_ambil, $tanggal, $jumlah_stok, $jumlah_mentah);

    if ($stmt->execute()) {
        echo json_encode([
            "status" => true,
            "message" => "Data olah berhasil ditambahkan.",
            "data" => [
                "id" => $stmt->insert_id,
                "id_ambil" => $id_ambil,
                "tanggal" => $tanggal,
                "jumlah_stok" => $jumlah_stok,
                "jumlah_mentah" => $jumlah_mentah
            ]
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Gagal menambahkan data olah."
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
