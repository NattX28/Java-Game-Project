package entity;

// parent of character class

import java.awt.image.BufferedImage;

public abstract class Entity {
    
    public int worldX, worldY;
    
    public int speed;
    
    public BufferedImage up1, up2,up3,up4,up5,down1,down2,down3,down4,down5,left1,left2,left3,left4,left5,right1,right2,right3,right4,right5,
            upLeft1,upLeft2,upLeft3,upLeft4,upLeft5,upRight1,upRight2,upRight3,upRight4,upRight5,
            downLeft1,downLeft2,downLeft3,downLeft4,downLeft5,downRight1,downRight2,downRight3,downRight4,downRight5;
    
    public String direction;
    
    public int spriteCounter = 0;
    public int spriteNum = 1;
    
    // abstart methods
    public abstract void update();
    public abstract void getEntityImage();
    protected abstract void setDefaultValues();
    
    
}
