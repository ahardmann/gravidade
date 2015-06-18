package br.com.projetointegrador.gravidade.gravidade;

import android.content.Intent;
import android.util.Log;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import java.io.IOException;
import java.io.InputStream;

public class TelaInicialActivity extends SimpleBaseGameActivity {
    public static int CAMERA_WIDTH = 480;
    public static int CAMERA_HEIGHT = 800;
    public Text textsound;
    private Scene scene = new Scene();
    private Scene mSceneSobre = new Scene();
    private Camera camera;
    private Sprite inicialSprite, logoSprite, botaoSairSprite, botaoInativoSprite, botaoConfgSprite, botaoVoltarSprite,botaoSoundSprite;
    private ITextureRegion inicialTextureRegion;
    private BitmapTextureAtlas texBotaoInativo, texLogo, mFontTexture;
    private TiledTextureRegion botaoRegiaoInativo, logoRegiao;
    private float mDowX, mDowY;
    private Font mFont;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0 , 0,CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

        return engineOptions;
    }

    private void loadGraphics() throws IOException{
        ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
            @Override
            public InputStream open() throws IOException {
                return getAssets().open("gfx/newinicial.png");
            }
        });

        this.inicialTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0, 400, 486);

        //Logo Gravidade
        this.texLogo = new BitmapTextureAtlas(this.getTextureManager(),485,78, TextureOptions.DEFAULT);
        this.logoRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texLogo, this.getAssets(),"gfx/gravidade_marca.png",0,0,1,1);

        //Botao Inativo
        texBotaoInativo = new BitmapTextureAtlas(this.getTextureManager(),342,64, TextureOptions.DEFAULT);
        this.botaoRegiaoInativo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                texBotaoInativo, this.getAssets(), "gfx/botao_ativo.png", 0, 0, 1, 1
        );

        //FontFactory.setAssetBasePath("font/");
        this.mFontTexture = new BitmapTextureAtlas(mEngine.getTextureManager(), 256, 256);
        this.mFont = FontFactory.createFromAsset(this.getFontManager(), mFontTexture, this.getAssets(), "font/aspace.ttf", 20, true, Color.WHITE_ABGR_PACKED_INT);
        this.mFont.load();
        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.mEngine.getFontManager().loadFont(this.mFont);

        texBotaoInativo.load();
        texLogo.load();
        backgroundTexture.load();
    }

    @Override
    protected void onCreateResources() throws IOException {
        loadGraphics();
    }

    @Override
    protected Scene onCreateScene() {
        this.inicialSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.inicialTextureRegion, this.getVertexBufferObjectManager());
        inicialSprite.setWidth(CAMERA_WIDTH);
        inicialSprite.setHeight(CAMERA_HEIGHT);
        scene.attachChild(this.inicialSprite);

        this.logoSprite= new Sprite (this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 150 ,this.logoRegiao,this.getVertexBufferObjectManager());
        scene.attachChild(this.logoSprite);
        Text text = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 400 ,mFont,"Jogar",this.getVertexBufferObjectManager());
        text.setScale(2.f);
        this.botaoInativoSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 400,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){

            //Cria o touch dentro do botao
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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
        scene.attachChild(text);
        scene.registerTouchArea(this.botaoInativoSprite);

        Text textconfg = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 500 ,mFont,"Sobre",this.getVertexBufferObjectManager());
        this.botaoConfgSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 500,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){

            //Cria o touch dentro do botao
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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
                        createSobre();
                        mEngine.setScene(mSceneSobre);
                        break;
                }
                return true;
            }
        };
        scene.attachChild(this.botaoConfgSprite);
        scene.attachChild(textconfg);
        scene.registerTouchArea(this.botaoConfgSprite);

        textsound = new Text(CAMERA_WIDTH/2,CAMERA_HEIGHT - 600, mFont, "Musica | On", getVertexBufferObjectManager());
        this.botaoSoundSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 600,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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
                        if (!(textsound.getText().equals("Musica | On"))){
                            textsound.setText("Musica | On");
                        } else {
                            textsound.setText("Musica | Off");
                        }
                        break;
                }
                return true;
            }
        };
        scene.attachChild(this.botaoSoundSprite);
        scene.attachChild(textsound);
        scene.registerTouchArea(this.botaoSoundSprite);

        Text textsair = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 700 ,mFont,"Sair",this.getVertexBufferObjectManager());
        this.botaoSairSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 700,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){

            //Cria o touch dentro do botao
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                int eventAction = pSceneTouchEvent.getAction();
                float X = pSceneTouchEvent.getX();
                float Y = pSceneTouchEvent.getY();

                switch (eventAction) {
                    case TouchEvent.ACTION_DOWN:
                        mDowX = X;
                        mDowY = Y;
                        break;
                    case TouchEvent.ACTION_MOVE:
                        break;
                    case TouchEvent.ACTION_UP:
                        System.exit(0);
                        break;
                }
                return true;
            }
        };
        scene.attachChild(this.botaoSairSprite);
        scene.attachChild(textsair);
        scene.registerTouchArea(this.botaoSairSprite);

        return scene;
    }

    protected void createSobre() {
        mEngine.registerUpdateHandler(new FPSLogger());

        this.inicialSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.inicialTextureRegion, this.getVertexBufferObjectManager());
        inicialSprite.setWidth(CAMERA_WIDTH);
        inicialSprite.setHeight(CAMERA_HEIGHT);
        mSceneSobre.attachChild(this.inicialSprite);

        this.logoSprite= new Sprite (this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 100 ,this.logoRegiao,this.getVertexBufferObjectManager());
        mSceneSobre.attachChild(this.logoSprite);

        Text textCriado = new Text(CAMERA_WIDTH /2, CAMERA_HEIGHT - 200, mFont, "Desenvolvido por:\n\t Arthur Hardmann \n\t " +
               " https://github.com/ahardmann \n\t" + "Marcus Vinicius \n\t  https://github.com/mvfsilva" ,getVertexBufferObjectManager());
        mSceneSobre.attachChild(textCriado);

        Text textvoltar = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 600 ,mFont,"Voltar",this.getVertexBufferObjectManager());
        this.botaoVoltarSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 600,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){

            //Cria o touch dentro do botao
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                int eventAction = pSceneTouchEvent.getAction();
                float X = pSceneTouchEvent.getX();
                float Y = pSceneTouchEvent.getY();

                switch (eventAction) {
                    case TouchEvent.ACTION_DOWN:
                        mDowX = X;
                        mDowY = Y;
                        break;
                    case TouchEvent.ACTION_MOVE:
                        break;
                    case TouchEvent.ACTION_UP:
                        onCreateScene();
                        mEngine.setScene(scene);
                        break;
                }
                return true;
            }
        };
        mSceneSobre.attachChild(this.botaoVoltarSprite);
        mSceneSobre.attachChild(textvoltar);
        mSceneSobre.registerTouchArea(this.botaoVoltarSprite);
    }

    //Metodo pra iniciar o MainActivity
    private void startMainActivity(){
        Intent playGame = new Intent(this, MainActivity.class);
        startActivity(playGame);
        this.finish();
    }
}