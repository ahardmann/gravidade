package br.com.projetointegrador.gravidade.gravidade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
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
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

import DB.ScoreService;
import DB.ScoreSqlite;

public class MainActivity extends SimpleBaseGameActivity implements SensorEventListener {
    //Engine
    public static int CAMERA_WIDTH  = 480;
    public static int CAMERA_HEIGHT = 800;
    private Scene scene = new Scene();
    private Camera camera;

    //Imagens
    private Sprite asteroideSprite;
    private Sprite backgroundSprite;
    private Sprite naveSprite;
    private Sprite starSprite;

    private ITextureRegion mBackgroundTextureRegion;
    private TiledTextureRegion naveRegiao;
    private TiledTextureRegion asteroideRegiao;
    private TiledTextureRegion starRegiao;
    private BitmapTextureAtlas texNave;
    private BitmapTextureAtlas texAsteroide;
    private BitmapTextureAtlas texStar;

    //Potuação
    private Long recorde;
    private ScoreService scoreService;
    private Long pontos = 0L;
    private BitmapTextureAtlas pontuacaoTextureAtlas;
    private Font pontuacaoFont;
    private Text textoPontuacao;

    //Acelerometro
    private SensorManager mSensorGerenciador;
    private Sensor mAcelerometro;

    //Parallax
    private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
    private ITextureRegion mParallaxStars;


    //Necessário para utilização do sensor
    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        mSensorGerenciador = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcelerometro = mSensorGerenciador.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //Método necessário para utilização do sensor
    @Override
    protected void onResume() {
        super.onResume();
        mSensorGerenciador.registerListener(this, mAcelerometro, SensorManager.SENSOR_DELAY_GAME);
    }

    //Método necessário para utilização do sensor
    @Override
    protected void onPause() {
        super.onPause();
        mSensorGerenciador.unregisterListener(this);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        //instancia novo score, inicializa socoreService, e usa função open
        //para utilizar o banco
        ScoreSqlite scoreSqlite = new ScoreSqlite(this);
        scoreService = new ScoreService(scoreSqlite);
        scoreService.open();

        //Captura tamanho da tela
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width  = metrics.widthPixels;

        if (CAMERA_HEIGHT < height && CAMERA_WIDTH < width) {
            CAMERA_HEIGHT = height;
            CAMERA_WIDTH = width;
        }

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        //Engine options
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getRenderOptions().setDithering(true);
        engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);

        return engineOptions;
    }

    //seta as imagens do jogo
    @Override
    protected void onCreateResources() throws IOException {
        try {
            // 1 - Set bitmap textures
            ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("newbackground.png");
                }
            });
            this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture,0,0,400,900);



            //Parallax
           /* BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
            this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(),800, 480, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.mParallaxStars = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax-space-stars.png", 0,0);
            this.mAutoParallaxBackgroundTexture.load();*/

            //seta asteroide
            texAsteroide = new BitmapTextureAtlas(this.getTextureManager(), 36, 36, TextureOptions.DEFAULT);
            this.asteroideRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    texAsteroide, this.getAssets(), "asteroide.png", 0, 0, 1, 1
            );
            //Seta constelação
            texStar = new BitmapTextureAtlas(this.getTextureManager(),272,160, TextureOptions.DEFAULT);
            this.starRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    texStar, this.getAssets(),"parallax-space-stars.png",0,0,1,1);

            //seta nave
            texNave = new BitmapTextureAtlas(this.getTextureManager(), 64, 70, TextureOptions.DEFAULT);
            this.naveRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    texNave, this.getAssets(), "nave.png", 0, 0, 1, 1
            );

            pontuacaoTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.pontuacaoFont = new Font(this.getFontManager(),pontuacaoTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
            this.mEngine.getTextureManager().loadTexture(pontuacaoTextureAtlas);
            this.mEngine.getFontManager().loadFont(pontuacaoFont);

            //load nas texturas
            texStar.load();
            texAsteroide.load();
            texNave.load();
            backgroundTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    //Criação da cena do jogo
    @Override
    protected Scene onCreateScene() {
        //criação da nave, width/2 meio do eixo horizontal, heigth/4 posiçao no eixo vertical
        this.naveSprite = new Nave(CAMERA_WIDTH/2,CAMERA_HEIGHT/4,naveRegiao,this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager());

       //criando o background
        this.backgroundSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager());

        backgroundSprite.setWidth(CAMERA_WIDTH);
        backgroundSprite.setHeight(CAMERA_HEIGHT);
        scene.attachChild(this.backgroundSprite);
        scene.registerTouchArea(this.backgroundSprite);

       /* this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        final VerticalAutoParallaxBackground autoParallaxBackground = new VerticalAutoParallaxBackground(0,0,0,20);
        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
        autoParallaxBackground.attachParallaxEntity(new VerticalParallaxBackground.ParallaxEntity(5.0f, new Sprite(0, 0, this.mParallaxStars, vertexBufferObjectManager)));
        scene.setBackground(autoParallaxBackground);*/

        //cria a nave antes do background,
        scene.attachChild(this.naveSprite);
        scene.registerTouchArea(this.naveSprite);

        //Laço que gera mais de um asteroide na tela
        for(int i = 0; i < 15 ; i++) {
            this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
            scene.attachChild(this.asteroideSprite);
        }

       /* for(int x = 0; x < 20; x++){
            this.starSprite = new Stars(this.starRegiao, 200f,this.CAMERA_HEIGHT ,this.CAMERA_WIDTH, this.getVertexBufferObjectManager());
            scene.attachChild(this.starSprite);
        }*/

        //Pontos
        recorde = scoreService.getRecorde();
        textoPontuacao = new Text(this.CAMERA_WIDTH - 100,this.CAMERA_HEIGHT - 50, this.pontuacaoFont,/*"PONTUAÇÃO: " + pontos + */" RECORDE: "+
                recorde * 5 + "              " , this.getVertexBufferObjectManager());
        scene.attachChild(textoPontuacao);

        return scene;
    }

    //metodo game over(não esquecer de criar o activity no AndroiManifest.xml)
    public void gameOver() {
        Bundle bundle = new Bundle();
        bundle.putLong("pontos", pontos);
        bundle.putLong("recorde", recorde);
        Intent intent = new Intent(this,GameOverActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    public void atualizaPontuacao(){
        this.pontos++;
        if(this.pontos > this.recorde){
            this.recorde = this.pontos;
            this.scoreService.novoRecorde(recorde);
        }
        this.textoPontuacao.setText(" " + pontos *5); //+ " RECORDE: "+ recorde);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (naveSprite != null){
                //Com Bug
               // if(naveSprite.getX() > 0 && naveSprite.getX() + naveSprite.getWidth() < CAMERA_WIDTH ){
                    naveSprite.setX(naveSprite.getX() - (x * 4));
               // }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}