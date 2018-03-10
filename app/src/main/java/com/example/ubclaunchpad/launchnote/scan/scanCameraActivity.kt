package com.example.ubclaunchpad.launchnote.scan

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.ubclaunchpad.launchnote.R

import kotlinx.android.synthetic.main.activity_scan_camera.*

class ScanCameraActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_camera)
        setSupportActionBar(toolbar)

        }

    override fun onResume() {
        super.onResume()
        safeCameraOpen()
    }

    private fun safeCameraOpen(): Boolean {
        var manager : CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var cameraIds = manager.cameraIdList
        var cameraCallback = object: CameraDevice.StateCallback() {
            override fun onDisconnected(p0: CameraDevice?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(p0: CameraDevice?, p1: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onOpened(p0: CameraDevice?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        try {
            //releaseCameraAndPreview()
            manager.openCamera(cameraIds[0], cameraCallback, null)
        } catch (e: Exception) {
            Log.e(getString(R.string.app_name), "failed to open camera")
            e.printStackTrace()
            return false
        } catch(se: SecurityException) { // not sure if this is right, maybe should use checkPermission()
            Log.e(getString(R.string.app_name), "permission failed")
            se.printStackTrace()
            return false
        }

        return true
    }


    /*private fun releaseCameraAndPreview() {
        mPreview.setCamera(null)
        if (mCamera != null) {
            mCamera.release()
            mCamera = null
        }
    }*/

}

