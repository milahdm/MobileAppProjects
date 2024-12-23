package com.example.receiptscanner

import android.os.Parcel
import android.os.Parcelable

data class Item(
    var name: String,
    var price: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item = Item(parcel)
        override fun newArray(size: Int): Array<Item?> = arrayOfNulls(size)
    }
}
