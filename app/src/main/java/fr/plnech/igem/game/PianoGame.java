/*
=======================================================================
BactMan Adventures | Scientific popularisation through mini-games
Copyright (C) 2015 IONIS iGEM Team
Distributed under the GNU GPLv3 License.
(See file LICENSE.txt or copy at https://www.gnu.org/licenses/gpl.txt)
=======================================================================
*/

package fr.plnech.igem.game;

import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import fr.plnech.igem.game.managers.ResMan;
import fr.plnech.igem.game.model.HUDElement;
import fr.plnech.igem.game.model.LandscapeGame;
import fr.plnech.igem.game.model.TouchableAnimatedSprite;
import fr.plnech.igem.game.model.WorldObject;
import fr.plnech.igem.game.model.res.FontAsset;
import fr.plnech.igem.game.model.res.GFXAsset;
import fr.plnech.igem.game.piano.Base;
import fr.plnech.igem.game.piano.Key;
import fr.plnech.igem.game.piano.Polymerase;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PianoGame extends LandscapeGame {
    private static final String TAG = "PianoGame";

    private static final int INIT_SCORE = 0;
    private static final float CREATION_INTERVAL = 0.05f;
    private static final float DURATION_SHADER = 0.25f;

    private HUDElement HUDScore;
    private HUDElement HUDTime;

    private int gameTime;
    private final ArrayList<Base> bases = new ArrayList<>();
    private final ArrayList<Base> baseCpls = new ArrayList<>();
    private final HashMap<Key.Type, Key> keyMap = new HashMap<>();

    private int currentBaseIndex = 0;

    public PianoGame(AbstractGameActivity pActivity) {
        super(pActivity);
    }

    @Override
    public List<GFXAsset> getGraphicalAssets() {
        if (graphicalAssets.isEmpty()) {
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_BG, 1841, 1395, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_L_PHO, 732, 512, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_L_PHO_CPL, 732, 512, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_POLY, 1536, 1014, 0, 0, 2, 1));

            /* Bases */
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_A, 317, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_A_CPL, 317, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_T, 334, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_T_CPL, 334, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_G, 334, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_G_CPL, 334, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_C, 331, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_C_CPL, 329, 1024, 0, 0));

            /* Effects */
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_SHADER_OK, 329, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.PIANO_SHADER_KO, 329, 1024, 0, 0));

            /* HUD */
            graphicalAssets.add(new GFXAsset(ResMan.HUD_TIME, 1885, 1024, 0, 0));
            graphicalAssets.add(new GFXAsset(ResMan.HUD_SCORE, 1885, 1024, 0, 0));
        }
        return graphicalAssets;
    }

    @Override
    public List<GFXAsset> getProfAssets() {
        if (profAssets.isEmpty()) {
            final int profWidth = 2400;
            final int profHeight = 1440;
            if (Locale.getDefault().getLanguage().equals("fr")) {
                profAssets.add(new GFXAsset(ResMan.PROF_PIANO_1_FR, profWidth, profHeight));
                profAssets.add(new GFXAsset(ResMan.PROF_PIANO_2_FR, profWidth, profHeight));
            } else {
                profAssets.add(new GFXAsset(ResMan.PROF_PIANO_1, profWidth, profHeight));
                profAssets.add(new GFXAsset(ResMan.PROF_PIANO_2, profWidth, profHeight));
            }
        }
        return profAssets;
    }

    @Override
    public List<FontAsset> getFontAssets() {
        if (fontAssets.isEmpty()) {
            fontAssets.add(new FontAsset(ResMan.F_HUD_BIN, ResMan.F_HUD_BIN_SIZE, ResMan.F_HUD_BIN_COLOR, ResMan.F_HUD_BIN_ANTI));
        }
        return fontAssets;
    }

    @Override
    public List<HUDElement> getHudElements() {
        if (elements.isEmpty()) {
            final ITiledTextureRegion textureScore = activity.getTexture(ResMan.HUD_SCORE);
            final ITiledTextureRegion textureTime = activity.getTexture(ResMan.HUD_TIME);

            final float scale = 0.08f;

            Vector2 posS = new Vector2(5, 0);
            Vector2 offS = new Vector2(65, 23);
            Vector2 posT = new Vector2(350, 0);
            Vector2 offT = new Vector2(345, 21);

            IFont fontRoboto = activity.getFont(FontAsset.name(ResMan.F_HUD_BIN, ResMan.F_HUD_BIN_SIZE, ResMan.F_HUD_BIN_COLOR, ResMan.F_HUD_BIN_ANTI));
            Log.d(TAG, "getHudElements - Score: sprite: " + posS + " - text:" + offS.add(posS));
            Log.d(TAG, "getHudElements - Time:  sprite: " + posT + " - text:" + offT.add(posT));

            final VertexBufferObjectManager vbom = activity.getVBOM();

            HUDScore = new HUDElement()
                    .buildSprite(posS, textureScore, vbom, scale)
                    .buildText("", "31337".length(), offS, fontRoboto, vbom);
            HUDTime = new HUDElement()
                    .buildSprite(posT, textureTime, vbom, scale)
                    .buildText("", 8, offT, fontRoboto, vbom)
                    .setUrgent(false);

            elements.add(HUDScore);
            elements.add(HUDTime);
        }

        return elements;
    }

    @Override
    public Scene prepareScene() {
        Scene scene = activity.getScene();

        resetGamePoints();
        final SmoothCamera camera = activity.getCamera();
        final VertexBufferObjectManager vbom = activity.getVBOM();

        scene.setBackground(new SpriteBackground(new Sprite(0, 0, camera.getWidth(), camera.getHeight(),
                activity.getTexture(ResMan.PIANO_BG), vbom)));

        Polymerase polymerase = new Polymerase(189, 35, activity);
        scene.getChildByIndex(AbstractGameActivity.LAYER_FOREGROUND).attachChild(polymerase.getSprite());

        createKeys();
        createADN();

        scene.setTouchAreaBindingOnActionDownEnabled(true);

        TimerHandler myTimer = new TimerHandler(1, true, new ITimerCallback() {
            public void onTimePassed(TimerHandler pTimerHandler) {
                decrementTime();
            }
        });
        scene.registerUpdateHandler(myTimer);
        return scene;
    }

    private void createADN() {
        createADN(15);
    }

    private void createADN(final int count) {
        if (count > 0) {
            createBase();
            activity.registerUpdateHandler(CREATION_INTERVAL, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    createADN(count - 1);
                }
            });
        }
    }

    private void createBase() {
        shiftBases();
        createBase(Base.Type.random(), new Vector2(800, 200), false);
    }

    private void createCplBase(Base.Type type) {
        createBase(type, new Vector2(245, 125), true);
    }

    private void createBase(Base.Type t, Vector2 pos, boolean cpl) {
        Base b = new Base(pos.x, pos.y, t, cpl, activity);
        final IEntity layerBG = activity.getScene().getChildByIndex(AbstractGameActivity.LAYER_BACKGROUND);
        layerBG.attachChild(b.getPhosphate());
        layerBG.attachChild(b.getSprite());
        if (cpl) {
            baseCpls.add(b);
        } else {
            bases.add(b);
        }
    }

    private void createKeys() {
        createKey(Base.Type.A);
        createKey(Base.Type.T);
        createKey(Base.Type.G);
        createKey(Base.Type.C);
    }

    private void createKey(Base.Type type) {
        Key key = new Key(type, this);
        keyMap.put(type, key);
        final Scene scene = activity.getScene();
        final TouchableAnimatedSprite sprite = key.getSprite();
        final TouchableAnimatedSprite shape = key.getShape();

        final IEntity layerBG = scene.getChildByIndex(AbstractGameActivity.LAYER_FOREGROUND);
        layerBG.attachChild(key.getShadowInvalid());
        layerBG.attachChild(key.getShadowValid());
        layerBG.attachChild(sprite);
        layerBG.attachChild(shape);
        scene.registerTouchArea(shape);
    }

    private Vector2 shiftPos(float x, float y) {
        final float baseWidth = 27; // Maximum sprite width, once scaled
        final float margin = 10;
        return new Vector2(x - baseWidth - margin, y);
    }

    private void shiftBases() {
        for (Base base : bases) {
            shiftBase(base);
        }
        for (Base base : baseCpls) {
            shiftBase(base);
        }
    }

    private void shiftBase(Base base) {
        final Sprite s = base.getSprite();
        final Sprite phosphate = base.getPhosphate();
        final Vector2 newPos = shiftPos(s.getX(), s.getY());
        final Vector2 newPosPho = shiftPos(phosphate.getX(), phosphate.getY());

        Log.d(TAG, "shiftBase: shifted from " + s.getX() + ", " + s.getY() + " to " + newPos.x + ", " + newPos.y);
        s.setPosition(newPos.x, newPos.y);
        phosphate.setPosition(newPosPho.x, newPosPho.y);
    }

    private void resetGamePoints() {
        gameScore = INIT_SCORE;
        gameTime = INIT_TIME;
        setScore(gameScore);
        setTime(gameTime);
    }

    private void setScore(int score) {
        String padding = "";
        final int scoreLength = String.valueOf(score).length();
        if (scoreLength < 2) {
            padding += " ";
        }
        if (scoreLength < 3) {
            padding += " ";
        }
        setScore(padding + score);
    }

    private void setScore(CharSequence text) {
        HUDScore.getText().setText(text);
    }

    private void gameOver() {
        final float posRatioX = 0.5f;
        final float posRatioY = 0.2f;
        if (gameScore >= 50) {
            activity.onWin(gameScore, posRatioX, posRatioY);
        } else {
            activity.onLose(gameScore, posRatioX, posRatioY);
        }
    }

    private void incrementScore() {
        super.playSoundSuccess();
        setScore(++gameScore);
        Log.v(TAG, "beginContact - Increasing score to " + gameScore + ".");
    }

    private void decrementScore() {
        super.playSoundFailure();
        setScore(--gameScore);
        Log.v(TAG, "beginContact - Decreasing score to " + gameScore + ".");
    }

    private void decrementTime() {
        setTime(--gameTime);
        if (gameTime == 0) {
            gameOver();
        }
    }

    private void setTime(int time) {
        String padding = "";
        if (time < 10) {
            padding += " ";
            HUDTime.setUrgent(true);
        }
        if (time < 100) padding += " ";
        setTime(padding + time);
    }

    private void setTime(CharSequence text) {
        HUDTime.getText().setText(text);
    }

    @Override
    public void resetGame() {
        for (Base b : bases) {
            deleteBase(b);
        }
        for (Base b : baseCpls) {
            deleteBase(b);
        }

        bases.clear();
        baseCpls.clear();
        currentBaseIndex = 0;
        resetGamePoints();
        createADN();
    }

    private void deleteBase(Base b) {
        final TouchableAnimatedSprite sprite = b.getSprite();
        final Sprite phosphate = b.getPhosphate();
        sprite.setVisible(false);
        sprite.detachSelf();
        phosphate.setVisible(false);
        phosphate.detachSelf();
    }

    public void onKeyPress(final Base.Type type) {
        final Base.Type currentType = bases.get(currentBaseIndex).getCplType();
        final Key keyValid = keyMap.get(currentType);
        final Key keyInvalid = keyMap.get(type);
        final boolean isValid = currentType == type;

        animate(keyValid, new SequenceEntityModifier(new DelayModifier(DURATION_SHADER)), new IEntityModifier.IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                keyValid.showShadow(true, Key.Animation.VALID_HIT);
                if (!isValid) {
                    keyValid.showShadow(true, Key.Animation.VALID_HIT);
                    keyInvalid.showShadow(true, WorldObject.Animation.INVALID_HIT);
                }
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                keyValid.hideShadows();
                if (!isValid) {
                    keyInvalid.hideShadows();
                }
            }
        });

        if (isValid) {
            createBase();
            createCplBase(type);
            currentBaseIndex++;
            incrementScore();
        } else {
            decrementScore();
        }
    }

    @Override
    public boolean isPortrait() {
        return false;
    }
}
