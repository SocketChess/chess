package client;

import javax.swing.JLabel;

public class Chess extends JLabel{
	
	private static final long serialVersionUID = 2145118684985455179L;
	private String id;//���ӵ���ݵ�һλ�����ڣ��ڶ�λ������֮���
	private int x,y;//������������λ��xΪ����top yΪ����left���Ͻ�00
	private int top=31-20;
	private int left=21-20;
	/**
	 * @param id �������
	 * @param x ������
	 * @param y	�������
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
	//z����������������ò����JLabel���Դ��еĺ������������Ϊ����Ļ����൱�ڸ�д��JLabel�еķ���
	//�����޷�����λ��
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
