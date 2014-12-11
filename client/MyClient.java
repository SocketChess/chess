package client;

import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Toolkit;

import javax.swing.JTextField;

import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingConstants;

import java.awt.Font;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

import tool.ImageChange;
import tool.Scroll;

public class MyClient extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private static JPanel contentPane;
	private static JTextField ipFiled;
	private static JTextField name;
	private static JTextField post;
	private static JTextField say;
	private static JTextArea textArea;
	private static JLabel link;
	private static JLabel newGame;
	private static JLabel exit;

	private static JPanel panel = null;
	private static JLabel chessBoard;

	private static ClientThread client;
	private static Thread tempClient;
	private static Chess[][] chessPoint = new Chess[10][9];
	
	public static boolean chessFocus = false;//发过来的第一个信息如果是红方则为true，黑方则改为false;
	public static boolean boardFocus = false;//当单击过棋子的时候其才能变为true，然后如果给服务器发送了落子信息，则其变为false
	private static Chess tempFocusChess = null;
	//用于判断是否胜利，以及是否两将碰面
	private static Point myKing = new Point();
	private static Point enemyKing = new Point();
	
	public static boolean isChessExist(int i,int j){//判断该点上有没有棋子
		if(chessPoint[i][j]==null)return false;
		return true;
	}
	public static void moveChess(int oldI,int oldJ,int newI, int newJ){
		if(chessPoint[newI][newJ]!=null){
			panel.remove(chessPoint[newI][newJ]);
		}
		chessPoint[newI][newJ] = chessPoint[oldI][oldJ];
		chessPoint[oldI][oldJ] = null;
		chessPoint[newI][newJ].setPoint(newI, newJ);
		chessPoint[newI][newJ].setBounds(chessPoint[newI][newJ].getLeft(),chessPoint[newI][newJ].getTop(),40,40);
	}
	// private Chess newChess;
	/**
	 * 棋子的单击响应事件
	 */
	private static MouseListener chessListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			Chess pointChess = (Chess) e.getSource();
			/*textArea.append(pointChess.getId() + "\r\n");
			Scroll.showNewArea(textArea);*/
			if(chessFocus){
				//当是第一次单击棋子的时候，判断单击的棋子是否是自己的棋子
				//temp equals("r")只是临时用于判断的
				if(tempFocusChess==null
						&&chessFocus&&pointChess.getId().substring(0,1).equals(client.getPlayer())){
					tempFocusChess = pointChess;
					//开启棋盘的监听
					boardFocus = true;
				}
				//不是第一次单击棋子则判断第二次单击的是自己的棋子还是对方的棋子
				else if(tempFocusChess!=null
						&&pointChess.getId().substring(0,1).equals(tempFocusChess.getId().substring(0,1))){
					tempFocusChess = pointChess;
				}
				else if(tempFocusChess!=null&&!pointChess.getId().substring(0,1).equals(tempFocusChess.getId().substring(0,1))){
					int oldX = tempFocusChess.getPointX(),
							oldY = tempFocusChess.getPointY(),
							newX = pointChess.getPointX(),
							newY = pointChess.getPointY();
					String type;
					
					if ((type=tempFocusChess.getId().substring(1,2)).equals("6"))type+="1";//代表炮吃
					if(ChessRule.judgeChess(type, oldX, oldY, newX, newY,myKing,enemyKing)){
						if(type.equals("5")){
							myKing.x = newX;
							myKing.y = newY;
						}
						panel.remove(chessPoint[newX][newY]);
						chessPoint[newX][newY] = chessPoint[oldX][oldY];
						chessPoint[oldX][oldY] = null;
						chessPoint[newX][newY].setPoint(newX, newY);
						chessPoint[newX][newY].setBounds(chessPoint[newX][newY].getLeft(),chessPoint[newX][newY].getTop(),40,40);
						tempFocusChess = null;
						//走完棋了关闭棋盘的监听 
						boardFocus = false;
						chessFocus = false;
						String msg= "0"+oldX;
						msg = msg+oldY;
						msg = msg+newX;
						msg = msg+newY;
						client.send(msg);
						if(newX==enemyKing.x&&newY==enemyKing.y) {
							textArea.append("win\r\n");
							newGame.addMouseListener(newGameListener);
							JOptionPane.showMessageDialog(null, "恭喜恭喜，你赢了");
							client.send("5");
							int i = JOptionPane.showConfirmDialog(null, "是否开始一局新游戏","游戏",JOptionPane.YES_NO_OPTION);
							if(i==JOptionPane.YES_OPTION){
								client.send("4");
								newGame.removeMouseListener(newGameListener);
							}
						}
					}else
					textArea.append("走棋不符合规则\r\n");
				}
			}//if chessFocus
		}
	};
	/**
	 * 棋盘的监听事件
	 */
	private static MouseListener boardListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
//			textArea.append(e.getX()+" "+e.getY()+"\r\n");
			if(boardFocus){
				int x = e.getX(),
					y = e.getY();
				int  i,j;
				i=0;j=0;
				for(;j<9;j++){
					if(x>=20+45*j-20&&x<=20+45*j+20)
						break;
				}
				for(;i<10;i++){
					if(y>=31+45*i-20&&y<=31+45*i+20)
						break;
				}
				if(i!=10&&j!=9){//点击在有效区域内
					textArea.append(i+" "+j);//temp
					int oldX = tempFocusChess.getPointX(),
							oldY = tempFocusChess.getPointY();
					String type;
					if ((type=tempFocusChess.getId().substring(1,2)).equals("6"))type+="0";//代表炮走
					textArea.append("\r\n"+type+"\r\n");
					if(ChessRule.judgeChess(type, oldX, oldY, i, j,myKing,enemyKing)){
						if(type.equals("5")){
							myKing.x = i;
							myKing.y = j;
							System.out.println(myKing.x+" "+myKing.y);
						}
						
						chessPoint[i][j] = chessPoint[oldX][oldY];
						chessPoint[oldX][oldY] = null;
						chessPoint[i][j].setPoint(i, j);
						chessPoint[i][j].setBounds(chessPoint[i][j].getLeft(),
								chessPoint[i][j].getTop(),40,40);
						String msg= "0"+oldX;
						msg = msg+oldY;
						msg = msg+i;
						msg = msg+j;
						client.send(msg);
						tempFocusChess = null;
						boardFocus = false;
						chessFocus = false;
						panel.repaint();
					}else textArea.append("棋不符合规则");
					
				}
			}
		}
	};
	/**
	 * 监听连接按钮的事件
	 */
	private static MouseListener linkListener = new MouseAdapter() {// 连接的事件
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/link2.jpg")));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/link1.jpg")));
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {// 单击连接的事件
			try {

				textArea.append("here");
				Scroll.showNewArea(textArea);
				client = new ClientThread(new Socket(ipFiled.getText(),
						Integer.valueOf(post.getText())), textArea,
						name.getText(), newGame, exit, link);
				ClientThread.flag = true;//这里把client换成了类名，不知道有没有影响，出错的时候要注意
				tempClient = new Thread(client);
				tempClient.start();
				client.send(1 + "000" + name.getText());// 房间号000
				exit.addMouseListener(exitListener);
				link.setEnabled(false);
				link.removeMouseListener(linkListener);
				newGame.addMouseListener(newGameListener);
			} catch (IOException e) {
				JOptionPane
						.showMessageDialog(null, "无法连接到服务器，请检查服务器ip与端口号是否正确");

			}
		}
	};
	/**
	 * 监听退出按钮的事件
	 */
	private static MouseListener exitListener = new MouseAdapter() {// 退出的事件
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/exit2.jpg")));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/exit1.jpg")));
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {// 单击退出事件
			int i = JOptionPane.showConfirmDialog(null, "主人，真的要离开了吗？","回家了",JOptionPane.YES_NO_OPTION);
			if(i==JOptionPane.YES_OPTION){
				client.send("2");
				ClientThread.flag = false;// 因为flag设置为静态成员变量，所以虽然client被清空了，但是Flag的值还在，所以静态成员变量都应该在使用时注意初始化
				link.addMouseListener(linkListener);
				link.setEnabled(true);
				client = null;
				chessFocus=false;
				boardFocus = false;
				tempFocusChess = null;
				System.exit(0);
			}
		}
	};
	
	/**
	 * 监听发送按钮的事件
	 */
	private static MouseListener sendListener = new MouseAdapter() {// 发送的事件
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/send2.jpg")));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/send1.jpg")));
		}

		@Override
		public void mouseClicked(MouseEvent e) {// 发送信息事件
			client.send(3 + say.getText());
		}
	};
	/**
	 * 单击新游戏的时间
	 */
	public static MouseListener newGameListener = new MouseAdapter() {// 新游戏的事件
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/newGame2.jpg")));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JLabel tempJLabel = (JLabel) e.getSource();
			tempJLabel.setIcon(new ImageIcon(MyClient.class
					.getResource("/image/newGame1.jpg")));
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {// 单击新游戏事件
//			newChess("r");//测试用语句
			client.send("4");//先测试棋子的单击事件，该语句是需要保留的语句 本来，没有成功连接的话是无法触发新游戏的按钮的
			// newChess(client.getPlayer());
			newGame.removeMouseListener(newGameListener);
		}
	};
	
	
	
	/**
	 * Launch the application.
	 */
	public static void newChess(String player) {
		//清空棋盘
		contentPane.remove(panel);
		panel = new JPanel();
		panel.setBounds(0, 67, 409, 470);
		contentPane.add(panel);
		panel.setLayout(null);
		chessBoard = new JLabel("");
		chessBoard.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/pan.jpg")));
		chessBoard.setBounds(0, 0, 409, 470);
		chessBoard.addMouseListener(boardListener);
		panel.add(chessBoard);
		String enemy;
		enemy = player.equals("r") ? "b" : "r";
		for (int i = 0; i < chessPoint.length; i++) {// 全部初始化
			for (int j = 0; j < chessPoint[i].length; j++) {
				chessPoint[i][j] = null;
			}
		}
		// 绘制棋子
		// 画车
		chessPoint[0][0] = new Chess(enemy + 1, 0, 0);
		chessPoint[0][8] = new Chess(enemy + 1, 0, 8);
		chessPoint[9][0] = new Chess(player + 1, 9, 0);
		chessPoint[9][8] = new Chess(player + 1, 9, 8);
		// 画马
		chessPoint[0][1] = new Chess(enemy + 2, 0, 1);
		chessPoint[0][7] = new Chess(enemy + 2, 0, 7);
		chessPoint[9][1] = new Chess(player + 2, 9, 1);
		chessPoint[9][7] = new Chess(player + 2, 9, 7);
		// 画相
		chessPoint[0][2] = new Chess(enemy + 3, 0, 2);
		chessPoint[0][6] = new Chess(enemy + 3, 0, 6);
		chessPoint[9][2] = new Chess(player + 3, 9, 2);
		chessPoint[9][6] = new Chess(player + 3, 9, 6);
		// 画士
		chessPoint[0][3] = new Chess(enemy + 4, 0, 3);
		chessPoint[0][5] = new Chess(enemy + 4, 0, 5);
		chessPoint[9][3] = new Chess(player + 4, 9, 3);
		chessPoint[9][5] = new Chess(player + 4, 9, 5);
		// 画将
		chessPoint[0][4] = new Chess(enemy + 5, 0, 4);
		chessPoint[9][4] = new Chess(player + 5, 9, 4);
		enemyKing.x=0;enemyKing.y=4;
		myKing.x=9;myKing.y=4;
		// 画炮
		chessPoint[2][1] = new Chess(enemy + 6, 2, 1);
		chessPoint[2][7] = new Chess(enemy + 6, 2, 7);
		chessPoint[7][1] = new Chess(player + 6, 7, 1);
		chessPoint[7][7] = new Chess(player + 6, 7, 7);
		// 画兵
		chessPoint[3][0] = new Chess(enemy + 7, 3, 0);
		chessPoint[3][2] = new Chess(enemy + 7, 3, 2);
		chessPoint[3][4] = new Chess(enemy + 7, 3, 4);
		chessPoint[3][6] = new Chess(enemy + 7, 3, 6);
		chessPoint[3][8] = new Chess(enemy + 7, 3, 8);
		chessPoint[6][0] = new Chess(player + 7, 6, 0);
		chessPoint[6][2] = new Chess(player + 7, 6, 2);
		chessPoint[6][4] = new Chess(player + 7, 6, 4);
		chessPoint[6][6] = new Chess(player + 7, 6, 6);
		chessPoint[6][8] = new Chess(player + 7, 6, 8);

		/*
		 * chessPoint[3][0].setBounds(100,200,40,40);
		 * chessPoint[3][0].setIcon(new
		 * ImageIcon(MyClient.class.getResource("/chessImage/"
		 * +chessPoint[3][0].getId()+".gif"))); panel.add(chessPoint[3][0]);
		 * panel.validate();
		 */

		// 棋子都展示出来
		for (int i = 0; i < chessPoint.length; i++) {
			for (int j = 0; j < chessPoint[i].length; j++) {
				if (chessPoint[i][j] != null) {
					chessPoint[i][j].setBounds(chessPoint[i][j].getLeft(),
							chessPoint[i][j].getTop(), 40, 40);// setBounds设置完毕
					chessPoint[i][j].addMouseListener(chessListener);
					if (chessPoint[i][j].getId().substring(0, 1).equals(player)) {
						chessPoint[i][j].setIcon(new ImageIcon(MyClient.class
								.getResource("/chessImage/"
										+ chessPoint[i][j].getId() + ".gif")));
//						chessPoint[i][j].addMouseListener(chessListener);
					} else {
						chessPoint[i][j].setIcon(ImageChange
								.changDeg(new ImageIcon(MyClient.class
										.getResource("/chessImage/"
												+ chessPoint[i][j].getId()
												+ ".gif")// getResoure
								)// ImageIcon
								)// chengDeg
								);// setIcon
					}
					/*
					 * new ImageIcon( ImageChange.changDeg( new Im(
					 * MyClient.class.getResource("/chessImage/"
					 * +chessPoint[i][j].getId()+".gif" ) ) ) )
					 */
					panel.add(chessPoint[i][j]);
				}// if
			}// for j
		}// for i
			// 将chessBoard放在最下面
		panel.remove(chessBoard);
		panel.add(chessBoard);
		// 重绘
		panel.repaint();
		
		//事件判断的初始化
		if(player.equals("r")){
			chessFocus =true;
			tempFocusChess =null;
			boardFocus=false;
		}else{
			chessFocus =false;
			tempFocusChess =null;
			boardFocus=false;
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyClient frame = new MyClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame. 初始化方法
	 */
	public MyClient() {
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				MyClient.class.getResource("/image/p.gif")));
		setTitle("\u4E2D\u56FD\u8C61\u68CB");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 705, 629);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 240, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel = new JPanel();
		panel.setBounds(0, 67, 409, 470);
		contentPane.add(panel);
		panel.setLayout(null);

		/*
		 * newChess = new Chess("r1",3,5); newChess.setBounds(100,200,40,40);
		 * panel.add(newChess);
		 */

		/*
		 * newChess = new Chess("r1", 2, 3); newChess.setPosition(100, 200, 40,
		 * 40,panel);
		 */

		chessBoard = new JLabel("");
		chessBoard.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/pan.jpg")));
		chessBoard.setBounds(0, 0, 409, 470);
		chessBoard.addMouseListener(boardListener);
		panel.add(chessBoard);

		ipFiled = new JTextField();
		ipFiled.setText("127.0.0.1");
		ipFiled.setBackground(SystemColor.text);
		ipFiled.setBounds(538, 29, 152, 25);
		contentPane.add(ipFiled);
		ipFiled.setColumns(15);

		name = new JTextField();
		name.setText("player");
		name.setColumns(15);
		name.setBounds(538, 61, 152, 25);
		contentPane.add(name);

		post = new JTextField();
		post.setText("10000");
		post.setBounds(538, 92, 66, 21);
		contentPane.add(post);
		post.setColumns(10);

		newGame = new JLabel("");
//		newGame.addMouseListener(newGameListener);

		JLabel message = new JLabel("\u7B49\u5F85\u6E38\u620F\u5F00\u59CB");
		message.setFont(new Font("黑体", Font.PLAIN, 16));
		message.setBounds(286, 29, 123, 28);
		contentPane.add(message);
		newGame.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/newGame1.jpg")));
		newGame.setBounds(455, 220, 96, 30);
		contentPane.add(newGame);

		JLabel unDo = new JLabel("");
		unDo.addMouseListener(new MouseAdapter() {// 悔棋事件
			@Override
			public void mousePressed(MouseEvent e) {
				JLabel tempJLabel = (JLabel) e.getSource();
				tempJLabel.setIcon(new ImageIcon(MyClient.class
						.getResource("/image/unDo2.jpg")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				JLabel tempJLabel = (JLabel) e.getSource();
				tempJLabel.setIcon(new ImageIcon(MyClient.class
						.getResource("/image/unDo1.jpg")));
			}
		});
		unDo.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/unDo1.jpg")));
		unDo.setBounds(574, 220, 96, 30);
		contentPane.add(unDo);

		JLabel draw = new JLabel("");
		draw.addMouseListener(new MouseAdapter() {// 和局的事件
			@Override
			public void mousePressed(MouseEvent e) {
				JLabel tempJLabel = (JLabel) e.getSource();
				tempJLabel.setIcon(new ImageIcon(MyClient.class
						.getResource("/image/draw2.jpg")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				JLabel tempJLabel = (JLabel) e.getSource();
				tempJLabel.setIcon(new ImageIcon(MyClient.class
						.getResource("/image/draw1.jpg")));
			}
		});
		draw.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/draw1.jpg")));
		draw.setBounds(455, 253, 96, 30);
		contentPane.add(draw);

		exit = new JLabel("");
		
		exit.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/exit1.jpg")));
		exit.setBounds(574, 253, 96, 30);
		contentPane.add(exit);

		link = new JLabel("");
		link.addMouseListener(linkListener);
		link.setVerticalAlignment(SwingConstants.TOP);
		link.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/link1.jpg")));
		link.setBounds(617, 91, 73, 22);
		contentPane.add(link);

		say = new JTextField();
		say.setBounds(10, 547, 280, 25);
		contentPane.add(say);
		say.setColumns(10);

		JLabel send = new JLabel("");
		send.addMouseListener(sendListener);
		send.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/send1.jpg")));
		send.setBounds(306, 542, 88, 39);
		contentPane.add(send);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED,
				new Color(128, 128, 128), new Color(192, 192, 192), new Color(
						128, 128, 128), new Color(192, 192, 192)));
		scrollPane.setBounds(455, 318, 234, 219);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setBackground(new Color(173, 216, 230));
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(MyClient.class
				.getResource("/image/bg.jpg")));
		lblNewLabel.setBounds(0, 0, 700, 600);
		contentPane.add(lblNewLabel);
	}
}
