package client;

import java.awt.Point;

import javax.swing.JOptionPane;

public class ChessRule {
	/**
	 * @param type 棋子的类型，1车2马3相4士5将6炮7卒 炮比较特殊，炮走类型为60 炮吃类型为61
	 * @param startI 棋子的起始横行
	 * @param startJ 棋子的起始纵行
	 * @param targetI 棋子的目标横行
	 * @param targetJ 棋子的目标纵行
	 * @return
	 */
	public static boolean judgeChess(String type,int startI,int startJ,int targetI,int targetJ,Point myKing,Point enemyKing){
		if(myKing.y==enemyKing.y&&myKing.y!=targetJ&&myKing.y==startJ&&!type.equals("5")){
			int num;
			num = -1;
			for(int i=enemyKing.x+1;i<myKing.x;i++){
				if(MyClient.isChessExist(i, myKing.y))num++;
			}
			if(num<=0)
			return false;
		}
		switch (type) {
		case "1":return judgeRooks(startI, startJ, targetI, targetJ);
		case "2":return judgeKnights(startI, startJ, targetI, targetJ);
		case "3":return judgeElephants(startI, startJ, targetI, targetJ);
		case "4":return judgeMandarins(startI, startJ, targetI, targetJ);
		case "5":return judgeKing(startI, startJ, targetI, targetJ,enemyKing);
		case "60":return judgeCannons("0",startI, startJ, targetI, targetJ);//6代表炮，0代表走
		case "61":return judgeCannons("1",startI, startJ, targetI, targetJ);//6代表炮，1代表吃
		case "7":return judgePawns(startI, startJ, targetI, targetJ);
		default:
			JOptionPane.showMessageDialog(null, "恩，一定有什么地方出毛病了");
			break;
		}
		return false;
	}

	private static boolean judgeRooks(int startI,int startJ,int targetI,int targetJ){
		if(startI == targetI){
			int min,max;
			if(startJ<targetJ){
				min = startJ;
				max = targetJ;
			}else {
				min = targetJ;
				max = startJ;
			}
			for(int j=min+1;j<max;j++){
				if(MyClient.isChessExist(startI, j))return false;
			}
			return true;
		}else if(startJ == targetJ){
			int min,max;
			if(startI<targetI){
				min = startI;
				max = targetI;
			}else {
				min = targetI;
				max = startI;
			}
			for(int i=min+1;i<max;i++){
				if(MyClient.isChessExist(i, startJ))return false;
			}
			return true;
		}
		return false;
	}

	private static boolean judgeKnights(int startI,int startJ,int targetI,int targetJ){
		int x = targetI - startI;
		int y = targetJ - startJ;
		if(x==2&&Math.abs(y)==1)
			return MyClient.isChessExist(startI+1, startJ)?false:true;
		else if(x==-2&&Math.abs(y)==1)
			return MyClient.isChessExist(startI-1, startJ)?false:true;
		else if(Math.abs(x)==1&&y==2)
			return MyClient.isChessExist(startI, startJ+1)?false:true;
		else if(Math.abs(x)==1&&y==-2)
			return MyClient.isChessExist(startI, startJ-1)?false:true;
		return false;
	}
	
	private static boolean judgeElephants(int startI,int startJ,int targetI,int targetJ){
		if(Math.abs(targetI-startI)==2&&Math.abs(targetJ-startJ)==2&&targetI>=5)
			return MyClient.isChessExist((startI+targetI)/2, (startJ+targetJ)/2)?false:true;
		return false;
	}
	
	private static boolean judgeMandarins(int startI,int startJ,int targetI,int targetJ){
		if(targetI>=7&&targetJ>=3&&targetJ<=5&&Math.abs(targetI-startI)==1&&Math.abs(targetJ-startJ)==1)
			return true;
		return false;
	}
	
	private static boolean judgeKing(int startI,int startJ,int targetI,int targetJ,Point enemyKing){
		if(targetI>=7&&targetJ>=3&&targetJ<=5
				&&((Math.abs(targetI-startI)==1&&Math.abs(targetJ-startJ)==0)
						||(Math.abs(targetI-startI)==0&&Math.abs(targetJ-startJ)==1))){
			if(targetJ==enemyKing.y){
				for(int i=enemyKing.x+1;i<targetI;i++){
					if(MyClient.isChessExist(i, targetJ))return true;
				}
			}else if(targetJ!=enemyKing.y){
				return true;
			}
		}
		return false;
	}
	
	private static boolean judgeCannons(String mode,int startI,int startJ,int targetI,int targetJ){
		int num=0;
		if(targetI == startI){
			int min,max;
			if(startJ<targetJ){
				min = startJ;
				max = targetJ;
			}else {
				min = targetJ;
				max = startJ;
			}
			for(int j=min+1;j<max;j++){
				if(MyClient.isChessExist(startI, j)&&mode.equals("1"))num++;
				else if(MyClient.isChessExist(startI, j)&&mode.equals("0")) return false;
			}
			if(mode.equals("0")) return true;
			if(mode.equals("1"))return num==1?true:false;
		}else if(targetJ == startJ){
			int min,max;
			if(startI<targetI){
				min = startI;
				max = targetI;
			}else {
				min = targetI;
				max = startI;
			}
			for(int i=min+1;i<max;i++){
				if(MyClient.isChessExist(i, startJ)&&mode.equals("1"))num++;
				else if(MyClient.isChessExist(i, startJ)&&mode.equals("0")) return false;
			}
			if(mode.equals("0")) return true;
			if(mode.equals("1"))return num==1?true:false;
		}
		return false;
	}
	
	private static boolean judgePawns(int startI,int startJ,int targetI,int targetJ){
		if(startI>=5&&targetI-startI==-1&&targetJ==startJ)return true;
		else if(startI<=4&&targetI-startI<=0){
			if(targetI-startI==0&&Math.abs(targetJ-startJ)==1)return true;
			else if(targetJ==startJ&&targetI-startI==-1)return true;
		}
		return false;
	}
	
}
