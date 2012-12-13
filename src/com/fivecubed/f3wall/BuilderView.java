package com.fivecubed.f3wall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BuilderView extends SurfaceView {

	// Родительский класс
	private Context mContext;
	private SurfaceHolder holder;
	// Класс поля с частицами
	private ParticlesBoard board=null;
	
    // Координаты частицы
    private int x=0, y=0;
    // Координаты частицы на канве
    private float ax=0, ay=0;
    //
    private float width = 0;
    private float height = 0;
	
    // Device desinty
	private float density;

	public BuilderView(Context context) {
		super(context);
		mContext = context;
		
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		density = dm.density;
		
		
		setZOrderOnTop(true);
        
		holder = getHolder();
		holder.setFormat(PixelFormat.TRANSPARENT);
		holder.addCallback(new SurfaceHolder.Callback() 
		{
			
			public void surfaceDestroyed(SurfaceHolder holder) 
			{
			}
			
			public void surfaceCreated(SurfaceHolder holder) 
			{
				try 
				{
			        Canvas c = holder.lockCanvas(null);
	                onDraw(c);
	                holder.unlockCanvasAndPost(c);
				}
				catch (Exception e) 
				{
					
				}
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) 
			{
			}
		});
		
		// Инициализация поля
		board = new ParticlesBoard(15, 15);
		
		Log.v("", "width: "+width+", height: "+height);
		
        for (int i=0;i<board.getWidth();i++) 
        {
            for (int j=0;j<board.getHeight();j++) 
            {
            	board.setParticle(i, j, 0);
            }
        }
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		int size = prefs.getInt("cubeSize", 0);
		int k=0;
		if (size > 0) {
			for (int i=0;i<size;i++) {
            	board.setParticle((int)prefs.getFloat("cubeX"+k, 0), (int)prefs.getFloat("cubeY"+k, 0), 1);
            	k++;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		width = (this.getViewWidth()/board.getWidth()); 
		height = width;
		if (canvas==null) return;
        for (int i=0;i<board.getWidth();i++) 
        {
            for (int j=0;j<board.getHeight();j++) 
            {
				Paint paint = new Paint();
			    paint.setAntiAlias(true);
			    paint.setColor((board.getParticle(i, j)==0)?Color.WHITE:Color.DKGRAY);
			    paint.setStyle(Paint.Style.FILL_AND_STROKE);
			    paint.setStrokeJoin(Paint.Join.ROUND);
			    paint.setStrokeWidth(2f);
			    float xOffset = -5;
			    float yOffset = -5;
			    float tmpX = i;
			    float tmpY = j;
			    canvas.drawRect(new RectF(xOffset+tmpX*width+5, yOffset+tmpY*height+5, xOffset+tmpX*width+width, yOffset+tmpY*height+height), paint);
            }
        }
	}
	
	/**
	 * Обрабатывает все прикосновения
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		super.onTouchEvent(event);
		
		switch(event.getAction()) 
		{
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				// Coords of particle
				ax=event.getX();
				ay=event.getY();
				x=(int)Math.ceil(ax/(this.getViewWidth()/board.getWidth()));
				y=(int)Math.ceil(ay/(this.getViewWidth()/board.getWidth()));
				
				Log.v("", "x: "+x+", y: "+y);
				
				if (x<1 || y<1) return true;
				if (x>board.getWidth() || y>board.getHeight()) return true;
				
				board.setParticle(x-1, y-1, (board.getParticle(x-1, y-1)==0)?1:0);
				
				break;
		}
		
        Canvas c = holder.lockCanvas(null);
        onDraw(c);
        holder.unlockCanvasAndPost(c);
		
	    return true;
	}
	
	/**
	 * Возвращает ширину поля
	 * @return
	 */
	public int getViewWidth() 
	{
		return this.getMeasuredWidth();
	}
	
	/**
	 * Возвращает высоту поля
	 * @return
	 */
	public int getViewHeight() 
	{
		return this.getMeasuredHeight();
	}
	
	/**
	 * Возвращает поле
	 * @return
	 */
	public ParticlesBoard getBoard() 
	{
		return this.board;
	}
	
}
