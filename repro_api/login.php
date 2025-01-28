<?php
require_once 'connection.php';
header("Content-Type: application/json");

// Ambil raw data JSON dari body request
$data = json_decode(file_get_contents("php://input"), true);

// Validasi input
if (!isset($data['email']) || !isset($data['password'])) {
    echo json_encode(["error" => "Email and password are required"]);
    $conn->close();
    exit();
}

// Ambil data dari JSON
$email = $conn->real_escape_string($data['email']);
$password = $conn->real_escape_string($data['password']);

// Query untuk mencari user berdasarkan email
$sql_user = "SELECT * FROM `user` WHERE `email` = '$email' LIMIT 1";
$result = $conn->query($sql_user);

if ($result->num_rows > 0) {
    // Ambil data user
    $user = $result->fetch_assoc();

    // Verifikasi password
    if (password_verify($password, $user['password'])) {
        // Login berhasil
        // Buat session atau token untuk status login
        session_start();
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['email'] = $user['email'];
        $_SESSION['level'] = $user['level'];

        // Ambil detail dari tabel sesuai dengan level pengguna
        $user_details = [];
        if ($user['level'] == 'admin') {
            // Ambil data dari tabel admin
            $sql_admin = "SELECT * FROM `admin` WHERE `id_user` = " . $user['id'] . " LIMIT 1";
            $admin_result = $conn->query($sql_admin);
            if ($admin_result->num_rows > 0) {
                $user_details = $admin_result->fetch_assoc();
            }
        } elseif ($user['level'] == 'pengelola') {
            // Ambil data dari tabel mitra_pengelola
            $sql_pengelola = "SELECT * FROM `mitra_pengelola` WHERE `id_user` = " . $user['id'] . " LIMIT 1";
            $pengelola_result = $conn->query($sql_pengelola);

            // Debugging: Cek jika query berhasil
            if ($pengelola_result === false) {
                echo json_encode(["error" => "Error executing query: " . $conn->error]);
                exit();
            }

            if ($pengelola_result->num_rows > 0) {
                $user_details = $pengelola_result->fetch_assoc();
            }
        } elseif ($user['level'] == 'pemasok') {
            // Ambil data dari tabel pemasok
            $sql_pemasok = "SELECT * FROM `pemasok` WHERE `id_user` = " . $user['id'] . " LIMIT 1";
            $pemasok_result = $conn->query($sql_pemasok);
            if ($pemasok_result->num_rows > 0) {
                $user_details = $pemasok_result->fetch_assoc();
            }
        }

        // Kirimkan respons sukses dengan detail pengguna
        echo json_encode([
            "status" => true,
            "message" => "Login successful",
            "id_user" => $user['id'],
            "email" => $user['email'],
            "level" => $user['level'],
            "user_details" => $user_details
        ]);
    } else {
        // Password salah
        echo json_encode(["error" => "Invalid email or password"]);
    }
} else {
    // Email tidak ditemukan
    echo json_encode(["error" => "User not found"]);
}

// Tutup koneksi
$conn->close();
