package br.com.projetointegrador.gravidade.gravidade;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.Random;

/**
 * Created by arthur on 25/05/2015.
 */
public class Stars extends AnimatedSprite {
    private static Random random = new Random();
    private static float velocidadeMin = 50;

    private PhysicsHandler physicsHandler;

    private int pLarguraTela;
    private int pAlturaTela;
    private float velocidadeX;
    private float velocidadeY;

    public Stars(TiledTextureRegion pTiledTextureRegion, float pVelocidade, int largura, int altura
            , final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(largura * random.nextFloat(), altura, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);

        this.physicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(this.physicsHandler);

        this.pAlturaTela  = altura;
        this.pLarguraTela = largura;

        this.velocidadeX = 0;
        this.velocidadeY = getVelocidadeY(pVelocidade);

        this.physicsHandler.setVelocity(this.velocidadeX, this.velocidadeY);
        this.setPosition(getPosicaoInicial(), MainActivity.CAMERA_HEIGHT);
    }

    public float getPosicaoInicial(){
        return this.random.nextFloat() * MainActivity.CAMERA_WIDTH;
        //return MainActivity.CAMERA_WIDTH /2 * MainActivity.CAMERA_WIDTH;
    }

    public float getVelocidadeY(float velocidade) {
        return -(this.random.nextFloat() * velocidade + velocidadeMin);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        final  String LOGS = "logs";
        //mY = posição Y
        if(this.mY < 0) {
            this.setPosition(getPosicaoInicial(), MainActivity.CAMERA_HEIGHT);
        }
        super.onManagedUpdate(pSecondsElapsed);
    }
}
