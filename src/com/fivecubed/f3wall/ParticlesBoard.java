package com.fivecubed.f3wall;

public class ParticlesBoard {
	// поле
    private Integer particles[][] = null;
    // ширина
    private int n;
    // высота
    private int m;
	
    /**
     *  онструктор
     * @param n ширина пол€
     * @param m высота пол€
     */
	public ParticlesBoard(int n, int m) 
	{
		this.n=n;
		this.m=m;

		particles = new Integer[n][m];
	}
	

    /**
     * ¬озвращает двумерный  массив частиц
     * @return
     */
    public Integer[][] getParticles() 
    {
    	return particles;
    }

    /**
     * ¬озвращает частицу по заданным координатам
     * @param x
     * @param y
     * @return
     */
    public Integer getParticle(int x, int y) 
    {
    	if ((x>=0 && x<n) && (y>=0 && y<m))
    		return particles[x][y];
    	return null;
    }
    
    /**
     * ”станавливает частицу по заданным координатам
     * @param x
     * @param y
     */
    public void setParticle(int x, int y, Integer particle) 
    {
    	if ((x>=0 && x<n) && (y>=0 && y<m))
    		particles[x][y] = particle;
    }

    /**
     * ¬озвращает ширину пол€
     * @return
     */
    public int getWidth() 
    {
    	return n;
    }
    
    /**
     * ¬озвращает высоту пол€
     * @return
     */
    public int getHeight() 
    {
    	return m;
    }
    
    public void clear() {
    	for(int i=0;i<n;i++) {
        	for(int j=0;j<m;j++) {
        		particles[i][j] = 0;
        	}
    	}
    }
    
}
