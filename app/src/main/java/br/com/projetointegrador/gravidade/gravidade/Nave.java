package br.com.projetointegrador.gravidade.gravidade;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.extension.collisions.entity.sprite.PixelPerfectAnimatedSprite;
import org.andengine.extension.collisions.opengl.texture.region.PixelPerfectTiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Nave extends PixelPerfectAnimatedSprite {

    private PhysicsHandler mPhysicsHandler;
    private float oringemX;
    private float origemY;

    private int velocidade = 150;

    private int larguraTela;
    private int alturaTela;

    public Nave(float pX, float pY,PixelPerfectTiledTextureRegion pTiledTextureRegion, int plarguraTela, int palturaTela, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTiledTextureRegion,pVertexBufferObjectManager);

        mPhysicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(mPhysicsHandler);

        this.velocidade = 100;

        this.origemY  = pY;
        this.oringemX = pX;

        this.larguraTela = plarguraTela;
        this.alturaTela  = palturaTela;
    }

    public PhysicsHandler getPhysicsHandler(){
        return mPhysicsHandler;
    }
}