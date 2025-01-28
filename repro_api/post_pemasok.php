<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Ambil raw data JSON dari body request
$data = json_decode(file_get_contents("php://input"), true);

// Validasi input
if (
    !isset($data['email']) || !isset($data['password']) || !isset($data['nama']) ||
    !isset($data['nama_usaha']) || !isset($data['no_hp']) || !isset($data['alamat'])
) {
    echo json_encode([
        "status" => false,
        "message" => "All fields are required"
    ]);
    $conn->close();
    exit();
}

// Ambil data dari JSON
$email = $conn->real_escape_string($data['email']);
$password = $conn->real_escape_string($data['password']);
$nama = $conn->real_escape_string($data['nama']);
$nama_usaha = $conn->real_escape_string($data['nama_usaha']);
$no_hp = $conn->real_escape_string($data['no_hp']);
$alamat = $conn->real_escape_string($data['alamat']);
$lokasi = isset($data['lokasi']) ? $conn->real_escape_string($data['lokasi']) : ''; // Lokasi opsional

// Set level menjadi 'pemasok'
$level = 'pemasok';

// Cek apakah email sudah terdaftar
$sql_check_email = "SELECT * FROM `user` WHERE `email` = '$email'";
$result_check = $conn->query($sql_check_email);

if ($result_check->num_rows > 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email already exists"
    ]);
    $conn->close();
    exit();
}

// Enkripsi password
$password_hashed = password_hash($password, PASSWORD_DEFAULT);

// Query untuk insert ke tabel `user`
$sql_user = "INSERT INTO `user` (`email`, `password`, `level`) 
             VALUES ('$email', '$password_hashed', '$level')";

if ($conn->query($sql_user) === true) {
    // Ambil id_user yang baru dimasukkan
    $user_id = $conn->insert_id;

    // Query untuk insert ke tabel `pemasok`
    $sql_pemasok = "INSERT INTO `pemasok` (`id_user`, `nama`, `nama_usaha`, `no_hp`, `alamat`, `lokasi`) 
                    VALUES ('$user_id', '$nama', '$nama_usaha', '$no_hp', '$alamat', '$lokasi')";

    if ($conn->query($sql_pemasok) === true) {
        echo json_encode([
            "status" => true,
            "message" => "Data pemasok berhasil ditambahkan!",
            "details" => [
                "email" => $email,
                "nama" => $nama,
                "nama_usaha" => $nama_usaha,
                "no_hp" => $no_hp,
                "alamat" => $alamat,
                "lokasi" => $lokasi
            ]
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Error inserting data into 'pemasok' table",
            "details" => $conn->error
        ]);
    }
} else {
    echo json_encode([
        "status" => false,
        "message" => "Error inserting data into 'user' table",
        "details" => $conn->error
    ]);
}

// Tutup koneksi
$conn->close();