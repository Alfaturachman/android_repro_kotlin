<?php

$host = "localhost";
$username = "root";
$password = "";
$dbname = "crumb_rubber";

// Koneksi ke database
$conn = new mysqli($host, $username, $password, $dbname);

// Periksa koneksi
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
