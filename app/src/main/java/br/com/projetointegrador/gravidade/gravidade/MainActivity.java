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
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.collisions.entity.sprite.PixelPerfectAnimatedSprite;
import org.andengine.extension.collisions.opengl.texture.region.PixelPerfectTextureRegionFactory;
import org.andengine.extension.collisions.opengl.texture.region.PixelPerfectTiledTextureRegion;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;

import java.io.IOException;

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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFonts(){

    }

    private void loadGraphics() throws IOException {
            // 1 - Set bitmap textures
           /* ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/newbackground.png");
                }
            });*/

            //this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0, 400, 900);



            //seta asteroide
            texAsteroide = new BitmapTextureAtlas(this.getTextureManager(), 36, 36, TextureOptions.DEFAULT);
            this.asteroideRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texAsteroide, this.getAssets(), "gfx/asteroide.png", 0, 0, 1, 1);

            //Seta constelação
            texStar = new BitmapTextureAtlas(this.getTextureManager(),272,160, TextureOptions.DEFAULT);
            this.starRegiao = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texStar, this.getAssets(),"gfx/parallax-space-stars.png",0,0,1,1);

            //seta nave
            texNave = new BitmapTextureAtlas(this.getTextureManager(), 64, 70, TextureOptions.DEFAULT);
            this.naveRegiao = PixelPerfectTextureRegionFactory.createTiledFromAsset(texNave, this.getAssets(), "gfx/nave.png", 0, 0, 1, 1, 0);

            pontuacaoTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.pontuacaoFont = new Font(this.getFontManager(),pontuacaoTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
            this.mEngine.getTextureManager().loadTexture(pontuacaoTextureAtlas);
            this.mEngine.getFontManager().loadFont(pontuacaoFont);

            //Carrega as texturas
            texStar.load();
            texAsteroide.load();
            texNave.load();
//            backgroundTexture.load();
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

    @Override
    protected Scene onCreateScene() {
        //Start da música após o play
        startMusic();

        //criando o background
        //this.backgroundSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager());
        //backgroundSprite.setWidth(CAMERA_WIDTH);
        //backgroundSprite.setHeight(CAMERA_HEIGHT);
        //scene.attachChild(this.backgroundSprite);
        //scene.registerTouchArea(this.backgroundSprite);

        scene.setBackground(new Background(0, 0, 0));

        //Criando nave, width/2 meio do eixo horizontal, heigth/4 posiçao no eixo vertical
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

        int dificuldade = (int) (this.pontos * 5);
        int i = 0;

        switch (dificuldade){
            case 50:
                for(i =0; i < 6; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 6");
                }
                break;
            case 200:
                for(i = 0; i < 8; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 8");
                }
                break;
            case 400:
                for(i = 0; i < 10; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 10");
                }
                break;
            case 800:
                for(i = 0; i < 12; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 12");
                }
                break;
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