package com.jason840917.consulting
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*




class activity_register: AppCompatActivity() {
    private val TAG="activity_register"
    private val DOMAIN_NAME = "gmail.com"
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        btn_register.setOnClickListener {
            Log.d(TAG,"onClik: Attempting to register.")
            if(!isEmpty(input_email.text.toString())
                &&  !isEmpty(input_password.text.toString() )
                &&  !isEmpty(input_confirm_password.text.toString())) {
                //check is user have company email
                if(isValidDomain(input_email.text.toString())){
                    //check if password match
                    if(doStringMatch(input_password.text.toString(),input_confirm_password.text.toString())){
                        registerNewEmail(input_email.text.toString(),input_password.text.toString())
                    }else
                        Toast.makeText(this,"Passwords do not match.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Please register with company Email.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"All Fields are required!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerNewEmail(email: String, pass: String) {
        registerProgressBar.visibility = ProgressBar.VISIBLE
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
                registerProgressBar.visibility = ProgressBar.GONE
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            var inm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inm?.let {
                inm.hideSoftInputFromWindow(input_password.windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            auth.signOut()
        }
    }

    private fun doStringMatch(pass: String, passConfirm: String): Boolean {
        return pass.equals(passConfirm)
    }

    private fun isValidDomain(email:String): Boolean {
        Log.d(TAG,"isValidDomain: verifying if email has correct domain: "+ email )
        var domain = email.substring(email.indexOf("@")+1).toLowerCase()
        Log.d(TAG,"isValidDomain: user domain:  "+ domain )
        return domain.equals(DOMAIN_NAME)
    }

    private fun isEmpty(string:String):Boolean{
        return string.equals("")
    }
}