package com.example.repro.ui.pengelola;
import com.google.gson.annotations.SerializedName;
public class Ambil {

    @SerializedName("id_ambil")
    private int idAmbil;

    @SerializedName("tanggal_ambil")
    private String tanggalAmbil;

    @SerializedName("jumlah_stok_ambil")
    private int jumlahStokAmbil;

    @SerializedName("keterangan_ambil")
    private String keteranganAmbil;

    @SerializedName("id_pemasok")
    private int idPemasok;

    @SerializedName("nama_pemasok")
    private String namaPemasok;

    @SerializedName("nama_usaha_pemasok")
    private String namaUsahaPemasok;

    @SerializedName("alamat_pemasok")
    private String alamatPemasok;

    @SerializedName("no_hp_pemasok")
    private String noHpPemasok;

    @SerializedName("id_pengelola")
    private int idPengelola;

    @SerializedName("nama_pengelola")
    private String namaPengelola;

    @SerializedName("nama_usaha_pengelola")
    private String namaUsahaPengelola;

    @SerializedName("alamat_pengelola")
    private String alamatPengelola;

    @SerializedName("id_stok")
    private int idStok;

    @SerializedName("tanggal_stok")
    private String tanggalStok;

    @SerializedName("jenis_stok")
    private String jenisStok;

    @SerializedName("jumlah_stok_status")
    private int jumlahStokStatus;

    @SerializedName("harga_stok")
    private double hargaStok;

    @SerializedName("total_harga_stok")
    private double totalHargaStok;

    @SerializedName("status_stok")
    private String statusStok;

    // Getter and Setter methods for each field

    public int getIdAmbil() {
        return idAmbil;
    }

    public void setIdAmbil(int idAmbil) {
        this.idAmbil = idAmbil;
    }

    public String getTanggalAmbil() {
        return tanggalAmbil;
    }

    public void setTanggalAmbil(String tanggalAmbil) {
        this.tanggalAmbil = tanggalAmbil;
    }

    public int getJumlahStokAmbil() {
        return jumlahStokAmbil;
    }

    public void setJumlahStokAmbil(int jumlahStokAmbil) {
        this.jumlahStokAmbil = jumlahStokAmbil;
    }

    public String getKeteranganAmbil() {
        return keteranganAmbil;
    }

    public void setKeteranganAmbil(String keteranganAmbil) {
        this.keteranganAmbil = keteranganAmbil;
    }

    public int getIdPemasok() {
        return idPemasok;
    }

    public void setIdPemasok(int idPemasok) {
        this.idPemasok = idPemasok;
    }

    public String getNamaPemasok() {
        return namaPemasok;
    }

    public void setNamaPemasok(String namaPemasok) {
        this.namaPemasok = namaPemasok;
    }

    public String getNamaUsahaPemasok() {
        return namaUsahaPemasok;
    }

    public void setNamaUsahaPemasok(String namaUsahaPemasok) {
        this.namaUsahaPemasok = namaUsahaPemasok;
    }

    public String getAlamatPemasok() {
        return alamatPemasok;
    }

    public void setAlamatPemasok(String alamatPemasok) {
        this.alamatPemasok = alamatPemasok;
    }

    public String getNoHpPemasok() {
        return noHpPemasok;
    }

    public void setNoHpPemasok(String noHpPemasok) {
        this.noHpPemasok = noHpPemasok;
    }

    public int getIdPengelola() {
        return idPengelola;
    }

    public void setIdPengelola(int idPengelola) {
        this.idPengelola = idPengelola;
    }

    public String getNamaPengelola() {
        return namaPengelola;
    }

    public void setNamaPengelola(String namaPengelola) {
        this.namaPengelola = namaPengelola;
    }

    public String getNamaUsahaPengelola() {
        return namaUsahaPengelola;
    }

    public void setNamaUsahaPengelola(String namaUsahaPengelola) {
        this.namaUsahaPengelola = namaUsahaPengelola;
    }

    public String getAlamatPengelola() {
        return alamatPengelola;
    }

    public void setAlamatPengelola(String alamatPengelola) {
        this.alamatPengelola = alamatPengelola;
    }

    public int getIdStok() {
        return idStok;
    }

    public void setIdStok(int idStok) {
        this.idStok = idStok;
    }

    public String getTanggalStok() {
        return tanggalStok;
    }

    public void setTanggalStok(String tanggalStok) {
        this.tanggalStok = tanggalStok;
    }

    public String getJenisStok() {
        return jenisStok;
    }

    public void setJenisStok(String jenisStok) {
        this.jenisStok = jenisStok;
    }

    public int getJumlahStokStatus() {
        return jumlahStokStatus;
    }

    public void setJumlahStokStatus(int jumlahStokStatus) {
        this.jumlahStokStatus = jumlahStokStatus;
    }

    public double getHargaStok() {
        return hargaStok;
    }

    public void setHargaStok(double hargaStok) {
        this.hargaStok = hargaStok;
    }

    public double getTotalHargaStok() {
        return totalHargaStok;
    }

    public void setTotalHargaStok(double totalHargaStok) {
        this.totalHargaStok = totalHargaStok;
    }

    public String getStatusStok() {
        return statusStok;
    }

    public void setStatusStok(String statusStok) {
        this.statusStok = statusStok;
    }
}