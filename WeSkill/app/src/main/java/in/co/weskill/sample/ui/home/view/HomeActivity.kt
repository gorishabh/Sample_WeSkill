package `in`.co.weskill.sample.ui.home.view

import `in`.co.weskill.sample.databinding.ActivityHomeBinding
import `in`.co.weskill.sample.ui.home.viewmodel.HomeActivityVM
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util

class HomeActivity : AppCompatActivity(), Player.Listener {

    companion object {

        fun start(context: Context, action: String? = null): Intent {
            return Intent(context, HomeActivity::class.java)
        }

        private const val mp4Url: String = "https://html5demos.com/assets/dizzy.mp4"
        private const val mp3Url: String =
            "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"

    }

//    private val dashUrl = "https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears_uhd.mpd"
//    private val urlList = listOf(mp4Url to "default")
//
//    private val dataSourceFactory: DataSource.Factory by lazy {
//        DefaultDataSourceFactory(this, "exoplayer-sample")
//    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeActivityVM: HomeActivityVM
    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition = 0L
    private var currentWindow = 0
    private var playWhenReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setUpViewModel()
//        setUpObservers()
    }

//    private fun setUpViewModel() {
//        homeActivityVM = ViewModelProvider(
//            this, ViewModelFactory(
//                ApiHelperImpl(RetrofitBuilder.apiService)
//            )
//        ).get(HomeActivityVM::class.java)
//    }
//
//    private fun setUpObservers() {
//        homeActivityVM.fetchVideos().observe(this, {
//            var msg = null
//            when (it.status) {
//                Status.SUCCESS -> TODO()
//                Status.ERROR -> TODO()
//                Status.LOADING -> TODO()
//            }
//            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//        })
//    }

    // region player

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(mp4Url)
        simpleExoplayer.setMediaItem(mediaItem)
//        simpleExoplayer.addMediaItem()
        simpleExoplayer.prepare()
        binding.exoplayerView.player = simpleExoplayer
        simpleExoplayer.seekTo(currentWindow, playbackPosition)
        simpleExoplayer.playWhenReady = playWhenReady
    }

    private fun releasePlayer() {
        simpleExoplayer.run {
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            playWhenReady = playWhenReady
            release()
        }
    }

    // endregion


    // region helpers

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.exoplayerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    // endregion

}