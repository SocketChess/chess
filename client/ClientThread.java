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
	private JLabel link;//ͨ����Ϣ�ж�ҳ�����Ӱ�ť��ʧЧ���
	private JLabel start;
	private JLabel exit;
	private String name;
	//temp ��ʱ����һ����������
	private String player = "r";//��ݣ�ִ���廹�Ǻ���r��b��
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
			//2�Ǵ���������������˳���Ϣ��flag���Ǵ���ͻ����Լ��Ĺر�ѡ��
		while((str=in.readLine())!="2"&flag){
			//�Դ��ݵ���Ϣ���ж�
//			this.showArea.append(str+"\r\n");
			//��ʾ���յ���Ϣ���ڲ���
			showArea.append(str+"\r\n");
			Scroll.showNewArea(showArea);
			switch (str.substring(0,1)) {
			case "1":
				this.player = str.substring(1,2);
				showArea.append("��������:"+(this.player.equals("r")?"�췽":"�ڷ�")+"\r\n");
				Scroll.showNewArea(showArea);
				break;
			case "3":
				int num = Integer.valueOf(str.substring(1,3));
				String name = str.substring(3,3+num);
				String say = str.substring(3+num);
				this.showArea.append(name+"����˵:"+say+"\r\n");
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
				JOptionPane.showMessageDialog(null, "���������");
				start.addMouseListener(MyClient.newGameListener);
				int i = JOptionPane.showConfirmDialog(null, "��Ҫ������Ϸô","����Ϸ",JOptionPane.YES_NO_OPTION);
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
