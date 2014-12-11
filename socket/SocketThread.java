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
	private ServerSocket server;//服务器连接
	private JTextArea showArea;//展示消息框
	public static int playerNumbers=0;
	private static AcceptThread[] acceptThreads = new AcceptThread[2];//一个房间游戏的两个玩家
	
	/**
	 * @param server 服务器
	 * @param show  展示框引用
	 */
	public static void clearThread(){
		for(int i=0;i<acceptThreads.length;i++){
			if(acceptThreads[i]!=null&&acceptThreads[i].getIsClose()){
				System.out.println("clear"+i+"线程");
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
	 * @param num 房间号
	 * @param player 接收信息的玩家，通过是红方还是黑方判断
	 * @param mess 发送的信息
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
	 * 关闭服务器线程
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
				if(playerNumbers<2){//最大游戏数为2人过了不在进行监听
					for(int i=0;i<2;i++){
						if(acceptThreads[i]==null&&server!=null){
							acceptThreads[i] = new AcceptThread(server.accept());
							acceptThreads[i].setPlayer(i+1);//在房间中先进来的为红，后进来的为黑
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
 *	接收客户端发过来的消息的线程
 */
class AcceptThread implements Runnable{
	private String name;//游戏名
	private int roomNumber; //房间号
	private int player;//1标识红方，2标识黑方
	private Socket client;//连接客户端的对象
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
	 * @param name 用户名
	 * @param room 房间号
	 * @param player 玩家持何种棋子
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
	 * @param str 向客户端发送的消息
	 */
	public void sendMessage(String str){
		this.pout.println(str);
	}
	/**
	 * @return 玩家信息
	 */
	public int getPlayer() {
		return player;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			String str;
			while(!(str=buf.readLine()).equals("2")){//如果发送过来的消息是2退出
				
				System.out.println(str);//查看发送的信息
				
				if(str.substring(0,1).equals("1")){//消息为1则代表游戏的初始化事件
					setPersonalMessage(str.substring(4), Integer.valueOf(str.substring(1, 4)));
					sendMessage("1"+(player==1?"r":"b"));
					System.out.println("1"+(player==1?"r":"b"));
				}else if(str.substring(0,1).equals("3")){//3代表发送的信息
					String temp = "3";
					temp += name.length()>9?name.length():"0"+name.length();
					temp += str.substring(1);
					if(!SocketThread.sendMessage(this.roomNumber, this.player==1?2:1, temp)){
						sendMessage("303"+"系统娘"+"该房间内没有其他人");
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
