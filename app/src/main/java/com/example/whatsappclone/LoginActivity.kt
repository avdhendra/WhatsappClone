package com.example.whatsappclone

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker

class LoginActivity : AppCompatActivity() {
    companion object {
        var CREDENTIAL_PICKER_REQUEST = 1
    }


    lateinit var  PhonenumberEt:EditText
    lateinit var Submit:MaterialButton
    lateinit var countryCode:String
    lateinit var ccp:CountryCodePicker
    lateinit var phonenumber:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        PhonenumberEt=findViewById(R.id.phoneNumberEt)
        ccp=findViewById(R.id.ccp)
        Submit=findViewById(R.id.nextBtn)
        var count:Int=1
        PhonenumberEt.addTextChangedListener {

            Submit.isEnabled=!(it.isNullOrEmpty()||it.length>10)
            if(count==1){
                phoneSelection()
                count++
            }


        }
        Submit.setOnClickListener {
            checkNumber()

        }
    }



    private fun phoneSelection() {
        // To retrieve the Phone Number hints, first, configure
        // the hint selector dialog by creating a HintRequest object.
        val hintRequest=HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
        val options=CredentialsOptions.Builder().forceEnableSaveDialog().build()
        //pass the HinRequest object to credentialClient.getHinpickerIntent()
        // to get an intent to prompt the user to
        // choose a phone number.
        val credentialsClient= Credentials.getClient(applicationContext,options).getHintPickerIntent(hintRequest)
        try {
startIntentSenderForResult(credentialsClient.intentSender, CREDENTIAL_PICKER_REQUEST,null,0,0,0,
    Bundle()
)
        }
        catch (e:IntentSender.SendIntentException){
            e.printStackTrace()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {

            // get data from the dialog which is of type Credential
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

            // set the received data t the text view
            credential?.apply {
                PhonenumberEt.setText(credential.id.toString())
            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }

    private fun checkNumber() {
        countryCode=ccp.selectedCountryCodeWithPlus
       phonenumber  =countryCode+PhonenumberEt.text.toString()

notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number : $phonenumber\n"+"Is this OK or Would you like to edit the number ?")
            setPositiveButton("Ok"){_,_->
                ShowOtpActivity()

            }
            setNegativeButton("Edit"){dialog,which->
                dialog.dismiss()
                PhonenumberEt.focusable= View.FOCUSABLE
            }
            setCancelable(false)
            create()
            show()
        }
    }
    private fun ShowOtpActivity(){
        startActivity(Intent(this,OtpActivity::class.java).putExtra("phone_number",phonenumber))
finish()
    }
}