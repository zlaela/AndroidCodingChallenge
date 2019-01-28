package utils

import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

// Lazily inflate our layouts
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

// Lazily load images into ImageViews using Glide
fun AppCompatImageView.loadImg(imageUrl : String) {
    Glide.with(context)
        .load(imageUrl)
        .into(this)
}
