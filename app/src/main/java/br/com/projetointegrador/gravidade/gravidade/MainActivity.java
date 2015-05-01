package br.com.projetointegrador.gravidade.gravidade;


import android.util.DisplayMetrics;

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
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends BaseGameActivity {

    private final Scene scene = new Scene();
    private Camera camera;
    private Sprite telaInicialSprite;
    private ITextureRegion mTelaInicialTextureRegion;

    private static int CAMERA_HEIGHT = 480;
    private static int CAMERA_WIDTH = 800;

    @Override
    public EngineOptions onCreateEngineOptions() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        if(CAMERA_HEIGHT < height && CAMERA_WIDTH < width){
            CAMERA_HEIGHT = height;
            CAMERA_WIDTH = width;
        }

        camera = new Camera(0,0,CAMERA_WIDTH,CAMERA_HEIGHT);

        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new FillResolutionPolicy(),camera);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getRenderOptions().setDithering(true);
        engineOptions.getRenderOptions().getConfigChooserOptions()
                .setRequestedMultiSampling(true);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);

        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        try{
            ITexture telaInicialTexture = new BitmapTexture(this.getTextureManager(),new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("inicial.png");
                }
            });

            ITexture telaJogoTexture = new BitmapTexture(this.getTextureManager(),new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("background.png");
                }
            });

            this.mTelaInicialTextureRegion = TextureRegionFactory.extractFromTexture(telaInicialTexture, 0, 0, 480,800);

            telaInicialTexture.load();
            telaJogoTexture.load();

        }catch(IOException e){
            Debug.e(e);
        }
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        telaInicialSprite = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2 ,this.mTelaInicialTextureRegion,
                this.getVertexBufferObjectManager());
       telaInicialSprite.setWidth(CAMERA_WIDTH);
       telaInicialSprite.setHeight(CAMERA_HEIGHT);
       scene.attachChild(telaInicialSprite);

    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {

    }
}
