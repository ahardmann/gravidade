package org.andengine.entity.scene.background;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.util.GLState;

/**
 * 
 * Modificado de AndEngine GLES 2.0 cuyo autor es Nicolas Gramlich.
 * 
 * @author Borja Anselmo Cano Parra
 * @since  22.11.2012
 */
public class VerticalParallaxBackground extends Background {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<ParallaxEntity> mParallaxEntities = new ArrayList<ParallaxEntity>();
	private int mParallaxEntityCount;

	protected float mParallaxValue;

	// ===========================================================
	// Constructors
	// ===========================================================

	public VerticalParallaxBackground(final float pRed, final float pGreen, final float pBlue) {
		super(pRed, pGreen, pBlue);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setParallaxValue(final float pParallaxValue) {
		this.mParallaxValue = pParallaxValue;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onDraw(final GLState pGLState, final Camera pCamera) {
		super.onDraw(pGLState, pCamera);

		final float parallaxValue = this.mParallaxValue;
		final ArrayList<ParallaxEntity> parallaxEntities = this.mParallaxEntities;

		for(int i = 0; i < this.mParallaxEntityCount; i++) {
			parallaxEntities.get(i).onDraw(pGLState, pCamera, parallaxValue);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void attachParallaxEntity(final ParallaxEntity pParallaxEntity) {
		this.mParallaxEntities.add(pParallaxEntity);
		this.mParallaxEntityCount++;
	}

	public boolean detachParallaxEntity(final ParallaxEntity pParallaxEntity) {
		this.mParallaxEntityCount--;
		final boolean success = this.mParallaxEntities.remove(pParallaxEntity);
		if(!success) {
			this.mParallaxEntityCount++;
		}
		return success;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class ParallaxEntity {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		final float mParallaxFactor;
		final IAreaShape mAreaShape;

		// ===========================================================
		// Constructors
		// ===========================================================

		public ParallaxEntity(final float pParallaxFactor, final IAreaShape pAreaShape) {
			this.mParallaxFactor = pParallaxFactor;
			this.mAreaShape = pAreaShape;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onDraw(final GLState pGLState, final Camera pCamera, final float pParallaxValue) {
			pGLState.pushModelViewGLMatrix();
			{
				final float cameraHeight = pCamera.getHeight();
				final float shapeHeightScaled = this.mAreaShape.getHeightScaled();
				float baseOffset = (pParallaxValue * this.mParallaxFactor) % shapeHeightScaled;

				while(baseOffset > 0) {
					baseOffset -= shapeHeightScaled;
				}
				pGLState.translateModelViewGLMatrixf(0,baseOffset, 0);

				float currentMaxY = baseOffset;

				do {
					this.mAreaShape.onDraw(pGLState, pCamera);
					pGLState.translateModelViewGLMatrixf(0,shapeHeightScaled, 0);
					currentMaxY += shapeHeightScaled;
				} while(currentMaxY < cameraHeight);
			}
			pGLState.popModelViewGLMatrix();
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
