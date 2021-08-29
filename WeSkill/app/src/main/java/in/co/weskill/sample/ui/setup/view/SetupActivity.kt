package `in`.co.weskill.sample.ui.setup.view

import `in`.co.weskill.sample.R
import `in`.co.weskill.sample.ui.home.HomeActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class SetupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val gsc by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        return@lazy GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (null != currentUser) navigateToHome()
        else navigateToSignUp()
    }

    private fun navigateToSignUp() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<SignUpFragment>(R.id.fragment_container_view)
        }
    }

    fun navigateToHome() {
        startActivity(HomeActivity.start(applicationContext))
        finish()
    }

    fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Timber.e("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(this, "Problem Logging in!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val googleSignInAccount = task.getResult(ApiException::class.java)!!
                    Timber.i("${googleSignInAccount.displayName} ${googleSignInAccount.email}")
                    firebaseAuthWithGoogle(googleSignInAccount.idToken!!)
                } catch (e: ApiException) {
                    Timber.e("Google Sign In failed $e")
                }
            }
        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 12345
    }

}