package com.example.repro.utils

import kotlin.math.*
import com.example.repro.model.getAmbilStok

object TSP {

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius bumi dalam km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun calculateDistanceMatrix(locations: List<getAmbilStok>): Array<DoubleArray> {
        val n = locations.size
        val distanceMatrix = Array(n) { DoubleArray(n) }

        for (i in 0 until n) {
            for (j in 0 until n) {
                if (i != j) {
                    val loc1 = locations[i].lokasi.split(",")
                    val loc2 = locations[j].lokasi.split(",")
                    val lat1 = loc1[0].toDouble()
                    val lon1 = loc1[1].toDouble()
                    val lat2 = loc2[0].toDouble()
                    val lon2 = loc2[1].toDouble()
                    distanceMatrix[i][j] = haversineDistance(lat1, lon1, lat2, lon2)
                } else {
                    distanceMatrix[i][j] = 0.0
                }
            }
        }

        return distanceMatrix
    }

    fun nearestNeighborTSP(distanceMatrix: Array<DoubleArray>, startIndex: Int): Pair<List<Int>, Double> {
        val n = distanceMatrix.size
        val visited = BooleanArray(n)
        val tour = mutableListOf(startIndex) // Mulai dari titik pengelola
        visited[startIndex] = true
        var totalDistance = 0.0

        for (step in 1 until n) {
            val current = tour.last()
            var nearestNeighbor = -1
            var nearestDistance = Double.MAX_VALUE

            for (i in 0 until n) {
                if (!visited[i] && distanceMatrix[current][i] < nearestDistance) {
                    nearestDistance = distanceMatrix[current][i]
                    nearestNeighbor = i
                }
            }

            if (nearestNeighbor != -1) {
                tour.add(nearestNeighbor)
                visited[nearestNeighbor] = true
                totalDistance += nearestDistance
            }
        }

        return Pair(tour, totalDistance)
    }
}