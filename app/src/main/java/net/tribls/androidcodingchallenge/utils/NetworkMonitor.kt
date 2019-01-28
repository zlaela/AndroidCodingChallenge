package net.tribls.androidcodingchallenge.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v4.content.LocalBroadcastManager

class NetworkMonitor {
    private val receiver = ConnectivityReceiver()
    private var filter : IntentFilter? = null
    private var thisContext: Context? = null


    fun startListening(context: Context) {
        if(thisContext == null) {
            filter = IntentFilter()
            filter?.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }

        context.registerReceiver(receiver, filter)
        thisContext = context
    }

    fun stopListening(context: Context) {
        try {
            context.unregisterReceiver(receiver)
            thisContext = null
        } catch (e:Exception){
            // don't really wanna die if the receiver isn't registered
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NetworkMonitor()
    }
}

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnected

        val body = if(isConnected) {
            BROADCAST_CONNECTIVITY_GAINED
        } else {
            BROADCAST_CONNECTIVITY_LOST
        }
        // Tell anyone listening that the user's data has changed
        val connectivityChanged = Intent(body)
        LocalBroadcastManager.getInstance(context).sendBroadcast(connectivityChanged)
    }
}
