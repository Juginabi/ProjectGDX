package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jukka on 9.9.2015.
 */
public class EntityInitializer {
    // Animations; down, up, left, right
    public Animation[] walkAnimations = new Animation[4];
    public Animation[] deathAnimations = new Animation[4];
    public TextureRegion[] idleFrames = new TextureRegion[4];

    // Stats of a monster wave
    public int id;
    public int hitpoints;
    public int velocity;
    public boolean flying;
    public boolean invisible;
    public String name;

    private final int FRAME_COLS = 11;
    private final int FRAME_ROWS = 4;

    /**
     * This utility class processes one large texture which contains 11x4 sprite animation frames
     * Class contains three public variables which contain the output of the process; walkAnimations, deathAnimatons and idleAnimations
     * @param spriteSheet Which is then split and processed
     */
    public EntityInitializer(Texture spriteSheet, FileHandle handle, int level, int wave) {
        boolean success = false;
        success = constructAnimations(spriteSheet);
        success = parseXML(handle, level, wave);

    }

    private boolean constructAnimations(Texture spriteSheet) {
        // Table containing all the frames of the sheet in 2D map
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/FRAME_COLS, spriteSheet.getHeight()/FRAME_ROWS);

        TextureRegion[] walkDownFrames = new TextureRegion[6];
        TextureRegion[] walkUpFrames = new TextureRegion[6];
        TextureRegion[] walkLeftFrames = new TextureRegion[6];
        TextureRegion[] walkRightFrames = new TextureRegion[6];
        TextureRegion[] deathDownFrames = new TextureRegion[4];
        TextureRegion[] deathUpFrames = new TextureRegion[4];
        TextureRegion[] deathLeftFrames = new TextureRegion[4];
        TextureRegion[] deathRightFrames = new TextureRegion[4];
        TextureRegion downIdleFrame = new TextureRegion();
        TextureRegion upIdleFrame = new TextureRegion();
        TextureRegion leftIdleFrame = new TextureRegion();
        TextureRegion rightIdleFrame = new TextureRegion();

        for (int i = 0; i < FRAME_ROWS; i++) {
            int walkIndex = 0;
            int deathIndex = 0;
            for (int j = 0; j < 11; j++) {
                switch (i) {
                    case 0:
                        if (j == 0) {
                            // Idle animation frame
                            downIdleFrame = tmp[i][j];
                        } else if (j < 7) {
                            // Walk animation frames
                            walkDownFrames[walkIndex++] = tmp[i][j];
                        } else {
                            // Death animation frames
                            deathDownFrames[deathIndex++] = tmp[i][j];
                        }
                        break;
                    case 1:
                        if (j == 0) {
                            // Idle animation frame
                            upIdleFrame = tmp[i][j];
                        } else if (j < 7) {
                            // Walk animation frames
                            walkUpFrames[walkIndex++] = tmp[i][j];
                        } else {
                            // Death animation frames
                            deathUpFrames[deathIndex++] = tmp[i][j];
                        }
                        break;
                    case 2:
                        if (j == 0) {
                            // Idle animation frame
                            leftIdleFrame = tmp[i][j];
                        } else if (j < 7) {
                            // Walk animation frames
                            walkLeftFrames[walkIndex++] = tmp[i][j];
                        } else {
                            // Death animation frames
                            deathLeftFrames[deathIndex++] = tmp[i][j];
                        }
                        break;
                    case 3:
                        if (j == 0) {
                            // Idle animation frame
                            rightIdleFrame = tmp[i][j];
                        } else if (j < 7) {
                            // Walk animation frames
                            walkRightFrames[walkIndex++] = tmp[i][j];
                        } else {
                            // Death animation frames
                            deathRightFrames[deathIndex++] = tmp[i][j];
                        }
                        break;
                    default:
                }
            }
        }

        walkAnimations[0] = new Animation(0.05f, walkDownFrames);
        walkAnimations[1] = new Animation(0.05f, walkUpFrames);
        walkAnimations[2] = new Animation(0.05f, walkLeftFrames);
        walkAnimations[3] = new Animation(0.05f, walkRightFrames);
        deathAnimations[0] = new Animation(0.1f, deathDownFrames);
        deathAnimations[1] = new Animation(0.1f, deathUpFrames);
        deathAnimations[2] = new Animation(0.1f, deathLeftFrames);
        deathAnimations[3] = new Animation(0.1f, deathRightFrames);
        return true;
    }

    private boolean parseXML(FileHandle handle, int level, int wave) {
        XmlReader reader = new XmlReader();
        try {
            XmlReader.Element rootElement = reader.parse(handle);
            XmlReader.Element waveStats = (XmlReader.Element)rootElement.getChildrenByName("Level" + level).iterator().next().getChildrenByName("Wave" + wave).iterator().next().getChildrenByName("Stats").iterator().next();
            ObjectMap<String, String> map = waveStats.getAttributes();
            this.velocity = Integer.parseInt(map.get("velocity"));
            this.id = Integer.parseInt(map.get("id"));
            this.name = map.get("name");
            this.invisible = Boolean.parseBoolean(map.get("invisible"));
            this.flying = Boolean.parseBoolean(map.get("flying"));
            this.hitpoints = Integer.parseInt(map.get("hitpoints"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
