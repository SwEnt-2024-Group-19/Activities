package com.android.sample.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.android.sample.model.network.NetworkManager
import com.android.sample.resources.C.Tag.OFFLINE_TOAST_MESSAGE

fun performOfflineAwareAction(
    context: Context,
    networkManager: NetworkManager,
    offlineMessage: String = OFFLINE_TOAST_MESSAGE,
    onPerform: () -> Unit
) {
    if (networkManager.isNetworkAvailable()) {
        onPerform()
    } else {
        Toast.makeText(context, offlineMessage, Toast.LENGTH_SHORT).show()
    }
}
