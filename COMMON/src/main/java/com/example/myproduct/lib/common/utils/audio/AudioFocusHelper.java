/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myproduct.lib.common.utils.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import com.example.myproduct.lib.common.utils.log.Log;

/**
 * Convenience class to deal with audio focus. This class deals with everything related to audio
 * focus: it can request and abandon focus, and will intercept focus change events and deliver
 * them to a MusicFocusable interface.
 * <p/>
 * This class can only be used on SDK level 8 and above, since it uses API features that are not
 * available on previous SDK's.
 */
@TargetApi(8)
public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
    public final static String LOG_TAG = "AudioFocusHelper";

    // do we have audio focus?
    public enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }

    private AudioFocus mAudioFocus;

    private AudioManager mAM;
    private MusicFocusable mFocusable;

    public AudioFocusHelper(Context ctx, MusicFocusable focusable) {
        mAM = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        mFocusable = focusable;
        mAudioFocus = AudioFocus.NoFocusNoDuck;
    }

    /**
     * Requests audio focus. Returns whether request was successful or not.
     *
     * @param audioFocusGainType the type of focus gain, one of
     *                           {@link AudioManager#AUDIOFOCUS_GAIN}
     *                           {@link AudioManager#AUDIOFOCUS_GAIN_TRANSIENT}
     *                           {@link AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK}
     *                           {@link AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE}
     */
    public boolean requestFocus(int audioFocusGainType) {
        if (audioFocusGainType != AudioManager.AUDIOFOCUS_GAIN
                && audioFocusGainType != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                && audioFocusGainType != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                && audioFocusGainType != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE) {
            throw new IllegalArgumentException("Illegal audio focus gain type: " + audioFocusGainType
                    + ", must 'AudioManager#AUDIOFOCUS_GAIN' or 'AudioManager#AUDIOFOCUS_GAIN_*'!");
        }

        if (Build.VERSION.SDK_INT < 19) {
            if (audioFocusGainType == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE) {
                audioFocusGainType = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
                Log.w(
                        LOG_TAG,
                        "AudioFocusHelper#requestFocus(int) below Api-19 AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE change to AudioManager.AUDIOFOCUS_GAIN_TRANSIENT.");
            }
        }

        boolean isSuccess = AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAM.requestAudioFocus(this, AudioManager.STREAM_MUSIC, audioFocusGainType);
        if (isSuccess) {
            mAudioFocus = AudioFocus.Focused;
        }
        return isSuccess;
    }

    /**
     * Abandons audio focus. Returns whether request was successful or not.
     */
    public boolean abandonFocus() {
        boolean isSuccess = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAM.abandonAudioFocus(this);
        if (isSuccess) {
            mAudioFocus = AudioFocus.NoFocusNoDuck;
        }
        return isSuccess;
    }

    /**
     * Called by AudioManager on audio focus changes. We implement this by calling our
     * MusicFocusable appropriately to relay the message.
     */
    public void onAudioFocusChange(int focusChange) {
        if (mFocusable == null) return;
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mAudioFocus = AudioFocus.Focused;
                if (mFocusable != null) {
                    mFocusable.onGainedAudioFocus();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mAudioFocus = AudioFocus.NoFocusNoDuck;
                if (mFocusable != null) {
                    mFocusable.onLostAudioFocus(false);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mAudioFocus = AudioFocus.NoFocusCanDuck;
                if (mFocusable != null) {
                    mFocusable.onLostAudioFocus(true);
                }
                break;
            default:
        }
    }
}
