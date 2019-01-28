package net.tribls.models

import android.os.Parcel
import android.os.Parcelable

/**
 * A representation of the multimedia JSON object returned from the API
 */
data class MultiMediaModel(
    var height: Int,
    var src: String,
    var type: String,
    var width: Int
)  : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString()?: "",
        source.readString()?: "",
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(height)
        writeString(src)
        writeString(type)
        writeInt(width)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MultiMediaModel> = object : Parcelable.Creator<MultiMediaModel> {
            override fun createFromParcel(source: Parcel): MultiMediaModel = MultiMediaModel(source)
            override fun newArray(size: Int): Array<MultiMediaModel?> = arrayOfNulls(size)
        }
    }
}


/**
 * A representation of the link JSON object
 */
data class Link(
    var suggested_link_text: String,
    var type: String,
    var url: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()?: "",
        source.readString()?: "",
        source.readString()?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(suggested_link_text)
        writeString(type)
        writeString(url)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Link> = object : Parcelable.Creator<Link> {
            override fun createFromParcel(source: Parcel): Link = Link(source)
            override fun newArray(size: Int): Array<Link?> = arrayOfNulls(size)
        }
    }
}