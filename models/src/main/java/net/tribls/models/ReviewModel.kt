package net.tribls.models

import android.os.Parcel
import android.os.Parcelable

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
data class ReviewsList(
    var lastOffset: Int,
    val reviews: List<ReviewModel>
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.createTypedArrayList(ReviewModel.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(lastOffset)
        writeTypedList(reviews)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReviewsList> = object : Parcelable.Creator<ReviewsList> {
            override fun createFromParcel(source: Parcel): ReviewsList = ReviewsList(source)
            override fun newArray(size: Int): Array<ReviewsList?> = arrayOfNulls(size)
        }
    }
}

data class ReviewModel(
    val display_title: String,
    val mpaa_rating: String,
    val byline: String,
    val headline: String,
    val summary_short: String,
    val publication_date: String,
    val multimedia: MultiMediaModel?
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString()?: "",
        source.readString()?: "",
        source.readString()?: "",
        source.readString()?: "",
        source.readString()?: "",
        source.readString()?: "",
        source.readParcelable<MultiMediaModel>(MultiMediaModel::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(display_title)
        writeString(mpaa_rating)
        writeString(byline)
        writeString(headline)
        writeString(summary_short)
        writeString(publication_date)
        writeParcelable(multimedia, 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ReviewModel> {
        override fun createFromParcel(parcel: Parcel): ReviewModel {
            return ReviewModel(parcel)
        }

        override fun newArray(size: Int): Array<ReviewModel?> {
            return arrayOfNulls(size)
        }
    }
}