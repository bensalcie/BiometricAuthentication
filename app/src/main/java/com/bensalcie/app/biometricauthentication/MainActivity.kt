package com.bensalcie.app.biometricauthentication

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private var cancellationSignal :CancellationSignal?=null
    private val authenticationCallback : BiometricPrompt.AuthenticationCallback
    get() =
        @RequiresApi(Build.VERSION_CODES.P)
        object :BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyUSer("Authentication Error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUSer("Authentication succeeded")
                startActivity(Intent(this@MainActivity,TopSecretActivity::class.java))

            }


        }
    lateinit var btnAuthenticate:MaterialButton
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnAuthenticate = findViewById(R.id.btnAuthenticate)
        checkBioMetricSupport()
        btnAuthenticate.setOnClickListener {
            val biometricPrompt:BiometricPrompt = BiometricPrompt.Builder(this).setTitle("Please Authenticate to ${getString(R.string.app_name)} using Your Biometrics")
            .setSubtitle("Authentication is Required").setDescription("We value your security, and so we need your Biometric data to secure your data.")
                .setNegativeButton("Cancel",this.mainExecutor,DialogInterface.OnClickListener {
                        _, _ ->
                notifyUSer("Authentication Cancelled")

            }).build()
            biometricPrompt.authenticate(getCancelationSignal(),mainExecutor,authenticationCallback)

        }

    }


    private fun getCancelationSignal():CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUSer("Authentication was cancelled by user")

        }

        return  cancellationSignal as CancellationSignal
    }
    private fun checkBioMetricSupport():Boolean{
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE)as KeyguardManager
        if (!keyguardManager.isKeyguardSecure){
            notifyUSer("Biometrics has not been enabled in Settings")
            return  false
        }
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED){
            notifyUSer("Biometrics Authenticaton permission needed")
            return  false
        }


         return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
             true
         }else
             true
    }


    private fun notifyUSer(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }
}