package com.hfad.a1001hikmatlisoz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class QuoteViewModel(application: Application) : AndroidViewModel(application) {

    fun getStartQuoteData(): LiveData<List<Quotes>> {
        return MutableLiveData<List<Quotes>>().apply {
            value = QuoteManager.startQuote
        }
    }

}