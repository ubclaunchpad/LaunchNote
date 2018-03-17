package com.example.ubclaunchpad.launchnote.scan

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.ubclaunchpad.launchnote.R

import kotlinx.android.synthetic.main.activity_scan_camera.*
import android.view.TextureView
import android.util.Size
import android.hardware.camera2.params.StreamConfigurationMap
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraDevice
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.Surface
import android.hardware.camera2.CameraCaptureSession
import android.os.HandlerThread
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.view.Window
import android.view.WindowManager


class ScanCameraActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SimpleCamera"
        const val MY_PERMISSIONS_REQUEST_CAMERA = 123
    }

    private lateinit var mTextureView: TextureView
    private lateinit var mPreviewSize: Size
    private var mCameraDevice: CameraDevice? = null
    private lateinit var mPreviewBuilder: CaptureRequest.Builder
    private lateinit var mPreviewSession: CameraCaptureSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_camera)
    }

    override fun onResume() {
        super.onResume()
        mTextureView = findViewById(R.id.previewTextureView)
        mTextureView.surfaceTextureListener = mSurfaceTextureListner
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()

        if (mCameraDevice != null) {
            mCameraDevice?.close()
            mCameraDevice = null
        }
    }

    private var mSurfaceTextureListner: TextureView.SurfaceTextureListener = object: TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.i(TAG, "onSurfaceTextureSizeChanged()")
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.i(TAG, "onSurfaceTextureUpdated()")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.i(TAG, "onSurfaceTextureDestroyed()")
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            Log.i(TAG, "onSurfaceTextureAvailable()")
            // Do I really need to do this check?
            if (ContextCompat.checkSelfPermission(this@ScanCameraActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ScanCameraActivity, arrayOf(android.Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
            }
            else {
                safeCameraOpen()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted
                    safeCameraOpen()
                } else {
                    // permission denied
                }
                return
            }

            else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    private fun safeCameraOpen() {
        val manager : CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            mPreviewSize = map.getOutputSizes(SurfaceTexture::class.java)[0]
            manager.openCamera(cameraId, mStateCallback, null)
        }
        catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var mStateCallback: CameraDevice.StateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            Log.i(TAG, "onOpened")
            mCameraDevice = camera
            val texture = mTextureView.surfaceTexture
            if (texture == null) {
                Log.e(TAG, "texture is null")
                return
            }

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

        override fun onDisconnected(camera: CameraDevice?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.e(TAG, "onDisconnected");
        }

        override fun onError(camera: CameraDevice?, int: Int) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.e(TAG, "onError")
        }
    }

    private val mPreviewStateCallback = object : CameraCaptureSession.StateCallback() {

        override fun onConfigured(session: CameraCaptureSession) {
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
            Log.e(TAG, "CameraCaptureSession Configure failed")
        }
    }
}

