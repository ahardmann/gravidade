package br.com.projetointegrador.gravidade.gravidade;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.MotionEvent;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Nave extends AnimatedSprite {

    private PhysicsHandler mPhysicsHandler;
    private float oringemX, origemY;
    private int velocidade = 100;
    private int larguraTela;
    private int alturaTela;

    private float anteriorX, anteriorY;
    public int direcao;
    public static final int ESQUERDA = 0;
    public static final int DIREITA  = 1;

    public int getDirecao(){
        return direcao;
    }

    public void setDirecao(int direcao){
        this.direcao = direcao;
    }

    public Nave(float pX, float pY, TiledTextureRegion pTiledTextureRegion,int plarguraTela,int palturaTela, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);

        mPhysicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(mPhysicsHandler);

        this.velocidade = 100;
        this.origemY  = pY;
        this.oringemX = pX;
        this.larguraTela = plarguraTela;
        this.alturaTela = palturaTela;
    }

    public PhysicsHandler getPhysicsHandler(){
        return mPhysicsHandler;
    }



    //metodo fez a nave navegar pela tela
    /*@Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        if (this.mX < 0) {
            this.mPhysicsHandler.setVelocityX(this.velocidade);
        } else if (this.mX + this.getHeight() > this.larguraTela) {
            this.mPhysicsHandler.setVelocityX(-this.velocidade);
        }
        if (this.mY < 0) {
            this.mPhysicsHandler.setVelocityY(this.velocidade);
        } else if (this.mY + this.getHeight() > this.alturaTela) {
            this.mPhysicsHandler.setVelocityY(-this.velocidade);
        }
        super.onManagedUpdate(pSecondsElapsed);
    }*/

    /*@Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }*/
}