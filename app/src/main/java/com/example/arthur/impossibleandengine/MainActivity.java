package com.example.arthur.impossibleandengine;


import android.util.DisplayMetrics;


import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends SimpleBaseGameActivity {

    private final Scene scene = new Scene();
    private Camera camera;
    private ITextureRegion mBackgroundTextureRegion, asteroideRegiao, naveRegiao;

    private Sprite[] asteroideSprite = new Sprite[128];
    private Sprite backgroundSprite;
    private Sprite naveSprite;
    private static int CAMERA_WIDTH = 480;
    private static int CAMERA_HEIGHT = 800;

    @Override
    public EngineOptions onCreateEngineOptions() {
        //int height = getWindowManager().getDefaultDisplay().getHeight();
        //int width = getWindowManager().getDefaultDisplay().getWidth();
        //captura tamanho da tela
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        if (CAMERA_HEIGHT < height && CAMERA_WIDTH < width) {
            CAMERA_HEIGHT = height;
            CAMERA_WIDTH = width;
        }

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        //engine options
        EngineOptions engineOptions = new EngineOptions(true,
                ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(),
                camera);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getRenderOptions().setDithering(true);
        engineOptions.getRenderOptions().getConfigChooserOptions()
                .setRequestedMultiSampling(true);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);

        return engineOptions;
    }

    @Override
    protected void onCreateResources() throws IOException {
        try {
            // 1 - Set bitmap textures
            ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("sky.png");
                }
            });

            ITexture asteroideTextura = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("asteroide.png");
                }
            });

            ITexture naveTextura = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("nave.png");
                }
            });

            this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0,480, 800);
            this.naveRegiao = TextureRegionFactory.extractFromTexture(naveTextura, 0, 0, 96, 96);

            backgroundTexture.load();
            naveTextura.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        backgroundSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.mBackgroundTextureRegion,
                this.getVertexBufferObjectManager());
        backgroundSprite.setWidth(CAMERA_WIDTH);
        backgroundSprite.setHeight(CAMERA_HEIGHT);
        scene.attachChild(backgroundSprite);

        ArrayList<Ponto> listaPontos = new ArrayList<>();
        for (int x = 50; x < CAMERA_WIDTH - 100; x = x + 75) {
            for (int y = 50; y < CAMERA_HEIGHT - 50; y = y + 75) {
                Ponto p = new Ponto();
                p.setX(x);
                p.setY(y);
                listaPontos.add(p);
            }
        }

        Ponto p = listaPontos.get(30);
        naveSprite = new Nave(p.getX(), p.getY(), this.naveRegiao, getVertexBufferObjectManager());
        backgroundSprite.attachChild(naveSprite);
        scene.registerTouchArea(naveSprite);

        return scene;
    }

    @Override
    public Engine onLoadEngine() {
        return null;
    }
}