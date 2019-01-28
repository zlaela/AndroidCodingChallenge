package net.tribls.androidcodingchallenge

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.tribls.androidcodingchallenge.utils.NetworkMonitor


class MainActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkMonitor.newInstance().startListening(this)

        /*
         * Avoid recreating the fragment when screen is rotated, unless this is the first launch
         * Preserves the bundle in the fragment when this activity is destroyed and re-created
         */
        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ReviewsFragment.newInstance())
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkMonitor.newInstance().stopListening(this)
    }
}
