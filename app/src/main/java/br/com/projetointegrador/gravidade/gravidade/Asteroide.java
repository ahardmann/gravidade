package br.com.projetointegrador.gravidade.gravidade;

import android.util.Log;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.Random;

public class Asteroide extends AnimatedSprite {
    private static Random random = new Random();
    private static float velocidadeMin = 50;

    private PhysicsHandler physicsHandler;

    private int pLarguraTela;
    private int pAlturaTela;
    private float velocidadeX;
    private float velocidadeY;
    private Sprite nave;
    private MainActivity activity;

    //Método construtor Asteroide
    public Asteroide(TiledTextureRegion pTiledTextureRegion, float pVelocidade, int largura, int altura
            , final VertexBufferObjectManager pVertexBufferObjectManager, Sprite pNave,MainActivity pActivity) {
        super(largura * random.nextFloat(), altura, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);

        this.physicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(this.physicsHandler);

        this.pAlturaTela  = altura;
        this.pLarguraTela = largura;

        this.velocidadeX = 0;
        this.velocidadeY = getVelocidadeY(pVelocidade);

        this.nave = pNave;
        this.activity = pActivity;

        this.physicsHandler.setVelocity(this.velocidadeX, this.velocidadeY);
        this.setPosition(getPosicaoInicial(), MainActivity.CAMERA_HEIGHT);
    }

    public float getPosicaoInicial(){
        return this.random.nextFloat() * MainActivity.CAMERA_WIDTH;
    }

    //Método que gera asteroides em pontos diferentes
    public float getVelocidadeY(float velocidade) {
        return -(this.random.nextFloat() * velocidade + velocidadeMin);
    }

    //Método de loop dos asteroids
    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        final  String LOGS = "logs";
        //mY = posição Y
        if(this.mY < 0) {
            this.setPosition(getPosicaoInicial(), MainActivity.CAMERA_HEIGHT);
            //att pontuação qdo asteroide chega no fim
            this.activity.atualizaPontuacao();
        }

        //verifica se ocorreu a colisão no metodo onManagedUpdate,
        // caso o asteroide bater na nave vamos pegar a Activity e parar a aplicação.
        if(this.collidesWith(this.nave)){
            //chama o game over atraves do metodo do main activity(não esquecer de criar o activity no AndroidManifest.xml)
            this.activity.gameOver();
            Log.i(LOGS, "Colisao");
        }
        super.onManagedUpdate(pSecondsElapsed);
    }
}