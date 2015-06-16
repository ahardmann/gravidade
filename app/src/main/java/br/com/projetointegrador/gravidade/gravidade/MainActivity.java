package br.com.projetointegrador.gravidade.gravidade;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

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
    private PixelPerfectAnimatedSprite naveSprite;
    private Sprite starSprite;

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

    private int width;
    private int height;

    //Necessário para utilização do sensor
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        mSensorGerenciador = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcelerometro = mSensorGerenciador.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Point size = new Point();
        WindowManager w = getWindowManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            width = size.x;
            height = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            width = d.getWidth();
            height = d.getHeight();
        }

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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public EngineOptions onCreateEngineOptions() {
        //Instancia novo score, inicializa socoreService, e usa função open para utilizar o banco
        ScoreSqlite scoreSqlite = new ScoreSqlite(this);
        scoreService = new ScoreService(scoreSqlite);
        scoreService.open();

        Point size = new Point();
        WindowManager w = getWindowManager();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            width = size.x;
            height = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            width = d.getWidth();
            height = d.getHeight();
        }

        //Captura Tamalho da tela
        camera = new Camera(0 , 0,width, height);

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
    }

    @Override
    protected void onCreateResources() throws IOException {
        loadGraphics();
        //loadFonts();
        loadSounds();
    }

    private void pauseMusic(){
        musica.pause();
        musica.setLooping(false);
        musica.setVolume(0);
        musica.setVolume(0);
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
        scene.setBackground(new Background(0, 0, 0));

        //Criando nave, width/2 meio do eixo horizontal, heigth/4 posiçao no eixo vertical
        this.naveSprite = new Nave(width/2,height/8,naveRegiao,this.width, this.height, this.getVertexBufferObjectManager());
        //this.naveSprite = new Nave(CAMERA_WIDTH/2,CAMERA_HEIGHT/8,naveRegiao,this.CAMERA_HEIGHT, this.CAMERA_WIDTH, this.getVertexBufferObjectManager());
        scene.attachChild(this.naveSprite);

        //Laço que gera mais de um asteroide na tela
        for(int i = 0; i < 5 ; i++) {
            //this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f,CAMERA_WIDTH, CAMERA_HEIGHT, this.getVertexBufferObjectManager(),this.naveSprite,this);
            this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f,width, height, this.getVertexBufferObjectManager(),this.naveSprite,this);
            scene.attachChild(this.asteroideSprite);
        }

        for(int x = 0; x < 60; x++){
            this.starSprite = new Stars(this.starRegiao, 300f,width ,height, this.getVertexBufferObjectManager());
            scene.attachChild(this.starSprite);
        }
        //Pontos
        recorde = scoreService.getRecorde();
        textoPontuacao = new Text(this.width - 100,this.height - 50, this.pontuacaoFont, " RECORDE: " + recorde * 5 + "              " , this.getVertexBufferObjectManager());
        scene.attachChild(textoPontuacao);

        return scene;
    }

    //Método game over(não esquecer de criar o activity no AndroiManifest.xml)
    public void gameOver() {
        colisaoSom.play();
        Bundle bundle = new Bundle();
        bundle.putLong("pontos", Long.valueOf(pontos));
        bundle.putLong("recorde", Long.valueOf(recorde));
        Intent intent = new Intent(this,GameOverActivity.class);
        intent.putExtras(bundle);
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
        int i;

        switch (dificuldade){
            case 50:
                for(i = 0; i < 2; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, width, height, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 2");
                }
                break;

            case 200:
                for(i = 0; i < 4; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, width, height, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 4");
                }
                break;

            case 400:
                for(i = 0; i < 6; i++) {
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f,width, height, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 6");
                }
                break;

            case 800:
                for(i = 0; i < 8; i++){
                    this.asteroideSprite = new Asteroide(this.asteroideRegiao, 150f, width, height, this.getVertexBufferObjectManager(),this.naveSprite,this);
                    scene.attachChild(this.asteroideSprite);
                    Log.i(LOGS, "Gerou 8");
                }
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];

            if (naveSprite != null){

                float nextX = naveSprite.getX() - (x * 2);
                float halfnave = naveSprite.getWidth()/2;

                if( nextX>halfnave && nextX < width -halfnave  ){
                    naveSprite.setX(nextX);
                    Log.i("getX:", String.valueOf(naveSprite.getX()));
                }

               /* if(naveSprite.getX() < 0) {
                    naveSprite.setX(0);
                }else if(naveSprite.getX() + naveSprite.getWidth() > CAMERA_WIDTH) {
                    naveSprite.setX((CAMERA_WIDTH - 5) - naveSprite.getWidth());
                }else{
                    naveSprite.setX(nextX);
                    Log.i("getX:", String.valueOf(naveSprite.getX()));
                }*/
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}