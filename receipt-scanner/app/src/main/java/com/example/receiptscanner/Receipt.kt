package com.example.receiptscanner

import android.os.Parcel
import android.os.Parcelable

data class Receipt(
    var title: String,
    var date: String,
    var items: MutableList<Item>,
    var total: Double = 0.0 // Default value for total
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Item.CREATOR)?.toMutableList() ?: mutableListOf(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(date)
        parcel.writeTypedList(items)
        parcel.writeDouble(total)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Receipt> {
        override fun createFromParcel(parcel: Parcel): Receipt = Receipt(parcel)
        override fun newArray(size: Int): Array<Receipt?> = arrayOfNulls(size)
    }
}
