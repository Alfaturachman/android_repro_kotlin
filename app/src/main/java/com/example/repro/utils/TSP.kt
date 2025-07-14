package com.example.repro.utils

import kotlin.math.*
import com.example.repro.model.getAmbilStok

object TSP {

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radius bumi dalam km (gunakan Double untuk konsistensi)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun calculateDistanceMatrix(pengelolaLat: Double, pengelolaLong: Double, locations: List<getAmbilStok>): Array<DoubleArray> {
        // Buat array lokasi dengan pengelola sebagai index 0
        val allLocations = mutableListOf<Pair<Double, Double>>()

        // Tambahkan pengelola sebagai titik pertama (index 0)
        allLocations.add(Pair(pengelolaLat, pengelolaLong))

        // Tambahkan semua pemasok dengan parsing yang lebih robust
        for (location in locations) {
            val koordinat = location.lokasi.trim().replace(" ", "").split(",")
            val lat = if (koordinat.size >= 1) koordinat[0].toDoubleOrNull() ?: 0.0 else 0.0
            val lon = if (koordinat.size >= 2) koordinat[1].toDoubleOrNull() ?: 0.0 else 0.0
            allLocations.add(Pair(lat, lon))
        }

        val n = allLocations.size
        val distanceMatrix = Array(n) { DoubleArray(n) }

        // Hitung jarak antara setiap pasang titik
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (i == j) {
                    distanceMatrix[i][j] = 0.0 // Jarak ke diri sendiri = 0
                } else {
                    val loc1 = allLocations[i]
                    val loc2 = allLocations[j]

                    distanceMatrix[i][j] = haversineDistance(
                        loc1.first, loc1.second,
                        loc2.first, loc2.second
                    )
                }
            }
        }

        return distanceMatrix
    }

    fun nearestNeighborTSP(distanceMatrix: Array<DoubleArray>): Pair<List<Int>, Double> {
        val numLocations = distanceMatrix.size
        val visited = BooleanArray(numLocations)
        val tour = mutableListOf<Int>()
        var totalDistance = 0.0

        // Mulai dari pengelola (titik 0)
        var currentLocation = 0
        visited[0] = true
        tour.add(currentLocation)

        // Kunjungi semua titik lainnya
        for (step in 1 until numLocations) {
            var nearestDistance = Double.MAX_VALUE
            var nearestNeighbor = -1

            // Cari titik terdekat yang belum dikunjungi
            for (i in 0 until numLocations) {
                if (!visited[i] && distanceMatrix[currentLocation][i] < nearestDistance) {
                    nearestDistance = distanceMatrix[currentLocation][i]
                    nearestNeighbor = i
                }
            }

            // Pergi ke titik terdekat
            if (nearestNeighbor != -1) {
                tour.add(nearestNeighbor)
                totalDistance += nearestDistance
                visited[nearestNeighbor] = true
                currentLocation = nearestNeighbor
            }
        }

        // Kembali ke titik awal (pengelola)
        if (tour.size > 1) { // Pastikan ada titik untuk dikunjungi
            val backToStart = distanceMatrix[currentLocation][0]
            totalDistance += backToStart
            tour.add(0) // Tambahkan pengelola sebagai titik akhir
        }

        return Pair(tour, totalDistance)
    }

    // Fungsi helper untuk mendapatkan urutan pemasok berdasarkan hasil TSP
    fun getOptimalRoute(pengelolaLat: Double, pengelolaLong: Double, pemasokList: List<getAmbilStok>): Pair<List<getAmbilStok>, Double> {
        if (pemasokList.isEmpty()) {
            return Pair(emptyList(), 0.0)
        }

        // Hitung matriks jarak
        val distanceMatrix = calculateDistanceMatrix(pengelolaLat, pengelolaLong, pemasokList)

        // Jalankan algoritma TSP
        val tspResult = nearestNeighborTSP(distanceMatrix)

        // Urutkan pemasok berdasarkan hasil TSP
        val sortedPemasok = mutableListOf<getAmbilStok>()
        for (index in tspResult.first) {
            if (index != 0) { // Skip index 0 (pengelola)
                val pemasokIndex = index - 1 // -1 karena index 0 untuk pengelola
                if (pemasokIndex >= 0 && pemasokIndex < pemasokList.size) {
                    sortedPemasok.add(pemasokList[pemasokIndex])
                }
            }
        }

        return Pair(sortedPemasok, tspResult.second)
    }

    // Fungsi untuk mendapatkan jarak individual dari pengelola ke setiap pemasok
    // (untuk keperluan display di adapter)
    fun getIndividualDistances(pengelolaLat: Double, pengelolaLong: Double, pemasokList: List<getAmbilStok>): List<Double> {
        return pemasokList.map { pemasok ->
            val koordinat = pemasok.lokasi.trim().replace(" ", "").split(",")
            if (koordinat.size == 2) {
                val lat = koordinat[0].toDoubleOrNull()
                val lon = koordinat[1].toDoubleOrNull()
                if (lat != null && lon != null) {
                    haversineDistance(pengelolaLat, pengelolaLong, lat, lon)
                } else {
                    0.0
                }
            } else {
                0.0
            }
        }
    }
}