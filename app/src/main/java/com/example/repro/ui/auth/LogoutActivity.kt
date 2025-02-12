package com.example.repro.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.repro.R

class LogoutActivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflasi layout kosong untuk fragment ini (meskipun proses logout tidak membutuhkan UI)
        val view = inflater.inflate(R.layout.activity_logout, container, false)

        // Proses logout
        logoutUser()

        return view
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
