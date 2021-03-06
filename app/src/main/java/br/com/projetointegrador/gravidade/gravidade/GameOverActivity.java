package br.com.projetointegrador.gravidade.gravidade;

import android.content.Intent;
import android.os.Bundle;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import org.andengine.entity.text.Text;
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

public class GameOverActivity extends SimpleBaseGameActivity{
        public static int CAMERA_WIDTH  = 480;
        public static int CAMERA_HEIGHT = 800;

        private Scene scene = new Scene();
        private Camera camera;

        private Sprite GameOverSprite;
        private ITextureRegion GameOverTextureRegion;

        private Long pontos = 0L;
        private Long recorde = 0L;
        private BitmapTextureAtlas pontuacaoTextureAtlas;
        private Text textoPontuacao;

        private Sprite botaoInativoSprite;
        private Sprite botaoSairSprite;
        private Sprite botaoMenuSprite;

        private BitmapTextureAtlas texBotaoInativo;
        private BitmapTextureAtlas mFontTexture;
        private TiledTextureRegion botaoRegiaoInativo;
        private float mDowX;
        private float mDowY;
        private Font mFont;

        @Override
        protected void onCreate(Bundle pSavedInstanceState) {
            if (this.getIntent() != null && this.getIntent().getExtras() != null) {
                Bundle bundle = this.getIntent().getExtras();
                pontos  = bundle.getLong("pontos");
                recorde = bundle.getLong("recorde");
            }
            super.onCreate(pSavedInstanceState);
        }

        @Override
        public EngineOptions onCreateEngineOptions() {
            camera = new Camera(0 , 0,CAMERA_WIDTH, CAMERA_HEIGHT);
            EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
            engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
            return engineOptions;
        }

        private void loadGraphics() throws IOException {
            ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/newgameover.png");
                }
            });

            this.mFontTexture = new BitmapTextureAtlas(mEngine.getTextureManager(), 256, 256);
            this.mFont = FontFactory.createFromAsset(this.getFontManager(), mFontTexture, this.getAssets(), "font/aspace.ttf", 28, true, Color.WHITE_ABGR_PACKED_INT);
            this.mFont.load();
            this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
            this.mEngine.getFontManager().loadFont(this.mFont);

            this.GameOverTextureRegion= TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0, 400, 494);

            texBotaoInativo = new BitmapTextureAtlas(this.getTextureManager(),342,64, TextureOptions.DEFAULT);
            this.botaoRegiaoInativo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texBotaoInativo, this.getAssets(), "gfx/botao_ativo.png", 0, 0, 1, 1);

            pontuacaoTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.mEngine.getTextureManager().loadTexture(pontuacaoTextureAtlas);

            texBotaoInativo.load();
            backgroundTexture.load();
        }

        @Override
        protected void onCreateResources() throws IOException {
            loadGraphics();
        }

        @Override
        protected Scene onCreateScene() {
            this.GameOverSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.GameOverTextureRegion, this.getVertexBufferObjectManager());
            GameOverSprite.setWidth(CAMERA_WIDTH);
            GameOverSprite.setHeight(CAMERA_HEIGHT);
            scene.attachChild(this.GameOverSprite);

            Text text = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 400 ,mFont,"Jogar Novamente",this.getVertexBufferObjectManager());

            this.botaoInativoSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 400, this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){
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
                            restartMainActivity();
                            break;
                    }
                    return true;
                }
            };
            scene.attachChild(this.botaoInativoSprite);
            scene.attachChild(text);
            scene.registerTouchArea(this.botaoInativoSprite);

            Text textmenu = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 500, mFont, "Menu", this.getVertexBufferObjectManager());
            this.botaoMenuSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 500,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){

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
                            Menu();
                            break;
                    }
                    return true;
                }
            };

            scene.attachChild(this.botaoMenuSprite);
            scene.attachChild(textmenu);
            scene.registerTouchArea(this.botaoMenuSprite);

            Text textsair = new Text(this.CAMERA_WIDTH/2, this.CAMERA_HEIGHT - 600 ,mFont,"Sair",this.getVertexBufferObjectManager());
            this.botaoSairSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 600,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){

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

            // Pontos
            textoPontuacao = new Text(this.CAMERA_WIDTH /2 ,this.CAMERA_HEIGHT - 100, mFont,"Pontuação: " + pontos*5 + "\nRecorde: " + recorde *5, this.getVertexBufferObjectManager());
            scene.attachChild(textoPontuacao);

            return scene;
        }

    private void restartMainActivity(){
        Intent playGame = new Intent(this, MainActivity.class);
        startActivity(playGame);
        this.finish();
    }

    private void Menu(){
        Intent menu = new Intent(this, TelaInicialActivity.class);
        startActivity(menu);
        this.finish();
    }
 }
