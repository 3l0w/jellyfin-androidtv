package org.jellyfin.androidtv.playback.overlay;

import androidx.leanback.media.PlayerAdapter;

import org.jellyfin.androidtv.TvApp;
import org.jellyfin.androidtv.playback.CustomPlaybackOverlayFragment;
import org.jellyfin.androidtv.playback.PlaybackController;
import org.jellyfin.androidtv.util.DeviceUtils;
import org.jellyfin.androidtv.util.apiclient.StreamHelper;
import org.jellyfin.apiclient.model.dto.BaseItemDto;

public class VideoPlayerAdapter extends PlayerAdapter {

    private final PlaybackController playbackController;
    private CustomPlaybackOverlayFragment customPlaybackOverlayFragment;

    VideoPlayerAdapter(PlaybackController playbackController) {
        this.playbackController = playbackController;
    }

    @Override
    public void play() {
        playbackController.play(playbackController.getCurrentPosition());
    }

    @Override
    public void pause() {
        playbackController.pause();
    }

    @Override
    public void rewind() {
        playbackController.skip(-30000);
        updateCurrentPosition();
    }

    @Override
    public void fastForward() {
        playbackController.skip(30000);
        updateCurrentPosition();
    }

    @Override
    public void seekTo(long positionInMs) {
        playbackController.seek(positionInMs);
        updateCurrentPosition();
    }

    @Override
    public void next() {
        playbackController.next();
    }

    @Override
    public long getDuration() {
        return playbackController.getCurrentlyPlayingItem().getRunTimeTicks() != null ?
                playbackController.getCurrentlyPlayingItem().getRunTimeTicks() / 10000 : -1;
    }

    @Override
    public long getCurrentPosition() {
        return playbackController.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return playbackController.isPlaying();
    }

    @Override
    public long getBufferedPosition() {
        return getDuration();
    }

    void updateCurrentPosition() {
        getCallback().onCurrentPositionChanged(this);
    }

    void updatePlayState() {
        getCallback().onPlayStateChanged(this);
    }

    boolean hasSubs() {
        return StreamHelper.getSubtitleStreams(playbackController.getCurrentMediaSource()).size() > 0;
    }

    boolean hasMultiAudio() {
        return StreamHelper.getAudioStreams(playbackController.getCurrentMediaSource()).size() > 1;
    }

    boolean hasNextItem() {
        return playbackController.hasNextItem();
    }

    boolean isNativeMode() {
        return playbackController.isNativeMode();
    }

    boolean canSeek() {
        return !DeviceUtils.isFireTv() && playbackController.canSeek();
    }

    boolean isLiveTv() {
        return playbackController.isLiveTv();
    }

    void setMasterOverlayFragment(CustomPlaybackOverlayFragment customPlaybackOverlayFragment) {
        this.customPlaybackOverlayFragment = customPlaybackOverlayFragment;
    }

    CustomPlaybackOverlayFragment getMasterOverlayFragment() {
        return customPlaybackOverlayFragment;
    }

    boolean canRecordLieTv() {
        BaseItemDto currentlyPlayingItem = playbackController.getCurrentlyPlayingItem();
        TvApp application = TvApp.getApplication();
        return currentlyPlayingItem.getCurrentProgram() != null
                && application.canManageRecordings();
    }

    void toggleRecording() {
        BaseItemDto currentlyPlayingItem = playbackController.getCurrentlyPlayingItem();
        getMasterOverlayFragment().toggleRecording(currentlyPlayingItem);
    }

    boolean isRecording() {
        BaseItemDto currentProgram = playbackController.getCurrentlyPlayingItem().getCurrentProgram();
        if (currentProgram == null) {
            return false;
        } else {
            return currentProgram.getTimerId() != null;
        }
    }
}
