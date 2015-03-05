package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jukka on 5.3.2015.
 */
public class AssetManager {
    public static String TAG = "Audiomanager";
    Map<String, Sound> weaponSounds;
    Sound defaulWeaponSound;

    public AssetManager() {
        // Initialization of arrays
        weaponSounds = new HashMap<String, Sound>();
        defaulWeaponSound = Gdx.audio.newSound(Gdx.files.internal("Audio/defaultLaser.ogg"));

        FileHandle[] files = Gdx.files.local("Audio/Laser/").list();
        for (FileHandle file : files) {
            Gdx.app.log(TAG, "Loading audio asset: " + file.name());
            weaponSounds.put(file.nameWithoutExtension(), Gdx.audio.newSound(file));
        }
    }

    public void playSound(String name) {
        Sound soundToBePlayed;
        soundToBePlayed = weaponSounds.get(name);
        if (soundToBePlayed == null) {
            defaulWeaponSound.play();
        } else {
            soundToBePlayed.play();
        }
    }

}
