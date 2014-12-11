package client;

import javax.swing.JLabel;

public class Chess extends JLabel{
	
	private static final long serialVersionUID = 2145118684985455179L;
	private String id;//棋子的身份第一位代表红黑，第二位代表车马之类的
	private int x,y;//棋子所处棋点的位置x为横线top y为纵线left左上角00
	private int top=31-20;
	private int left=21-20;
	/**
	 * @param id 棋子身份
	 * @param x 棋点横线
	 * @param y	棋点纵线
	 */
	public Chess(String id,int x,int y){
		super(id);
		this.id = id;
		this.x = x;
		this.y = y;
	}
	public void setPoint(int x,int y){
		this.x = x;
		this.y = y;
	}
	public int getPointX(){
		return this.x;
	}
	public int getPointY(){
		return this.y;
	}
	//z在这里这两个函数貌似是JLabel中自带有的函数，如果起名为这个的话就相当于覆写了JLabel中的方法
	//导致无法设置位置
	/*public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}*/
	public int getTop(){
		return x*45+top;
	}
	public int getLeft(){
		return y*45+left;
	}
	public String getId(){
		return this.id;
	}
}
