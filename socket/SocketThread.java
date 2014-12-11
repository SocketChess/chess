package socket;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;

import test.socket;


public class SocketThread implements Runnable{
	private ServerSocket server;//����������
	private JTextArea showArea;//չʾ��Ϣ��
	public static int playerNumbers=0;
	private static AcceptThread[] acceptThreads = new AcceptThread[2];//һ��������Ϸ���������
	
	/**
	 * @param server ������
	 * @param show  չʾ������
	 */
	public static void clearThread(){
		for(int i=0;i<acceptThreads.length;i++){
			if(acceptThreads[i]!=null&&acceptThreads[i].getIsClose()){
				System.out.println("clear"+i+"�߳�");
				acceptThreads[i] = null;
			}
		}
	}
	public SocketThread(ServerSocket server,JTextArea show) {
		 this.server = server;
		 this.showArea = show;
		 for(int i=0;i<acceptThreads.length;i++){
			 acceptThreads[i]=null;
		 }
	}
	/**
	 * @param num �����
	 * @param player ������Ϣ����ң�ͨ���Ǻ췽���Ǻڷ��ж�
	 * @param mess ���͵���Ϣ
	 */
	public static boolean sendMessage(int num,int player,String mess){
		boolean result = true;
		for(int i=0;i<acceptThreads.length;i++){
			if(acceptThreads[i]!=null&&player==acceptThreads[i].getPlayer()){
				if(mess.equals("5")){acceptThreads[i].setReady(false);}
				acceptThreads[i].sendMessage(mess);
				result = true;
				break;
			}else if(acceptThreads[i]==null){
				result = false;
				break;
			}
		}
		return result;
	}
	public static boolean JudgeReady() throws NullPointerException{
		boolean result = true;
		for(int i=0;i<acceptThreads.length;i++){
			if(acceptThreads[i]!=null){
			result = result&&acceptThreads[i].getReady();
			}else return false;
		}
		return result;
	}
	/**
	 * �رշ������߳�
	 */
	public void closeServer(){
		 try {
			server.close();
			server=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(server!=null){
			try {
				if(playerNumbers<2){//�����Ϸ��Ϊ2�˹��˲��ڽ��м���
					for(int i=0;i<2;i++){
						if(acceptThreads[i]==null&&server!=null){
							acceptThreads[i] = new AcceptThread(server.accept());
							acceptThreads[i].setPlayer(i+1);//�ڷ������Ƚ�����Ϊ�죬�������Ϊ��
							showArea.append("success\r\n");
							new Thread(acceptThreads[i]).start();
							playerNumbers++;
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		showArea.append("over\r\n");
	}

}

/**
 * @author Administrator
 *	���տͻ��˷���������Ϣ���߳�
 */
class AcceptThread implements Runnable{
	private String name;//��Ϸ��
	private int roomNumber; //�����
	private int player;//1��ʶ�췽��2��ʶ�ڷ�
	private Socket client;//���ӿͻ��˵Ķ���
	private BufferedReader buf=null;
	private PrintStream pout=null;
	private boolean OK;
	private boolean isClose;
	public AcceptThread(Socket client){
		try {
			this.client = client;
			OK = false;
			buf = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			pout = new PrintStream(this.client.getOutputStream());
			isClose = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param name �û���
	 * @param room �����
	 * @param player ��ҳֺ�������
	 */
	public void setPersonalMessage(String name,int room){
		this.name = name;
		this.roomNumber = room;
	}
	public void setPlayer(int p){
		this.player = p;
	}
	public boolean getReady(){
		return this.OK;
	}
	public void setReady(boolean b){
		this.OK = b;
	}
	public boolean getIsClose(){
		return isClose;
	}
	/**
	 * @param str ��ͻ��˷��͵���Ϣ
	 */
	public void sendMessage(String str){
		this.pout.println(str);
	}
	/**
	 * @return �����Ϣ
	 */
	public int getPlayer() {
		return player;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			String str;
			while(!(str=buf.readLine()).equals("2")){//������͹�������Ϣ��2�˳�
				
				System.out.println(str);//�鿴���͵���Ϣ
				
				if(str.substring(0,1).equals("1")){//��ϢΪ1�������Ϸ�ĳ�ʼ���¼�
					setPersonalMessage(str.substring(4), Integer.valueOf(str.substring(1, 4)));
					sendMessage("1"+(player==1?"r":"b"));
					System.out.println("1"+(player==1?"r":"b"));
				}else if(str.substring(0,1).equals("3")){//3�����͵���Ϣ
					String temp = "3";
					temp += name.length()>9?name.length():"0"+name.length();
					temp += str.substring(1);
					if(!SocketThread.sendMessage(this.roomNumber, this.player==1?2:1, temp)){
						sendMessage("303"+"ϵͳ��"+"�÷�����û��������");
					}
				}else if(str.substring(0,1).equals("4")){
					this.OK = true;
					if(SocketThread.JudgeReady()){
						sendMessage("4");
						SocketThread.sendMessage(this.roomNumber, this.player==1?2:1, "4");
					}
				}
				else if(str.substring(0,1).equals("0")){
					Point oldSrcPoint = new Point();
					Point newSrcPoint = new Point();
					oldSrcPoint.x = Integer.valueOf(str.substring(1,2));
					oldSrcPoint.y = Integer.valueOf(str.substring(2,3));
					newSrcPoint.x = Integer.valueOf(str.substring(3,4));
					newSrcPoint.y = Integer.valueOf(str.substring(4,5));
					String msg="0";
					msg+=9-oldSrcPoint.x;
					msg+=8-oldSrcPoint.y;
					msg+=9-newSrcPoint.x;
					msg+=8-newSrcPoint.y;
					SocketThread.sendMessage(this.roomNumber, this.player==1?2:1, msg);
				}
				else if(str.substring(0,1).equals("5")){
					this.OK = false;
					SocketThread.sendMessage(this.roomNumber, this.player==1?2:1, "5");
				}
			}
			System.out.println("here");
			buf.close();
			pout.close();
			client.close();
			isClose = true;
			SocketThread.playerNumbers--;
			SocketThread.clearThread();
		} catch (IOException e) {
			// TODO: handle exception
		}
		
	}
	
}
