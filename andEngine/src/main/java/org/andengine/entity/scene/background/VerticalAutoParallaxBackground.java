package org.andengine.entity.scene.background;

/**
 * 
 * Modificado de AndEngine GLES 2.0 cuyo autor es Nicolas Gramlich.
 * 
 * @author Borja Anselmo Cano Parra
 * @since  22.11.2012
 */
public class VerticalAutoParallaxBackground extends VerticalParallaxBackground {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private float mParallaxChangePerSecond;

	// ===========================================================
	// Constructors
	// ===========================================================

	public VerticalAutoParallaxBackground(final float pRed, final float pGreen, final float pBlue, final float pParallaxChangePerSecond) {
		super(pRed, pGreen, pBlue);
		this.mParallaxChangePerSecond = pParallaxChangePerSecond;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setParallaxChangePerSecond(final float pParallaxChangePerSecond) {
		this.mParallaxChangePerSecond = pParallaxChangePerSecond;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);

		this.mParallaxValue += this.mParallaxChangePerSecond * pSecondsElapsed;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}