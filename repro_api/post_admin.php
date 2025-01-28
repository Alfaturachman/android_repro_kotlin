<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Ambil raw data JSON dari body request
$data = json_decode(file_get_contents("php://input"), true);

// Validasi input
if (
    !isset($data['email']) || !isset($data['password']) || !isset($data['level']) ||
    !isset($data['nama']) || !isset($data['no_hp']) || !isset($data['alamat']) || !isset($data['lokasi'])
) {
    echo json_encode(["status" => false, "message" => "All fields are required"]);
    $conn->close();
    exit();
}

// Ambil data dari JSON
$email = $conn->real_escape_string($data['email']);
$password = $conn->real_escape_string($data['password']);
$level = $conn->real_escape_string($data['level']);
$nama = $conn->real_escape_string($data['nama']);
$no_hp = $conn->real_escape_string($data['no_hp']);
$alamat = $conn->real_escape_string($data['alamat']);
$lokasi = $conn->real_escape_string($data['lokasi']);

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

    // Query untuk insert ke tabel `admin`
    $sql_admin = "INSERT INTO `admin` (`id_user`, `nama`, `no_hp`, `alamat`, `lokasi`) 
                  VALUES ('$user_id', '$nama', '$no_hp', '$alamat', '$lokasi')";

    if ($conn->query($sql_admin) === true) {
        echo json_encode([
            "status" => true,
            "message" => "Data admin berhasil ditambahkan"
        ]);
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Error inserting data into 'admin' table",
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
