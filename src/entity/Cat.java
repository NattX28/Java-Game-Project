package entity;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;


public class Cat extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    
    public int score;
    public final int screenX;
    public final int screenY;
    public int moveX = 0;
    public int moveY = 0;
    
    private long slowStartTime = 0;
    private boolean isSlowed = false;
    private static final long SLOW_DURATION = 1000; // 2 seconds (2000 millisecond)
    private static final int NORMAL_SPEED = 4;
    private static final int SLOW_SPEED = 3;
    
    public Cat(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        
        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize/2);
        
        setDefaultValues();
        getEntityImage();
    }
    
    @Override
    protected void setDefaultValues(){
        worldX = 1500;
        worldY = 1500;
        speed = 4; // means 4 px
        direction = "down";
        score = 0;
    }
    
    private void updateSpeed() {
        if (isSlowed) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - slowStartTime >= SLOW_DURATION) {
                isSlowed = false;
                speed = NORMAL_SPEED;
            }
        }
    }
    
    @Override
    public void getEntityImage(){
        try {
            
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_up_2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_up_3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_up_4.png"));
            up5 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_up_5.png"));
            
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_down_2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_down_3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_down_4.png"));
            down5 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_down_5.png"));
            
            left1 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_left_2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_left_3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_left_4.png"));
            left5 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_left_5.png"));
            
            right1 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_right_2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_right_3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_right_4.png"));
            right5 = ImageIO.read(getClass().getResourceAsStream("/res/cat/cat_run_right_5.png"));
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public void update(){
        if(gp.getGameState() == GamePanel.GameState.PLAYING){
            moveX = 0;
            moveY = 0;
        if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true){
            // Save current postion in case we need to revert due to collision
            int prevX = worldX;
            int prevY = worldY;
            
            updateSpeed();
            
            if(keyH.upPressed == true){
                direction = "up";
                worldY -= speed;
                moveY = -0;
            }else if(keyH.downPressed == true){
                direction = "down";
                worldY += speed;
                moveY = 0;
            }else if(keyH.leftPressed == true){
                direction = "left";
                worldX -= speed;
                moveX = -0;
            }else if(keyH.rightPressed == true){
                direction = "right";
                worldX += speed;
                moveX = 0;
            }
            
//            System.out.println("X: " + worldX + " Y: " + worldY);
             //Check collision with new position
            if(gp.collisionChecker.checkCollision(worldX, worldY, gp.tileSize - 8, gp.tileSize - 8)) {
                worldX = prevX;
                worldY = prevY;
                // if collison count new down speed
                if (!isSlowed) {
                    isSlowed = true;
                    speed = SLOW_SPEED;
                    slowStartTime = System.currentTimeMillis();
                }
            }
            
            if (gp.mouseManager.checkCatCollision(worldX, worldY, gp.tileSize)) {
                score++;
            }
        
        // Update animation
        spriteCounter++;
        if(spriteCounter > 5) {
            spriteNum = (spriteNum % 5) + 1;
            spriteCounter = 0;
        }
       }
    }
        
        
    }
    
    public void draw(Graphics2D g2){            
        BufferedImage image = null;
        
        switch(direction){
            case "up":
                if(spriteNum == 1) image = up1;
                if(spriteNum == 2) image = up2;
                if(spriteNum == 3) image = up3;
                if(spriteNum == 4) image = up4;
                if(spriteNum == 5) image = up5;
                break;
            case "down":
                if(spriteNum == 1) image = down1;
                if(spriteNum == 2) image = down2;
                if(spriteNum == 3) image = down3;
                if(spriteNum == 4) image = down4;
                if(spriteNum == 5) image = down5;
                break;
            case "left":
                if(spriteNum == 1) image = left1;
                if(spriteNum == 2) image = left2;
                if(spriteNum == 3) image = left3;
                if(spriteNum == 4) image = left4;
                if(spriteNum == 5) image = left5;
                break;
            case "right":
                if(spriteNum == 1) image = right1;
                if(spriteNum == 2) image = right2;
                if(spriteNum == 3) image = right3;
                if(spriteNum == 4) image = right4;
                if(spriteNum == 5) image = right5;
                break;
        }
        
        g2.drawImage(image, screenX, screenY, gp.tileSize,gp.tileSize, null);
    }

}
