package com.fivecubed.f3wall;

public class ParticlesBoard {
	// ����
    private Integer particles[][] = null;
    // ������
    private int n;
    // ������
    private int m;
	
    /**
     * �����������
     * @param n ������ ����
     * @param m ������ ����
     */
	public ParticlesBoard(int n, int m) 
	{
		this.n=n;
		this.m=m;

		particles = new Integer[n][m];
	}
	

    /**
     * ���������� ���������  ������ ������
     * @return
     */
    public Integer[][] getParticles() 
    {
    	return particles;
    }

    /**
     * ���������� ������� �� �������� �����������
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
     * ������������� ������� �� �������� �����������
     * @param x
     * @param y
     */
    public void setParticle(int x, int y, Integer particle) 
    {
    	if ((x>=0 && x<n) && (y>=0 && y<m))
    		particles[x][y] = particle;
    }

    /**
     * ���������� ������ ����
     * @return
     */
    public int getWidth() 
    {
    	return n;
    }
    
    /**
     * ���������� ������ ����
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
