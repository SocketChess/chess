package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import tool.Scroll;


public class ClientThread implements Runnable{
	private Socket client;
	private BufferedReader in=null;
	private PrintStream out=null;
	private JTextArea showArea;
	private JLabel link;//通过消息判断页面连接按钮的失效与否
	private JLabel start;
	private JLabel exit;
	private String name;
	//temp 临时给与一个红棋的身份
	private String player = "r";//身份，执红棋还是黑棋r红b黑
	public static boolean flag = true;
	public String getPlayer(){
		return this.player;
	}
	public void send(String str){
		out.println(str);
	}
	public ClientThread(Socket client,JTextArea showArea,String name,JLabel start,JLabel exit,JLabel link){
		try{
			this.client = client;
			this.start = start;
			this.exit = exit;
			this.link = link;
			this.showArea = showArea;
			this.name = name;
			in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			out = new PrintStream(this.client.getOutputStream());
		}catch(IOException e){
			e.getStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String str;
		try{
			//2是代表服务器发来的退出消息，flag则是代表客户端自己的关闭选择
		while((str=in.readLine())!="2"&flag){
			//对传递的信息的判断
//			this.showArea.append(str+"\r\n");
			//显示接收的消息用于测试
			showArea.append(str+"\r\n");
			Scroll.showNewArea(showArea);
			switch (str.substring(0,1)) {
			case "1":
				this.player = str.substring(1,2);
				showArea.append("你的身份是:"+(this.player.equals("r")?"红方":"黑方")+"\r\n");
				Scroll.showNewArea(showArea);
				break;
			case "3":
				int num = Integer.valueOf(str.substring(1,3));
				String name = str.substring(3,3+num);
				String say = str.substring(3+num);
				this.showArea.append(name+"对你说:"+say+"\r\n");
				Scroll.showNewArea(showArea);
				break;
			case "4":
				MyClient.newChess(player);
				break;
			case "0":
//				showArea.append("haha\r\n");
				MyClient.chessFocus = true;
				int oldI = Integer.valueOf(str.substring(1,2));
				int oldJ = Integer.valueOf(str.substring(2,3));
				int newI = Integer.valueOf(str.substring(3,4));
				int newJ = Integer.valueOf(str.substring(4,5));
				MyClient.moveChess(oldI, oldJ, newI, newJ);
				break;
			case "5":
				JOptionPane.showMessageDialog(null, "残念，你输了");
				start.addMouseListener(MyClient.newGameListener);
				int i = JOptionPane.showConfirmDialog(null, "还要继续游戏么","新游戏",JOptionPane.YES_NO_OPTION);
				if(i==JOptionPane.YES_OPTION){
					send("4");
					start.removeMouseListener(MyClient.newGameListener);
				}
				break;
			default:
				break;
			}
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		link.setEnabled(true);
		
	}

}
