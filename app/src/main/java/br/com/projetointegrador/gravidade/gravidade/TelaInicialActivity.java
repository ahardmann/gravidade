package br.com.projetointegrador.gravidade.gravidade;

import android.content.Intent;
import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

public class TelaInicialActivity extends SimpleBaseGameActivity {
    public static int CAMERA_WIDTH = 480;
    public static int CAMERA_HEIGHT = 800;
    private Scene scene = new Scene();
    private Camera camera;
    private Sprite inicialSprite, logoSprite, botaoAtivoSprite, botaoInativoSprite;
    private ITextureRegion inicialTextureRegion;
    private BitmapTextureAtlas texBotaoInativo, texBotaoAtivo, texLogo;
    private TiledTextureRegion botaoRegiaoAtivo, botaoRegiaoInativo, logoRegiao;
    private float mDowX, mDowY;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0 , 0,CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

        return engineOptions;
    }

    @Override
    protected void onCreateResources() throws IOException {
        try {
            ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("newinicial.png");
                }
            });

            this.inicialTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0, 400, 486);

            //Logo gravidade
            this.texLogo = new BitmapTextureAtlas(this.getTextureManager(),485,78, TextureOptions.DEFAULT);
            this.logoRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    texLogo, this.getAssets(),"gravidade_marca.png",0,0,1,1);

            //Botao ativo
            texBotaoInativo = new BitmapTextureAtlas(this.getTextureManager(),342,64, TextureOptions.DEFAULT);
            this.botaoRegiaoInativo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    texBotaoInativo, this.getAssets(), "botao_inativo.png", 0, 0, 1, 1
            );

            //Botao inativo
            texBotaoAtivo = new BitmapTextureAtlas(this.getTextureManager(),342,64, TextureOptions.DEFAULT);
            this.botaoRegiaoAtivo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    texBotaoAtivo, this.getAssets(), "botao_ativo.png", 0, 0, 1, 1
            );

            texBotaoAtivo.load();
            texBotaoInativo.load();
            texLogo.load();
            backgroundTexture.load();
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        this.inicialSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.inicialTextureRegion, this.getVertexBufferObjectManager());
        inicialSprite.setWidth(CAMERA_WIDTH);
        inicialSprite.setHeight(CAMERA_HEIGHT);
        scene.attachChild(this.inicialSprite);

        this.logoSprite= new Sprite (this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 100 ,this.logoRegiao,this.getVertexBufferObjectManager());
        scene.attachChild(this.logoSprite);

        this.botaoInativoSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 300,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){
            //Cria o touch dentro do botao
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                                         float pTouchAreaLocalX, float pTouchAreaLocalY) {
                int eventAction = pSceneTouchEvent.getAction();
                float X = pSceneTouchEvent.getX();
                float Y = pSceneTouchEvent.getY();

                switch (eventAction) {
                    case TouchEvent.ACTION_DOWN:
                        Log.e("Fudeu",X+""+Y);
                        mDowX = X;
                        mDowY = Y;
                        break;

                    case TouchEvent.ACTION_MOVE:
                        break;

                    case TouchEvent.ACTION_UP:
                        Log.e("Fudeu","UP "+X+""+Y);
                        startMainActivity();
                        break;
                }
                return true;
            }
        };
        scene.attachChild(this.botaoInativoSprite);
        scene.registerTouchArea(this.botaoInativoSprite);

        return scene;
    }

    //Metodo pra iniciar o MainActivity
    private void startMainActivity(){
        Intent playGame = new Intent(this, MainActivity.class);
        startActivity(playGame);
        this.finish();
    }
}