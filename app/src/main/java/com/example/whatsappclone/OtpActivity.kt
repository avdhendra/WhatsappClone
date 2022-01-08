package com.example.whatsappclone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity(), View.OnClickListener {
    var PhoneNumber:String?=null
    lateinit var VerifyEt:TextView
    lateinit var waitingTv:TextView
    lateinit var resendBtn:MaterialButton
    lateinit var verifyBtn:MaterialButton
    lateinit var counterTv:TextView
    var mResendToken:PhoneAuthProvider.ForceResendingToken?=null
    var mVerificationId:String?=null
    lateinit var mCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth:FirebaseAuth
    lateinit var sendcodeEt:EditText
    private var mCountdown:CountDownTimer?=null //not null type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        VerifyEt=findViewById(R.id.verifyTv)
        waitingTv=findViewById(R.id.waitingTv)
        resendBtn=findViewById(R.id.resendBtn)
        verifyBtn=findViewById(R.id.verificationBtn)
        counterTv=findViewById(R.id.counterTv)
        mAuth= FirebaseAuth.getInstance()
    initView()
        startVerify()


    }

    private fun startVerify() {
        startPhoneNumberVerification(PhoneNumber!!)
        showTimer(60000)
        progressDialog = createProgressDialog("Sending a verification code", false)
        progressDialog.show()
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallBacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun showTimer(millisecond: Long) {
resendBtn.isEnabled=false
        mCountdown=object:CountDownTimer(millisecond,1000){
            override fun onTick(millisUntilFinised: Long) {
                counterTv.isVisible=true
                counterTv.text=getString(R.string.second_remaining,millisUntilFinised/1000)
            }

            override fun onFinish() {
                resendBtn.isEnabled=true
                counterTv.isVisible=false
            }

        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        if(mCountdown!=null)
        {
            mCountdown!!.cancel() //!! null pointer exception
        }
    }

    private fun initView() {
        PhoneNumber=intent.getStringExtra("phone_number");
        VerifyEt.setText(getString(R.string.verify_number,PhoneNumber))
        setSpannableString()
sendcodeEt=findViewById(R.id.sentcodeEt)
        // init click listener
        verifyBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)



        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
// This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                if(::progressDialog.isInitialized)
                {
                    progressDialog.dismiss()

                }
                val smsCode=credential.smsCode
                if(!smsCode.isNullOrBlank())
                {
sendcodeEt.setText(smsCode)
                }
                          signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if(::progressDialog.isInitialized)
                {
                    progressDialog.dismiss()
                }

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException", e)
                    Log.e("=========:", "FirebaseAuthInvalidCredentialsException " + e.message)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // The SMS quota for the project has been exceeded
                    Log.e("Exception:", "FirebaseTooManyRequestsException", e)
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
//for low level version which doesn't do auto verification save the verification code and the token
                progressDialog.dismiss()
                counterTv.isVisible = false
                // Save verification ID and resending token so we can use them later
                Log.e("onCodeSent==", "onCodeSent:$verificationId")
                mVerificationId = verificationId
                mResendToken= token
            }
        }

        
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    if (::progressDialog.isInitialized) {
                        progressDialog.dismiss()
                    }
                    //First Time Login
                    if (task.result?.additionalUserInfo?.isNewUser == true) {
                        showSignUpActivity()
                    } else {
                        showHomeActivity()
                    }
                } else {

                    if (::progressDialog.isInitialized) {
                        progressDialog.dismiss()
                    }

                    notifyUserAndRetry("Your Phone Number Verification is failed.Retry again!")
                }
            }
    }

    private fun notifyUserAndRetry(s: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(s)
            setPositiveButton("Ok"){_,_->
                showLoginActivity()
            }
        }

    }

    private fun showHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        
    }

    private fun showSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
        
    }

    private fun setSpannableString() {
        val span=SpannableString("Waiting to automatically detect an SMS sent to\nWrong Number")
        val clickableSpan=object:ClickableSpan(){
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
                ds.setColor(Color.GREEN)
            }

            override fun onClick(p0: View) {
                showLoginActivity()

            }
        }
        span.setSpan(clickableSpan,span.length-13,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod=LinkMovementMethod.getInstance()
        waitingTv.text=span
    }

    private fun showLoginActivity() {
        startActivity(Intent(this,LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    overridePendingTransition(0,0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
        overridePendingTransition(0,0)

    }
    fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
        return ProgressDialog(this).apply {
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(false)
            setMessage(message)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            verifyBtn->{
                // try to enter the code by yourself to handle the case
                // if user enter another sim card used in another phone ...
                var code = sendcodeEt.text.toString()
                if (code.isNotEmpty() && !mVerificationId.isNullOrEmpty()) {

                    progressDialog = createProgressDialog("Please wait...", false)
                    progressDialog.show()
                    val credential =
                        PhoneAuthProvider.getCredential(mVerificationId!!, code.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }
            resendBtn->{
                if(mResendToken!=null)
                {
                    resendVerificationCode(PhoneNumber.toString())
                    showTimer(6000)
                    progressDialog=createProgressDialog("Sending a verification code",false)
                    progressDialog.show()
                }
            }
        }
    }

    private fun resendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallBacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}