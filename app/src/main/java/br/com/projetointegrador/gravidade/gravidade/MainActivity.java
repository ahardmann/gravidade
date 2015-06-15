package br.com.projetointegrador.gravidade.gravidade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
import org.andengine.extension.collisions.entity.sprite.PixelPerfectAnimatedSprite;
import org.andengine.extension.collisions.opengl.texture.region.PixelPerfectTextureRegionFactory;
import org.andengine.extension.collisions.opengl.texture.region.PixelPerfectTiledTextureRegion;
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
    private PixelPerfectAnimatedSprite naveSprite;
    private Sprite starSprite;

    private ITextureRegion mBackgroundTextureRegion;
    private PixelPerfectTiledTextureRegion naveRegiao;
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

    //Musica / Som
    private Music musica;
    private Sound colisaoSom;



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
        //instancia novo score, inicializa socoreService, e usa função open para utilizar o banco
        ScoreSqlite scoreSqlite = new ScoreSqlite(this);
        scoreService = new ScoreService(scoreSqlite);
        scoreService.open();

        //Captura Tamalho da tela
        camera = new Camera(0 , 0,CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);

        engineOptions.getAudioOptions().setNeedsMusic(true);
        engineOptions.getAudioOptions().setNeedsSound(true);

        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        return engineOptions;
    }

    //Carrega os sons do jogo
    private void loadSounds() {
        try {
            musica = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), this, "sfx/inicio_game.ogg");
            colisaoSom = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "sfx/colisao.ogg");
            //shotSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "sfx/shot.wav");
            //boomSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "sfx/explosion.wav");
            //killSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "sfx/smallBoom.wav");
            //hitSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "sfx/hitBoom.wav");
            //powerSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "sfx/bonus.wav");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGraphics() throws IOException {
        try {
            // 1 - Set bitmap textures
            ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("newbackground.png");
                }
            });

            this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0, 400, 900);

            //seta asteroide
            texAsteroide = new BitmapTextureAtlas(this.getTextureManager(), 36, 36, TextureOptions.DEFAULT);
            this.asteroideRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texAsteroide, this.getAssets(), "asteroide.png", 0, 0, 1, 1);

            //Seta constelação
            texStar = new BitmapTextureAtlas(this.getTextureManager(),272,160, TextureOptions.DEFAULT);
            this.starRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texStar, this.getAssets(),"parallax-space-stars.png",0,0,1,1);

            //seta nave
            texNave = new BitmapTextureAtlas(this.getTextureManager(), 64, 70, TextureOptions.DEFAULT);
            this.naveRegiao = PixelPerfectTextureRegionFactory.createTiledFromAsset(texNave, this.getAssets(), "nave.png", 0, 0, 1, 1, 0);

            pontuacaoTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.pontuacaoFont = new Font(this.getFontManager(),pontuacaoTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
            this.mEngine.getTextureManager().loadTexture(pontuacaoTextureAtlas);
            this.mEngine.getFontManager().loadFont(pontuacaoFont);

            //Carrega as texturas
            texStar.load();
            texAsteroide.load();
            texNave.load();
            backgroundTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }


        @Override
        protected void onCreateResources() throws IOException {
            loadGraphics();
            //loadFonts();
            loadSounds();

        }

    private void startMusic() {
        musica.play();
        musica.setLooping(true);

        mEngine.getSoundManager().setMasterVolume(1);
        mEngine.getMusicManager().setMasterVolume(1);
    }


    //Criação da cena do jogo
    @Override
    protected Scene onCreateScene() {
        //Start da música após o play
        startMusic();

        //criando o background
        this.backgroundSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager());
        backgroundSprite.setWidth(CAMERA_WIDTH);
        backgroundSprite.setHeight(CAMERA_HEIGHT);
        scene.attachChild(this.backgroundSprite);
        scene.registerTouchArea(this.backgroundSprite);


        //criação da nave, width/2 meio do eixo horizontal, heigth/4 posiçao no eixo vertical
        this.naveSprite = new Nave(CAMERA_WIDTH/2,CAMERA_HEIGHT/4,naveRegiao,this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager());
        scene.attachChild(this.naveSprite);

        //Laço que gera mais de um asteroide na tela
        for(int i = 0; i < 4 ; i++) {
            this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
            scene.attachChild(this.asteroideSprite);
        }

        for(int x = 0; x < 60; x++){
            this.starSprite = new Stars(this.starRegiao, 300f,this.CAMERA_HEIGHT ,this.CAMERA_WIDTH, this.getVertexBufferObjectManager());
            scene.attachChild(this.starSprite);
        }

        //Pontos
        recorde = scoreService.getRecorde();
        textoPontuacao = new Text(this.CAMERA_WIDTH - 100,this.CAMERA_HEIGHT - 50, this.pontuacaoFont, " RECORDE: " + recorde * 5 + "              " , this.getVertexBufferObjectManager());
        scene.attachChild(textoPontuacao);

        return scene;
    }

    //metodo game over(não esquecer de criar o activity no AndroiManifest.xml)
    public void gameOver() {
        colisaoSom.play();
        Bundle bundle = new Bundle();
        bundle.putLong("pontos", pontos);
        bundle.putLong("recorde", recorde);
        Intent intent = new Intent(this,GameOverActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    public void atualizaPontuacao(){
        final  String LOGS = "logs";
        this.pontos++;
        if(this.pontos > this.recorde){
            this.recorde = this.pontos;
            this.scoreService.novoRecorde(recorde);
        }
        this.textoPontuacao.setText(" " + pontos *5);
        //adiciona meteoros na tela de acordo com o score estabelecido
        //fiz assim pra podermos setar qts meteoros queremos, qdo queremos e a velocidade que queremos
        //(que preferi deixar a 150f padrão mesmo)
        if((this.pontos * 5) == 100){
            for(int i =0; i < 2; i++){
                this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                scene.attachChild(this.asteroideSprite);
                Log.i(LOGS, "Gerou");
            }
        }
        if((this.pontos * 5) == 300){
            for(int i =0; i < 3; i++){
                this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                scene.attachChild(this.asteroideSprite);
                Log.i(LOGS, "Gerou");
            }
        }
        if((this.pontos * 5) == 800){
            for(int i =0; i < 4; i++){
                this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                scene.attachChild(this.asteroideSprite);
                Log.i(LOGS, "Gerou");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (naveSprite != null){
                //Com Bug
              if(naveSprite.getX() > CAMERA_WIDTH - 460 && naveSprite.getX() /*+ naveSprite.getWidth() */< CAMERA_WIDTH -20){
                if(this.CAMERA_WIDTH > 0 || this.CAMERA_WIDTH < 480)
                    naveSprite.setX(naveSprite.getX() - (x * 4));
              }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}