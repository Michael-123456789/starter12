package com.ghanacurrency.detector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.camerakit.CameraKitView
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import kotlinx.android.synthetic.main.activity_detect.*

class DetectActivity : AppCompatActivity() {

    private var cameraKitView: CameraKitView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)

        cameraKitView = cameraView


        detectBtn.setOnClickListener {
            cameraView.captureImage { cameraKitView, byteArray ->
                cameraView.onStop()
//                alertDialog.show()
                var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
                bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    cameraKitView?.width ?: 0,
                    cameraKitView?.height ?: 0,
                    false
                )
                runDetector(bitmap)
            }
//            graphic_overlay.clear()
        }
    }

    private fun runDetector(bitmap: Bitmap) {

        // Specify the name you assigned in the Firebase console.
        val remoteModel = FirebaseAutoMLRemoteModel.Builder("Cedi_notes_2020").build()

        val conditions = FirebaseModelDownloadConditions.Builder()
            .build()


//        FirebaseModelManager.getInstance()
//            .download(remoteModel, conditions)
//            .addOnCompleteListener {
//                doDetection(remoteModel, bitmap)
//            }

        doDetection(remoteModel, bitmap)

    }

    private fun doDetection(remoteModel: FirebaseAutoMLRemoteModel, bitmap: Bitmap) {


        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener { isDownloaded ->
                val optionsBuilder =
                    if (isDownloaded) {
                        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel)
                    } else {
                        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel)
                    }
                // Evaluate your model in the Firebase console to determine an appropriate threshold.
                val options = optionsBuilder.setConfidenceThreshold(0.0f).build()
                val labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)

                val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

                labeler.processImage(image)
                    .addOnSuccessListener { currencies ->
                        processLabelResults(currencies)
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            }
    }

    private fun processLabelResults(currencies: MutableList<FirebaseVisionImageLabel>) {
        for (currency in currencies) {
            Log.i("Data/Text", currency.text)
            Log.i("Data/Confidence", currency.confidence.toString())
        }
    }


    override fun onResume() {
        super.onResume()
        cameraKitView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraKitView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        cameraKitView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        cameraKitView?.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}