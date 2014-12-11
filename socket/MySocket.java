package socket;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ServerSocket;

public class MySocket extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField post;
	private ServerSocket server=null;
	private JTextArea showMessage;
	
	private SocketThread socketThread=null;
//	private static boolean flag=false;//服务器开启的关与闭

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MySocket frame = new MySocket();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MySocket() {
		setTitle("\u4E2D\u56FD\u8C61\u68CB\u670D\u52A1\u5668");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 409, 270);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 10, 200, 212);
		contentPane.add(scrollPane);
		
		this.showMessage = new JTextArea();
		this.showMessage.setEditable(false);
		scrollPane.setViewportView(this.showMessage);
		
		JLabel lblNewLabel = new JLabel("\u7AEF\u53E3\u53F7\uFF1A");
		lblNewLabel.setFont(new Font("楷体", Font.PLAIN, 14));
		lblNewLabel.setBounds(237, 10, 61, 15);
		contentPane.add(lblNewLabel);
		
		post = new JTextField();
		post.setText("10000");
		post.setBounds(297, 7, 66, 21);
		contentPane.add(post);
		post.setColumns(10);
		
		JButton start = new JButton("\u5F00\u542F\u8FDE\u63A5");
		start.addMouseListener(new MouseAdapter() {//开启连接事件
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					int post1 = Integer.valueOf(post.getText());
					server = new ServerSocket(post1);
					socketThread = new SocketThread(server, showMessage);
					new Thread(socketThread).start();
					showMessage.append("成功开启服务器\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "端口号已被占用请重新输入");
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null, "端口号填写不正确");
				}
			}
		});
		start.setBounds(237, 35, 126, 23);
		contentPane.add(start);
		
		JButton close = new JButton("\u5173\u95ED\u8FDE\u63A5");
		close.addMouseListener(new MouseAdapter() {//点击关闭连接事件
			@Override
			public void mouseClicked(MouseEvent arg0) {//清理服务器，初始化操作
				try {
					if(server!=null){
						server.close();
						server = null;
						socketThread.closeServer();
						socketThread=null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		close.setBounds(237, 85, 126, 23);
		contentPane.add(close);
		
		JButton showSimpleMessage = new JButton("\u67E5\u770B\u80DC\u8D1F\u60C5\u51B5");
		showSimpleMessage.setBounds(237, 135, 126, 23);
		contentPane.add(showSimpleMessage);
		
		JButton showDefinateMessage = new JButton("\u67E5\u770B\u8BE6\u7EC6\u4FE1\u606F");
		showDefinateMessage.setBounds(237, 185, 126, 23);
		contentPane.add(showDefinateMessage);
	}
}
