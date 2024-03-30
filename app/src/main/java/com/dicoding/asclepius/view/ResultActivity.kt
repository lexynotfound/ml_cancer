package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
            classifyImage(it)
        } ?: run {
            showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun classifyImage(imageUri: Uri) {
        val classifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    if (results.isNullOrEmpty()) {
                        showToast(getString(R.string.prediction_failed))
                    } else {
                        val classifications = results[0].categories
                        if (classifications.isNullOrEmpty()) {
                            showToast(getString(R.string.prediction_failed))
                        } else {
                            val topClassification = classifications[0]
                            val confidence = (topClassification.score * 100).toInt()
                            val label = topClassification.label
                            binding.resultText.text = getString(R.string.prediction_text, label, confidence)
                        }
                    }
                }
            }
        )
        classifierHelper.classifyStaticImage(imageUri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}
