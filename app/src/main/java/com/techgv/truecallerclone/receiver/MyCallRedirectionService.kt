package com.techgv.truecallerclone.receiver

import android.net.Uri
import android.os.Build
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
class MyCallRedirectionService: CallRedirectionService() {
    private val tag = "MyCallRedirectionService"
    override fun onPlaceCall(
        handle: Uri,
        initialPhoneAccount: PhoneAccountHandle,
        allowInteractiveResponse: Boolean
    ) {
        Log.d(tag, "onPlaceCall: ")
    }
}