/**
 * F3 Wallpaper
 * @author bendikv
 */
package com.fivecubed.f3wall;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class F3WallService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new F3WallpaperEngine();
	}
	
	private class F3WallpaperEngine extends Engine implements OnSharedPreferenceChangeListener {
		private static final String PARTICLE_SQUARE	= "SQUARE";
		private static final String PARTICLE_CIRCLE = "CIRCLE";
		
		private final Handler handler = new Handler();
		
		// Draw logo
		private final Runnable drawRunner = new Runnable(){
			@Override
			public void run() {
				if (color!=0) {
					fillByColor(cubes, color);
					draw();
				} else {
					//randomizeColors(cubes);
					fillByColor(cubes, colors[(int)(Math.random()*6)]);
					draw();
				}
			}
		};
		
		// Animation of scattering particles
		private final Runnable animationOutRunner = new Runnable(){
			@Override
			public void run() {
				randomizeCubes(cubes);
				
				animationInProgress = true;
				animationOut = true;
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
		    	startTime = System.currentTimeMillis();
		    	long time = System.currentTimeMillis();
				while (animationOut) {
					
					if (System.currentTimeMillis()-time >= ((1000.0f*animationOutTime)/((float)framesPerSecond*animationOutTime)))
					{
						try {
							canvas = holder.lockCanvas();
							if (canvas != null) {
								canvas.drawColor(Color.DKGRAY);
								//fillCubes(cubes);
								drawCubes(canvas, cubes, oldX, oldY);
							}
							
						} finally {
							if (canvas!=null) {
								holder.unlockCanvasAndPost(canvas);
							}
						}
						time = 0;
					}
					
					if (System.currentTimeMillis()-startTime >= (1000.0f*animationOutTime))
						animationOut = false;
				}
			}
		};
		
		// Animation of returning particles to destination 
		private final Runnable animationInRunner = new Runnable(){
			@Override
			public void run() {
				animationOut = false;
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				fillCubesCoords(cubes);
				
				while (animationInProgress) {
					try {
						canvas = holder.lockCanvas();
						if (canvas != null) {
							canvas.drawColor(Color.DKGRAY);
							//fillCubes(cubes);
							drawCubes(canvas, cubes, x, y);
						}
						
					} finally {
						if (canvas!=null) {
							holder.unlockCanvasAndPost(canvas);
						}
					}
					animationInProgress = false;
					for(F3Cube cube : cubes) {
						if (cube.getX()!=cube.getToX() || cube.getY()!=cube.getToY())
							animationInProgress = true;
					}
					
				}
				
				// Reset timer
				handler.removeCallbacks(drawRunner);
				if (visible) {
					handler.postDelayed(drawRunner, delay*1000);
				}
			}
		};
		
		// Particles
		private List<F3Cube> cubes;
		// Current coordinates of logo
		private float x;
		private float y;
		// Previous coordinates of logo
		private float oldX;
		private float oldY;
		// Width of canvas
		private int width;
		// Height of canvas
		private int height;
		// Wallpaper visibility
		private boolean visible = true;
		// Time of animation start
		private long startTime;
		// State of animation
		private boolean animationInProgress;
		// State of scattering particles animation
		private boolean animationOut;
		// Color
		private int color;
		
		// Wallpaper preferences
		private SharedPreferences prefs;
		// Delay before moving logo
		private int delay;
		// State of touch preference
		private boolean touchEnabled;
		// Kind of particles
		private String kindOfParticles;
		// Color of particles
		private String colorOfParticles;

		// Duration of scattering particles animation (ms)
		private float animationOutTime = 0.5f;
		// Distance of scattering
		private float distance = 15.0f;
		// Frames per second
		private int framesPerSecond = 20;
		
		// x coords of particles of the logo
		private float[] figureX;
		// y coords of particles of the logo
		private float[] figureY;
		
		// Table of colors for the logo
		private int colors[] = {Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.LTGRAY, Color.YELLOW, Color.WHITE};
		
		// Device desinty
		private float density;
		
		// Offset of wallpaper
		private float xOffset=0;
		private float yOffset=0;
		
		public F3WallpaperEngine() {
			prefs = PreferenceManager.getDefaultSharedPreferences(F3WallService.this);
   		    prefs.registerOnSharedPreferenceChangeListener(this); 			
			
			DisplayMetrics dm = getBaseContext().getResources().getDisplayMetrics();
			density = dm.density;

			delay = Integer.parseInt(prefs.getString("delay", "0"));
			touchEnabled = prefs.getBoolean("touch", false);
			kindOfParticles = prefs.getString("kind", F3WallpaperEngine.PARTICLE_SQUARE);
			colorOfParticles = prefs.getString("color", "RANDOM");
			
			color = getColorFromPreference(colorOfParticles);
			
			setTouchEventsEnabled(touchEnabled); 
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				doOffsetNotificationsEnabled();
			cubes = new ArrayList<F3Cube>();
			
			int size = prefs.getInt("cubeSize", 0);
			if (size > 0) {
				if (size > 0) {
					figureX = new float[size];
					figureY = new float[size];
					for (int i=0;i<size;i++) {
						figureX[i] = prefs.getFloat("cubeX"+i, 0);
						figureY[i] = prefs.getFloat("cubeY"+i, 0);
					}
				}
			}
			else {
				figureX = new float[]{0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 4.0f, 5.0f, 6.0f, 7.0f, 3.0f, 4.0f, 5.0f, 6.0f, 8.0f, 3.0f, 4.0f, 7.0f, 8.0f, 3.0f, 4.0f, 7.0f, 8.0f, 3.0f, 4.0f, 7.0f, 8.0f, 3.0f, 5.0f, 6.0f, 7.0f, 8.0f, 4.0f, 5.0f, 6.0f, 7.0f, 10.0f, 11.0f, 10.0f, 11.0f, 10.0f, 11.0f, 10.0f, 11.0f, 10.0f, 11.0f, 10.0f, 11.0f, 10.0f, 11.0f, 13.0f};
				figureY = new float[]{0.0f, 0.0f, 1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f, 4.0f, 4.0f, 5.0f, 5.0f, 6.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 2.0f, 2.0f, 2.0f, 2.0f, 3.0f, 3.0f, 3.0f, 3.0f, 4.0f, 4.0f, 4.0f, 4.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 6.0f, 6.0f, 6.0f, 6.0f, 0.0f, 0.0f, 1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f, 4.0f, 4.0f, 5.0f, 5.0f, 6.0f, 6.0f, 0.0f};
			}
				makeCubes(cubes);
			handler.post(drawRunner);
		}
		
		private int getColorFromPreference(String clr) {
			Log.v("Color", clr);
			if (clr.equals("RANDOM")) 
				color = 0;
			else if (clr.equals("WHITE"))
				color = Color.WHITE;
			else if (clr.equals("LTGRAY"))
				color = Color.LTGRAY;
			else if (clr.equals("YELLOW"))
				color = Color.YELLOW;
			else if (clr.equals("GREEN"))
				color = Color.GREEN;
			else if (clr.equals("MAGENTA"))
				color = Color.MAGENTA;
			return color;
		}

		@TargetApi(15)
		private void doOffsetNotificationsEnabled() {
			 setOffsetNotificationsEnabled(true);
		}
		
		/**
		 * Visibility changing handler
		 */
		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(animationInRunner);
				handler.removeCallbacks(animationOutRunner);
				handler.removeCallbacks(drawRunner);
			}
		}
		
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}
		
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(animationInRunner);
			handler.removeCallbacks(animationOutRunner);
			handler.removeCallbacks(drawRunner);
		}
		
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
	            float xOffsetStep, float yOffsetStep, int xPixelOffset,
	            int yPixelOffset) {
			
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
		                xPixelOffset, yPixelOffset);
			
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				canvas.translate(xPixelOffset, yPixelOffset);
				if (canvas != null) {
					canvas.drawColor(Color.DKGRAY);
					drawCubes(canvas, cubes, x, y);
				}
				
			} finally {
				if (canvas!=null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		
		/**
		 * Touch event handler 
		 */
		@Override
		public void onTouchEvent(MotionEvent event) {
			if (touchEnabled && !animationInProgress) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (color!=0) {
						fillByColor(cubes, color);
					} else {
						fillByColor(cubes, colors[(int)(Math.random()*6)]);
					}
					
					oldX = x;
					oldY = y;
					x = event.getX();
					y = event.getY();
					
					handler.post(animationOutRunner);
					handler.post(animationInRunner);
					
					super.onTouchEvent(event);
				}
			}
		}
		
		/**
		 * Changing destination coordinates of particles randomly
		 * and starting animation
		 */
		public void draw() {
			oldX = x;
			oldY = y;
			x = (float) (Math.random()*(width-14*15*density));
			if (x<0) x=0;
			y = (float) (Math.random()*(height-14*15*density));
			if (y<0) y=0;
			
			handler.post(animationOutRunner);
			handler.post(animationInRunner);
			
			handler.removeCallbacks(drawRunner);
			//handler.removeCallbacks(animationInRunner);
			//handler.removeCallbacks(animationOutRunner);
			if (visible) {
				handler.postDelayed(drawRunner, delay*1000);
			}
		}
		
		/**
		 * Drawing particles
		 * @param canvas - target canvas
		 * @param cubes - array of particles
		 * @param x - main x coordinate
		 * @param y - main y coordinate
		 */
		private void drawCubes(Canvas canvas, List<F3Cube> cubes, float x, float y) {
			//canvas.drawColor(Color.DKGRAY);
			for(F3Cube cube : cubes) {
				//Log.v("", cube.toString());
				Paint paint = new Paint();
			    paint.setAntiAlias(true);
			    paint.setColor(cube.getColor());
			    paint.setStyle(Paint.Style.FILL_AND_STROKE);
			    paint.setStrokeJoin(Paint.Join.ROUND);
			    paint.setStrokeWidth(2f);
			    float tmpX = cube.incX();
			    float tmpY = cube.incY();
			    if (kindOfParticles.equals(F3WallpaperEngine.PARTICLE_SQUARE)) {
			    	canvas.drawRect(new RectF(xOffset+x+tmpX*15*density, yOffset+y+tmpY*15*density, xOffset+x+tmpX*15*density+10*density, yOffset+y+tmpY*15*density+10*density), paint);
			    } else {
			    	canvas.drawCircle(xOffset+x+tmpX*15*density, yOffset+y+tmpY*15*density, 7.0f*density, paint);
			    }
			}
		}
		
		/**
		 * Change destination coordinates of particles randomly
		 * @param cubes - array of particles
		 */
		private void randomizeCubes(List<F3Cube> cubes) {
			for(F3Cube cube : cubes) {
				if (toBeOrNotToBe())
					cube.setToX((float)Math.random()*15*(float)Math.signum(Math.random()-0.5f));
				if (!toBeOrNotToBe())
				cube.setToY((float)Math.random()*15*(float)Math.signum(Math.random()-0.5f));
				cube.setdX(this.distance/this.framesPerSecond);
				cube.setdY(this.distance/this.framesPerSecond);
				//Log.v("","("+cube.getdX()+","+cube.getdY()+")");
			}
		}

		/**
		 * Making cubes with array of coordinates
		 * @param cubes - array of particles
		 */
		private void makeCubes(List<F3Cube> cubes) {
			cubes.clear();
			for(int i=0;i<figureX.length;i++) {
				cubes.add(new F3Cube(figureX[i], figureY[i], Color.WHITE));
			}
		}
		
		/**
		 * Change destination coordinates of particles
		 * @param cubes - array of particles
		 */
		private void fillCubesCoords(List<F3Cube> cubes) {
			int i=0;
			for(F3Cube cube : cubes) {
				cube.setToX(figureX[i]); 
				cube.setToY(figureY[i]);
				cube.setdX(this.distance/this.framesPerSecond);
				cube.setdY(this.distance/this.framesPerSecond);
				i++;
			}
		}
		
		/**
		 * Setting color from Table of colors for all particles randomly 
		 * @param cubes - array of particles
		 */
		private void randomizeColors(List<F3Cube> cubes) {
			for(F3Cube cube : cubes) {
				cube.setColor(colors[(int)(Math.random()*6)]);
			}
		}
		
		/**
		 * Setting the color for all particles 
		 * @param cubes - array of particles
		 * @param color - color of particles
		 */
		private void fillByColor(List<F3Cube> cubes, int color) {
			for(F3Cube cube : cubes) {
				cube.setColor(color);
			}
		}
		
		/**
		 * To be or not to be? :)
		 * @return
		 */
		private boolean toBeOrNotToBe() {
			return (Math.signum(Math.random()-0.5f)>0);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			delay = Integer.parseInt(prefs.getString("delay", "0"));
			touchEnabled = prefs.getBoolean("touch", false);
			kindOfParticles = prefs.getString("kind", F3WallpaperEngine.PARTICLE_SQUARE);
			colorOfParticles = prefs.getString("color", "RANDOM");
			
			int size = prefs.getInt("cubeSize", 0);
			if (size > 0) {
				if (size > 0) {
					figureX = new float[size];
					figureY = new float[size];
					for (int i=0;i<size;i++) {
						figureX[i] = prefs.getFloat("cubeX"+i, 0);
						figureY[i] = prefs.getFloat("cubeY"+i, 0);
					}
				}
				cubes.clear();
				makeCubes(cubes);
			}
			
			color = getColorFromPreference(colorOfParticles);
		}
	}

}
