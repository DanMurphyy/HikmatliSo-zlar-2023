package com.hfad.a1001hikmatlisoz

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.hfad.a1001hikmatlisoz.databinding.FragmentOnlyBinding
import com.hfad.a1001hikmatlisoz.notification.NotificationReceiver
import java.io.File
import java.io.FileOutputStream
import java.util.*

class OnlyFragment : Fragment() {
    private var _binding: FragmentOnlyBinding? = null
    private val binding get() = _binding!!
    private var mInterstitialAd: InterstitialAd? = null

    private val adapter: OnlyAdapter by lazy { OnlyAdapter() }
    private val mQuoteViewModel: QuoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnlyBinding.inflate(inflater, container, false)
        mobileAd()
        showRecyclerView()
        scheduleNotification(requireContext())
        mQuoteViewModel.getStartQuoteData().observe(viewLifecycleOwner) { data ->
            val shuffledData = data.shuffled()
            adapter.setData(shuffledData)
        }

        binding.share.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
            generateLayoutPhoto()
            shareLayoutPhoto("Ilova uchun havola: \n" +
                    "https://play.google.com/store/apps/details?id=com.hfad.a1001hikmatlisoz")
        }
        MobileAds.initialize(requireContext())
        val adInRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),
            "ca-app-pub-8558811277281829/9611254064",
            adInRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded")
                    mInterstitialAd = interstitialAd
                }
            })


        return (binding.root)
    }

    private fun scheduleNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val calendar1 = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar1.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

    private fun mobileAd() {
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false) {
                override fun onLayoutChildren(
                    recycler: RecyclerView.Recycler?,
                    state: RecyclerView.State?,
                ) {
                    super.onLayoutChildren(recycler, state)
                    scrollToPosition(adapter.itemCount / 2)
                }
            }

        recyclerView.layoutManager = linearLayoutManager

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

    }

    private fun generateLayoutPhoto(): Bitmap {
        // Find the root view of your layout
        val rootView = binding.root

        // Create a bitmap with the dimensions of the RecyclerView
        val bitmap = Bitmap.createBitmap(binding.recyclerView.width,
            binding.recyclerView.height,
            Bitmap.Config.ARGB_8888)
        // Create a canvas using the bitmap
        val canvas = Canvas(bitmap)
        // Draw the background of the root view onto the canvas
        rootView.draw(canvas)
        // Translate the canvas to the location of the RecyclerView within the root view
        canvas.translate(binding.recyclerView.left.toFloat(), binding.recyclerView.top.toFloat())
        // Draw the RecyclerView onto the canvas
        binding.recyclerView.draw(canvas)

        return bitmap
    }


    private fun shareLayoutPhoto(caption: String) {
        // Generate a bitmap of the specific view
        val bitmap = generateLayoutPhoto()

        // Save the bitmap to a temporary file
        val file = File(requireContext().cacheDir, "layout_image.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.close()

        // Create a share intent for the temporary file and the caption
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file))
        shareIntent.putExtra(Intent.EXTRA_TEXT, caption)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Show the share dialog
        startActivity(Intent.createChooser(shareIntent, "Share layout photo with caption"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}