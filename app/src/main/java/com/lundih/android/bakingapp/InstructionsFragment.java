package com.lundih.android.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.lundih.android.bakingapp.step.Step;

import java.util.List;
import java.util.Objects;

public class InstructionsFragment extends Fragment implements Player.EventListener {
    CustomViewModel customViewModel;
    PlayerView exoPlayerPlayerView;
    TextView textViewInstructions;
    LinearLayout linearLayoutAesthetics;
    Button buttonPrevious;
    Button buttonNext;
    List<Step> steps;
    int position;
    Context context;
    SimpleExoPlayer exoPlayer;
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    public InstructionsFragment(){ }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_and_instructions, container, false);

        exoPlayerPlayerView = rootView.findViewById(R.id.exoPlayerPlayerView);
        textViewInstructions = rootView.findViewById(R.id.textViewStepDescription);
        LinearLayout linearLayoutNavigationButtons = rootView.findViewById(R.id.linearLayoutNavigationButtons);
        buttonPrevious = rootView.findViewById(R.id.buttonPreviousStep);
        buttonNext = rootView.findViewById(R.id.buttonNextStep);
        linearLayoutAesthetics = rootView.findViewById(R.id.linearLayoutAestheticsLandscape);

        customViewModel = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
        if (customViewModel.isTabMode()){
            linearLayoutNavigationButtons.setVisibility(View.GONE);
        }
        if (customViewModel.isLandscape() && !customViewModel.isTabMode()) {
            // Show content in fullscreen
            Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            linearLayoutAesthetics.setVisibility(View.GONE);
        }
        if (customViewModel.getRecipe() != null) {
            steps = customViewModel.getRecipe().getSteps();
            if (customViewModel.getPositionLiveData() == null) {
                customViewModel.setPosition(0);
            }
            //noinspection Convert2Lambda
            customViewModel.getPositionLiveData().observe((LifecycleOwner) context, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    position = customViewModel.getPosition();
                    textViewInstructions.setText(steps.get(position).getDescription());
                    initialisePlayer(steps.get(position).getVideoURL());
                    // Array size is typical, expect normal behaviour with the buttons
                    if (steps.size() > 3) {
                        if (integer == 0) {
                            disableButton(buttonPrevious);
                        } else if (integer == 1) {
                            if (!buttonPrevious.isEnabled()) enableButton(buttonPrevious);
                        } else if (integer == steps.size() - 2) {
                            if (!buttonNext.isEnabled()) enableButton(buttonNext);
                        } else if (integer == steps.size() - 1) {
                            disableButton(buttonNext);
                        }
                    } else { // Smaller array sizes, have to cater for them differently
                        if (steps.size() == 3) {
                            if (integer == 0) {
                                disableButton(buttonPrevious);
                            } else if (integer == 1) {
                                enableButton(buttonPrevious);
                                enableButton(buttonNext);
                            } else if (integer == 2)
                                disableButton(buttonNext);
                        } else if (steps.size() == 2) {
                            if (integer == 0) {
                                disableButton(buttonPrevious);
                                enableButton(buttonNext);
                            } else if (integer == 1) {
                                enableButton(buttonPrevious);
                                disableButton(buttonNext);
                            }
                        } else if (steps.size() == 1) {
                            disableButton(buttonPrevious);
                            disableButton(buttonNext);
                        }
                    }
                }
            });
            textViewInstructions.setText(steps.get(position).getDescription());
            if (!customViewModel.isTabMode()) {
                buttonPrevious.setOnClickListener(view -> movePrevious());
                buttonNext.setOnClickListener(View -> moveNext());
            }
        }

        return rootView;
    }

    @Override
    public void onStop() {
        releasePlayer();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (customViewModel.isLandscape() && !customViewModel.isTabMode()) {
            Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        super.onDestroyView();
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(getContext(), getString(R.string.message_error_playing_video), Toast.LENGTH_LONG).show();
    }

    private void movePrevious() {
        if (position > 0) {
            position -= 1;
            customViewModel.setPosition(position);
        }
    }

    private void moveNext(){
        if (position < steps.size()-1) {
            position += 1;
            customViewModel.setPosition(position);
        }
    }

    private void disableButton(Button button) {
        button.setEnabled(false);
        if (button == buttonPrevious)
            button.setBackgroundResource(R.drawable.shape_button_previous_bg_disabled);
        else if (button == buttonNext)
            button.setBackgroundResource(R.drawable.shape_button_next_bg_disabled);
    }

    private void enableButton(Button button) {
        button.setEnabled(true);
        if (button == buttonPrevious)
            button.setBackgroundResource(R.drawable.shape_button_previous_bg);
        else if (button == buttonNext)
            button.setBackgroundResource(R.drawable.shape_button_next_bg);
    }

    private void initialisePlayer(String videoUrl) {
        try {
            exoPlayer = new SimpleExoPlayer.Builder(context).build();
            exoPlayerPlayerView.setPlayer(exoPlayer);
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(Objects.requireNonNull(getContext()),
                    Util.getUserAgent(getContext(), getString(R.string.app_name_full)));
            // This is the MediaSource representing the media to be played.
            if (videoUrl != null && !videoUrl.equals("")) {
                MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUrl));
                // Prepare the player with the source.
                exoPlayer.prepare(source);
                exoPlayerPlayerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
                exoPlayer.setPlayWhenReady(true);
            } else {
                Toast.makeText(getContext(), getString(R.string.message_no_video_found), Toast.LENGTH_LONG).show();
                releasePlayer();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}