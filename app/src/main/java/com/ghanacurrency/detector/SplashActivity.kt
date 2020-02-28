package com.ghanacurrency.detector

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel

class SplashActivity : AppCompatActivity() {

    private lateinit var remoteModel: FirebaseAutoMLRemoteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkModelDownloaded()
    }

    private fun checkModelDownloaded() {

        remoteModel = FirebaseAutoMLRemoteModel.Builder("Cedi_notes_2020").build()

        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener {
                if (it) {
                    onSuccess()
                }
            }

        val conditions = FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelManager.getInstance().download(remoteModel, conditions)
            .addOnCompleteListener {
                onSuccess()
            }
    }

    private fun onSuccess() {
        val i =  Intent(this, DetectActivity::class.java)
        startActivity(i)
        finish()
    }
}