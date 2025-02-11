package com.example.iconnet.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.repro.ui.auth.LoginActivity

class LogoutActivity : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tampilkan konfirmasi logout
        showLogoutConfirmationDialog()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Konfirmasi Logout")
            setMessage("Apakah Anda yakin ingin logout?")
            setPositiveButton("Ya") { _, _ ->
                logoutUser()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
                requireActivity().onBackPressedDispatcher.onBackPressed() // Kembali ke sebelumnya
            }
            setCancelable(false)
        }.show()
    }

    private fun logoutUser() {
        // Hapus sesi dari SharedPreferences
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            "UserSession",
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().clear().apply()

        // Arahkan ke LoginActivity
        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Membersihkan tumpukan aktivitas
        }
        startActivity(intent)

        // Tutup aktivitas saat ini
        requireActivity().finish()
    }
}
