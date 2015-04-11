package com.example.arthur.impossibleandengine;
import android.util.Log;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Nave extends Sprite implements IOnAreaTouchListener{

    private PhysicsHandler mPhysicsHandler;
    private float oringemX, origemY;
    private float anteriorX, anteriorY;

    public int getDirecao(){
        return direcao;
    }

    public void setDirecao(int direcao){
        this.direcao = direcao;
    }

    public int direcao;
    public static final int ESQUERDA =0;
    public static final int DIREITA=1;

    public Nave(float pX, float pY, ITextureRegion pTextureRegion,
                VertexBufferObjectManager pVertexBufferObjectManager ){
        super(pX,pY, pTextureRegion,pVertexBufferObjectManager);

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        origemY = pY;
        origemY = pX;
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {

        if(pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionOutside()) {
            if (pSceneTouchEvent.isActionUp()) {
                Log.i("isActionUp", "X=" + this.getX());
                Log.i("isActionUp", "Y=" + this.getY());
            }
            this.setX(pSceneTouchEvent.getX());
            this.setY(pSceneTouchEvent.getY());
        }
        if(pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionOutside()){
            this.setX(pSceneTouchEvent.getX());
            this.setY(pSceneTouchEvent.getY());
        }else if(pSceneTouchEvent.isActionUp()){
            float y=0;
            float x=0;

            x = this.anteriorX- (oringemX - getX());
            y = this.anteriorY- (origemY - getY());

            if(20 < x){
                this.direcao=DIREITA;
            }else if(20 > x){
                this.direcao=ESQUERDA;
            }
            this.anteriorX = oringemX - getX();
            this.anteriorY = origemY - getY();
        }

            return true;
    }
}
