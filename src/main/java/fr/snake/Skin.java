package fr.snake;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;


public enum Skin {
	SKIN_1(1,"/Interface/Skin/skin_4_head.png", "/Interface/Skin/skin_4_body2.png", "/Interface/Skin/skin_12_wreck.png"),
	SKIN_2(2,"/Interface/Skin/skin_1_head.png", "/Interface/Skin/skin_1_body.png", "/Interface/Skin/skin_1_tail.png"),
	SKIN_3(3,"/Interface/Skin/skin_11_head.png", "/Interface/Skin/skin_11_body.png", "/Interface/Skin/skin_11_body.png"),
	SKIN_4(4,"/Interface/Skin/skin_18_head.png", "/Interface/Skin/skin_18_body2.png", "/Interface/Skin/skin_18_body1.png"),
	SKIN_5(5,"/Interface/Skin/skin_12_head.png", "/Interface/Skin/skin_12_body.png", "/Interface/Skin/skin_12_body.png"),
	;

	public static final int MAX_SKIN_INDEX = 5;

	private BufferedImage headImage ;
	private BufferedImage bodyImage ;
	private BufferedImage tailImage ;
	private int index;
	Skin(int index, String headUrl, String bodyUrl, String tailUrl) {
		try {
			this.index = index;
			this.headImage = ImageIO.read(this.getClass().getResourceAsStream(headUrl)) ;
			this.bodyImage = ImageIO.read(this.getClass().getResourceAsStream(bodyUrl)) ;
			this.tailImage = ImageIO.read(this.getClass().getResourceAsStream(tailUrl)) ;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace() ;
		}
		catch (IOException e) {
			e.printStackTrace() ;
		}
	}
	
	public static Skin getSkin(int index){
		for (Skin skin : values()) {
			if(skin.getIndex()==index){
				return skin ;
			}
		}
		return null;
	}

	public Skin previousSkin() {
		if (index == 1) return getSkin(MAX_SKIN_INDEX) ;
		else return getSkin(index - 1) ;
	}

	public Skin nextSkin() {
		if (index == MAX_SKIN_INDEX) return getSkin(1);
		else return getSkin(index + 1) ;
	}
	
	public BufferedImage getHeadImage() {
		return headImage ;
	}
	
	public void setHeadImage(BufferedImage headImage) {
		this.headImage = headImage ;
	}
	
	public BufferedImage getBodyImage() {
		return bodyImage ;
	}
	
	public void setBodyImage(BufferedImage bodyImage) {
		this.bodyImage = bodyImage ;
	}
	
	public BufferedImage getTailImage() {
		return tailImage ;
	}
	
	public void setTailImage(BufferedImage tailImage) {
		this.tailImage = tailImage ;
	}

	
	public int getIndex() {
		return index ;
	}

	
	public void setIndex(int index) {
		this.index = index ;
	}

	public static Skin randomSkin(Random r) {
		Skin[] values = Skin.values() ;
		int index = r.nextInt(values.length) ;
		return values[index] ;
	}

}
