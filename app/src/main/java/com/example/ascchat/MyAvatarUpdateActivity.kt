package com.example.ascchat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.core.file.AmityUploadResult
import com.amity.socialcloud.sdk.core.user.AmityUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyAvatarUpdateActivity : AppCompatActivity() {

    private var currentPhotoPath: String? = null
    private var currentPhotoUri: Uri? = null
    private lateinit var sendButton: Button
    private lateinit var textview: TextView
    private lateinit var imageview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Send image"
        setContentView(R.layout.activity_image_message_sender)
        sendButton = findViewById(R.id.send_button)
        textview = findViewById(R.id.textview)
        imageview = findViewById(R.id.imageview)

        sendButton.isEnabled = false
        sendButton.setOnClickListener {
            sendButton.isEnabled = false
            updateUserAvatar()

        }

        findViewById<View>(R.id.camera_button).setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), IntentRequestCode.REQUEST_TAKE_PHOTO);
        }

        findViewById<View>(R.id.gallery_button).setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentRequestCode.REQUEST_SELECT_PHOTO)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == IntentRequestCode.REQUEST_TAKE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == IntentRequestCode.REQUEST_SELECT_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchSearchFileIntent()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IntentRequestCode.REQUEST_TAKE_PHOTO) {
                setImage()
            }
            if (requestCode == IntentRequestCode.REQUEST_SELECT_PHOTO) {
                data?.data?.also { uri ->
                    currentPhotoPath = RealPathUtil.getRealPath(this, uri)
                    setImage()
                }
            }
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.e("TAG", "Error creating image file")
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        packageName,
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, IntentRequestCode.REQUEST_TAKE_PHOTO)
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        currentPhotoPath = image.absolutePath
        return image

    }

    private fun dispatchSearchFileIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, IntentRequestCode.REQUEST_SELECT_PHOTO)

    }


    private fun setImage() {
        sendButton.isEnabled = false
        textview.visibility = View.VISIBLE
        sendButton.isEnabled = true
        textview.visibility = View.GONE
        /*Glide.with(this).clear(imageview)

        BitmapFactory.decodeFile(currentPhotoPath)?.also { bitmap ->

            Glide.with(this)
                    .load(bitmap)
                    .into(imageview)

            val f = File(currentPhotoPath)
            val contentUri = Uri.fromFile(f)
            currentPhotoUri = contentUri


        }*/
    }

    private fun updateUserAvatar() {
        createRequest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .doOnError {
                    it.printStackTrace()
                }
                .subscribe()
    }

    private fun createRequest(): io.reactivex.Single<AmityUser> {
        return AmityCoreClient.newFileRepository()
                .uploadImage(currentPhotoUri!!)
                .isFullImage(true)
                .build()
                .transfer()
                .filter {
                    it is AmityUploadResult.COMPLETE
                }
                .firstOrError()
                .flatMap {
                    AmityCoreClient
                            .updateUser()
                            .avatar((it as AmityUploadResult.COMPLETE).getFile())
                            .build()
                            .update()
                }
    }

}