package com.example.cinemax;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemax.utils.CustomYoutubePlayerUIController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PlayingTrailerActivity extends AppCompatActivity {
    String trailerPath;
    ImageView closeButton;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_trailer);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.setEnableAutomaticInitialization(false);
        getLifecycle().addObserver(youTubePlayerView);

        View customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.youtube_player_ui);

        trailerPath = getIntent().getStringExtra("videoId");
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                CustomYoutubePlayerUIController customPlayerUiController = new CustomYoutubePlayerUIController(customPlayerUi, youTubePlayer, youTubePlayerView);
                youTubePlayer.addListener(customPlayerUiController);

                YouTubePlayerUtils.loadOrCueVideo(youTubePlayer, getLifecycle(), trailerPath, 0F);
            }
        };

        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).ivLoadPolicy(3).ccLoadPolicy(1).rel(0).build();
        youTubePlayerView.initialize(listener, options);

        handleCloseVideo();
    }

    private void handleCloseVideo() {
        closeButton = findViewById(R.id.trailer_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}