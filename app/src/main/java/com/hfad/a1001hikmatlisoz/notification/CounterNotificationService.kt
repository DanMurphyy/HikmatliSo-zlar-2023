package com.hfad.a1001hikmatlisoz

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationService(private val context: Context) {

    var mQuoteViewModel: QuoteViewModel = QuoteViewModel(application = Application())

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification() {
        val quoteData = mQuoteViewModel.getStartQuoteData().value?.shuffled()?.firstOrNull()
        val quote = quoteData?.quote
        val author = quoteData?.author
        if (!author.isNullOrEmpty() && !quote.isNullOrEmpty()) {

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            )
            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(quote)

            val notification = NotificationCompat.Builder(context, DAILY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(author)
                .setContentText(quote)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(1, notification)
        }
    }


    companion object {
        const val DAILY_CHANNEL_ID = "1"
    }

}
