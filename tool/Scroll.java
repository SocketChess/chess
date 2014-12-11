package tool;

import javax.swing.JTextArea;

public class Scroll {
public static void showNewArea(JTextArea textArea){
	textArea.setCaretPosition(textArea.getText().length());
}
}
