package net.tribls.models

import android.os.Parcel
import android.os.Parcelable

data class CriticModel(
    val bio: String,
    val display_name: String,
    val multimedia: MultiMediaModel?,
    val `seo-nmae`: String,   // TODO: typo???
    val sort_name: String,
    val status: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()?: "",
        source.readString()?: "",
        source.readParcelable<MultiMediaModel>(MultiMediaModel::class.java.classLoader),
        source.readString()?: "",
        source.readString()?: "",
        source.readString()?: ""
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(bio)
        writeString(display_name)
        writeParcelable(multimedia, 0)
        writeString(`seo-nmae`)
        writeString(sort_name)
        writeString(status)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CriticModel> {
        override fun createFromParcel(parcel: Parcel): CriticModel {
            return CriticModel(parcel)
        }

        override fun newArray(size: Int): Array<CriticModel?> {
            return arrayOfNulls(size)
        }
    }
}