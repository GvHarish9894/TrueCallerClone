package com.techgv.truecallerclone

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.techgv.truecallerclone.ui.theme.TrueCallerCloneTheme


class MainActivity : ComponentActivity() {

    val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1
    val MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2
    val MY_PERMISSIONS_REQUEST_SYSTEM_ALERT_WINDOW = 3

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrueCallerCloneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (applicationContext.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
            )
        }

        if (applicationContext.checkSelfPermission(android.Manifest.permission.PROCESS_OUTGOING_CALLS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.PROCESS_OUTGOING_CALLS),
                MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS
            )
        }

        if (applicationContext.checkSelfPermission(android.Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.READ_CALL_LOG),
                MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS
            )
        }


        /*  Check if Android M or higher Show alert dialog to the user saying a separate permission is needed Launch the settings activity if the user prefers*/
        if (!Settings.canDrawOverlays(this)) {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(myIntent)
        }
    }

}
