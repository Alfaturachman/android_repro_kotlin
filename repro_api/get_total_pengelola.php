<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Ambil raw data JSON dari body request
$data = json_decode(file_get_contents("php://input"), true);

// Ambil id_pengelola dari data JSON
$user_id = isset($data['user_id']) ? $conn->real_escape_string($data['user_id']) : '';

if (empty($user_id)) {
    echo json_encode([
        "status" => false,
        "message" => "Parameter 'user_id' is required."
    ]);
    $conn->close();
    exit();
}

// Query SQL untuk get_total_ambil
$sql_ambil = "SELECT SUM(a.jumlah_stok) AS jumlah_stok
              FROM ambil a
              LEFT JOIN olah o ON a.id = o.id_ambil
              WHERE a.id_pengelola = '$user_id'
                AND o.id_ambil IS NULL";
$result_ambil = $conn->query($sql_ambil);

// Query SQL untuk get_total_diolah
$sql_diolah = "SELECT SUM(olah.jumlah_mentah) AS jumlah_mentah
               FROM olah
               JOIN ambil ON ambil.id = olah.id_ambil
               WHERE ambil.id_pengelola = '$user_id'";
$result_diolah = $conn->query($sql_diolah);

// Menyusun hasil untuk get_total_ambil
$total_ambil = ($result_ambil && $result_ambil->num_rows > 0)
    ? (float)$result_ambil->fetch_assoc()['jumlah_stok']
    : 0.0;

// Menyusun hasil untuk get_total_diolah
$total_diolah = ($result_diolah && $result_diolah->num_rows > 0)
    ? (float)$result_diolah->fetch_assoc()['jumlah_mentah']
    : 0.0;

// Gabungkan hasil dalam satu array
$response = [
    "status" => true,
    "message" => "Data retrieved successfully.",
    "data" => [
        "total_ambil" => $total_ambil,
        "total_diolah" => $total_diolah
    ]
];

// Kembalikan hasil dalam format JSON
echo json_encode($response, JSON_PRETTY_PRINT);

// Tutup koneksi
$conn->close();
