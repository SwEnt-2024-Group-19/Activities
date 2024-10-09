package com.android.sample.ui.authentification

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import androidx.compose.material3.TextButton

import com.github.se.bootcamp.ui.navigation.NavigationActions
import com.github.se.bootcamp.ui.navigation.Screen

@Composable
fun SignInScreen(navigationActions: NavigationActions) {
    val context = LocalContext.current

    val launcher = rememberFirebaseAuthLauncher(onAuthComplete = { result ->
        Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
        Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
        // Navigate to OverviewScreen upon successful login
    }, onAuthError = {
        Log.e("SignInScreen", "Failed to sign in: ${it.statusCode}")
        Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show()
    })
    val token = stringResource(R.string.default_web_client_id)

    // The main container for the screen
    Scaffold(modifier = Modifier.fillMaxSize(), content = { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // App Logo Image
            Image(
                painter = painterResource(id = R.drawable.google_logo), // Ensure this drawable exists
                contentDescription = "App Logo", modifier = Modifier.size(110.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))



            Spacer(modifier = Modifier.height(16.dp))


            // Authenticate With Google Button
            GoogleSignInButton(onSignInClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(token).requestEmail().build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            })

            // If user already has an account, navigate to the sign in screen
            TextButton(
                onClick = {
                    navigationActions.navigateTo(Screen.SIGN_UP)
                }, modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(36.dp)
            ) {
                Text("No account yet?", fontSize = 16.sp)
            }

            // Continue as a guest button
            TextButton(
                onClick = {
                    navigationActions.navigateTo(Screen.OVERVIEW)
                }, modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(36.dp)
            ) {
                Text("Continue as a guest", fontSize = 16.sp)
            }

        }
    })
}


@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
    Button(
        onClick = onSignInClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button color
        shape = RoundedCornerShape(50), // Circular edges for the button
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp) // Adjust height as needed
            .testTag("loginButton")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            // Load the Google logo from resources
            Image(
                painter = painterResource(id = R.drawable.google_logo), // Ensure this drawable exists
                contentDescription = "Google Logo",
                modifier = Modifier
                    .size(30.dp) // Size of the Google logo
                    .padding(end = 8.dp)
            )

            // Text for the button
            Text(
                text = "Sign in with Google", color = Color.Gray, // Text color
                fontSize = 16.sp, // Font size
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit, onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}
