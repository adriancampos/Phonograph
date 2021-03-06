package com.kabouzeid.gramophone.appshortcuts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;

import com.kabouzeid.gramophone.appshortcuts.shortcuttype.LastAddedShortcutType;
import com.kabouzeid.gramophone.appshortcuts.shortcuttype.ShuffleAllShortcutType;
import com.kabouzeid.gramophone.appshortcuts.shortcuttype.TopTracksShortcutType;
import com.kabouzeid.gramophone.loader.LastAddedLoader;
import com.kabouzeid.gramophone.loader.SongLoader;
import com.kabouzeid.gramophone.loader.TopAndRecentlyPlayedTracksLoader;
import com.kabouzeid.gramophone.model.Song;
import com.kabouzeid.gramophone.service.MusicService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @author Adrian Campos
 */

public class AppShortcutLauncherActivity extends Activity {
    public static final String KEY_SHORTCUT_TYPE = "com.kabouzeid.gramophone.appshortcuts.ShortcutType";

    public static final int SHORTCUT_TYPE_SHUFFLE_ALL = 0;
    public static final int SHORTCUT_TYPE_TOP_TRACKS = 1;
    public static final int SHORTCUT_TYPE_LAST_ADDED = 2;
    public static final int SHORTCUT_TYPE_NONE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @ShortcutType
        int shortcutType = SHORTCUT_TYPE_NONE;

        //Set shortcutType from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //noinspection WrongConstant
            shortcutType = extras.getInt(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE);
        }

        switch (shortcutType) {
            case SHORTCUT_TYPE_SHUFFLE_ALL:
                startServiceWithSongs(MusicService.SHUFFLE_MODE_SHUFFLE,
                        SongLoader.getAllSongs(getApplicationContext()));
                DynamicShortcutManager.reportShortcutUsed(this, ShuffleAllShortcutType.getId());
                break;
            case SHORTCUT_TYPE_TOP_TRACKS:
                startServiceWithSongs(MusicService.SHUFFLE_MODE_NONE,
                        TopAndRecentlyPlayedTracksLoader.getTopTracks(getApplicationContext()));
                DynamicShortcutManager.reportShortcutUsed(this, TopTracksShortcutType.getId());
                break;
            case SHORTCUT_TYPE_LAST_ADDED:
                startServiceWithSongs(MusicService.SHUFFLE_MODE_NONE,
                        LastAddedLoader.getLastAddedSongs(getApplicationContext()));
                DynamicShortcutManager.reportShortcutUsed(this, LastAddedShortcutType.getId());
                break;
        }

        finish();
    }

    private void startServiceWithSongs(int shuffleMode, ArrayList<Song> songs) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MusicService.INTENT_EXTRA_SONGS, songs);
        bundle.putInt(MusicService.INTENT_EXTRA_SHUFFLE_MODE, shuffleMode);

        intent.putExtras(bundle);

        startService(intent);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHORTCUT_TYPE_SHUFFLE_ALL, SHORTCUT_TYPE_TOP_TRACKS, SHORTCUT_TYPE_LAST_ADDED, SHORTCUT_TYPE_NONE})
    public @interface ShortcutType {
    }
}
