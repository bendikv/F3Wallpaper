/**
 * F3 Wallpaper
 * @author bendikv
 */
package com.fivecubed.f3wall;

public class F3Cube {
	private float x;
	private float y;
	private float toX;
	private float toY;
	private float dX;
	private float dY;
	private int color;
	
	public F3Cube(float x, float y, int color) {
		this.x = x;
		this.y = y;
		this.toX = x;
		this.toY = y;
		this.color = color;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
		this.toX = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
		this.toY = y;
	}
	
	public float getToX() {
		return toX;
	}
	
	public void setToX(float toX) {
		this.toX = toX;
	}
	
	public float getToY() {
		return toY;
	}
	
	public void setToY(float toY) {
		this.toY = toY;
	}
	
	public float getdX() {
		return dX;
	}
	
	public void setdX(float dX) {
		this.dX = dX*Math.signum(this.toX-this.x);
	}
	
	public float getdY() {
		return dY;
	}
	
	public void setdY(float dY) {
		this.dY = dY*Math.signum(this.toY-this.y);
	}

	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public float incX() {
		if (Math.floor(Math.abs(this.toX-this.x))>0)
			this.x += this.dX;
		else
			this.x = this.toX;
		return x;
	}
	
	public float incY() {
		if (Math.floor(Math.abs(this.toY-this.y))>0)
			this.y += this.dY;
		else
			this.y = this.toY;
		return y;
	}
	
	@Override
	public String toString() {
		return "CUBE("+this.getX()+","+this.getY()+")";
	}
}
