package tool;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageChange {
	public static ImageIcon changDeg(ImageIcon imageIcon){
		BufferedImage bi;
		BufferedImage bImage2;
		Image image;
		ByteArrayOutputStream out;
		image = imageIcon.getImage();
		bi = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		//将image会知道bi中
		Graphics2D g2D = bi.createGraphics();
		g2D.drawImage(image,0,0,null);
		
		//令bimage 是bimage1 的一个引用
		bImage2 = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
		DataBuffer db1 = bi.getRaster().getDataBuffer();
		DataBuffer db2 = bImage2.getRaster().getDataBuffer();
		for (int i=db1.getSize()-1,j=0;i>=0;i--,j++){
		db2.setElem(j,db1.getElem(i));
		}
		out = new ByteArrayOutputStream();  
        try {
			ImageIO.write(bImage2, "gif", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        byte[] b = out.toByteArray();  
        ImageIcon  reIcon= new ImageIcon(b);
        return reIcon;
	}
}
