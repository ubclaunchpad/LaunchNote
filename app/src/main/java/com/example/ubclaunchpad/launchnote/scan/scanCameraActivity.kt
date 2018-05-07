package com.example.ubclaunchpad.launchnote.scan

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.ubclaunchpad.launchnote.R

import android.view.TextureView
import android.util.Size
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraDevice
import android.view.Surface
import android.hardware.camera2.CameraCaptureSession
import android.os.HandlerThread
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.ubclaunchpad.launchnote.photoBrowser.PhotoBrowserActivity


class ScanCameraActivity : AppCompatActivity() {

    private lateinit var mTextureView: TextureView
    private lateinit var mPreviewSize: Size
    private var mCameraDevice: CameraDevice? = null
    private lateinit var mPreviewBuilder: CaptureRequest.Builder
    private lateinit var mPreviewSession: CameraCaptureSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_camera)
    }

    override fun onResume() {
        super.onResume()
        mTextureView = findViewById(R.id.previewTextureView)
        mTextureView.surfaceTextureListener = mSurfaceTextureListener
    }

    override fun onPause() {
        super.onPause()

        if (mCameraDevice != null) {
            mCameraDevice?.close()
            mCameraDevice = null
        }
    }

    // Listener to open camera when surface texture is available.
    private var mSurfaceTextureListener: TextureView.SurfaceTextureListener = object: TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            Log.i(TAG, "onSurfaceTextureSizeChanged()")
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            Log.i(TAG, "onSurfaceTextureUpdated()")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            Log.i(TAG, "onSurfaceTextureDestroyed()")
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            Log.i(TAG, "onSurfaceTextureAvailable()")
            safeCameraOpen()
        }
    }

    // Check for permission to open camera. If permission is not granted, request it. If it is, open camera.
    private fun safeCameraOpen() {
        if (ContextCompat.checkSelfPermission(this@ScanCameraActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@ScanCameraActivity, arrayOf(android.Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
        }
        else {
            val manager : CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
            manager.openCamera(cameraId, mStateCallback, null)
        }
    }

    /* ***************************************
    * Title: "The least you can do with camera2 API"
    * Author: Dziubek, M.
    * Date: 2017
    * Availability: https://android.jlelse.eu/the-least-you-can-do-with-camera2-api-2971c8c81b8b
    ****************************************** */
    private fun chooseOptimalSize(outputSizes: Array<Size>, width: Int, height: Int): Size {
        val preferredRatio = height / width.toDouble()
        var currentOptimalSize = outputSizes[0]
        var currentOptimalRatio = currentOptimalSize.width / currentOptimalSize.height.toDouble()
        for (currentSize in outputSizes) {
            val currentRatio = currentSize.width / currentSize.height.toDouble()
            if (Math.abs(preferredRatio - currentRatio) < Math.abs(preferredRatio - currentOptimalRatio)) {
                currentOptimalSize = currentSize
                currentOptimalRatio = currentRatio
            }
        }
        return currentOptimalSize
    }

    // Called after requesting permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted, open camera
                    safeCameraOpen()
                } else {
                    // permission denied, return to browser
                    Toast.makeText(this, "Need permission to open camera.", Toast.LENGTH_LONG).show()
                    val browserIntent = Intent(this, PhotoBrowserActivity::class.java)
                    startActivity(browserIntent)
                }
                return
            }
        }
    }

    // This is the callback function for openCamera method. If the camera is successfully opened, the onOpened method
    // will be called. The onOpened method will set the size of the image buffers, call createCaptureRequest,
    // and call createCaptureSession.
    private var mStateCallback: CameraDevice.StateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            Log.i(TAG, "onOpened")
            mCameraDevice = camera
            mTextureView.surfaceTexture?.let { texture ->
                texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
                val surface = Surface(texture)

                try {
                    mPreviewBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

                mPreviewBuilder.addTarget(surface)

                try {
                    mCameraDevice!!.createCaptureSession(arrayListOf(surface), mPreviewStateCallback, null)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

        }

        override fun onDisconnected(camera: CameraDevice?) {
            Log.e(TAG, "onDisconnected");
        }

        override fun onError(camera: CameraDevice?, int: Int) {
            Log.e(TAG, "onError")
        }
    }

    // Callback function for createCaptureSession method. If configured successfully,
    // build the preview.
    private val mPreviewStateCallback = object : CameraCaptureSession.StateCallback() {

        override fun onConfigured(session: CameraCaptureSession) {
            Log.i(TAG, "onConfigured")
            mPreviewSession = session

            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            val backgroundThread = HandlerThread("CameraPreview")
            backgroundThread.start()
            val backgroundHandler = Handler(backgroundThread.looper)

            try {
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }

        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e(TAG, "CameraCaptureSession Configure failed")
        }
    }

    companion object {
        const val TAG = "ScanCameraActivity"
        const val MY_PERMISSIONS_REQUEST_CAMERA = 123
    }
}

