package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Mouse extends Entity implements Runnable {
    GamePanel gp;
    Thread mouseThread;
    Random random;
    private boolean alive;
    private int moveInterval;
    private int directionCounter;
    
    public Mouse(GamePanel gp, int startX, int startY){
        this.gp = gp;
        this.worldX = startX;
        this.worldY = startY;
        this.random = new Random();
        this.alive = true;
        this.moveInterval = 60;
        this.directionCounter = 0;
        setDefaultValues();
        getEntityImage();
        startMouseThread();
    }
    
    @Override
    protected void setDefaultValues(){
        this.speed = 1;
        this.direction = "right";
    }
    
    @Override
    public void getEntityImage(){
        try{
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_up_2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_up_3.png"));
            up4 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_up_4.png"));
            up5 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_up_5.png"));
            
            
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_down_2.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_down_3.png"));
            down4 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_down_4.png"));
            down5 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_down_5.png"));
            
            left1 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_left_2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_left_3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_left_4.png"));
            left5 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_left_5.png"));
            
            right1 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_right_2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_right_3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_right_4.png"));
            right5 = ImageIO.read(getClass().getResourceAsStream("/res/mouse/mouse_run_right_5.png"));
            
        }catch(IOException e){
            System.out.println("Error loading mouse images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startMouseThread() {
        mouseThread = new Thread(this);
        mouseThread.start();
    }
    
    public void setAlive(boolean alive){
        this.alive = alive;
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    private void changeDirection(){
        int newDirection = random.nextInt(4);
        switch(newDirection){
            case 0: direction = "up"; break;
            case 1: direction = "down"; break;
            case 2: direction = "left"; break;
            case 3: direction = "right"; break;
        }
    }
    
    @Override
    public void update(){
        if(gp.getGameState() == GamePanel.GameState.PLAYING){
            if(!alive) return;
            
            //Save previous position for colission checking
            int prevX = worldX;
            int prevY = worldY;
            // Move based on current direction
            if (gp.mouseManager.checkCatCollision(gp.cat.worldX, gp.cat.worldY, gp.tileSize)) {
                // Cat is in the way, so change direction
                changeDirection();
            }else{
                switch(direction){
                    case "up" : 
                        this.worldY -= speed + gp.cat.moveY;
                         this.worldX += gp.cat.moveX;
                        break;
                    case "down" : 
                        this.worldY += speed - gp.cat.moveY;
                        this.worldX += -gp.cat.moveX;
                        break;
                    case "left" : 
                        this.worldX -= speed + gp.cat.moveX ;
                        this.worldY += -gp.cat.moveY;
                        break;
                    case "right" : 
                        this.worldX += speed - gp.cat.moveX ;
                        this.worldY += gp.cat.moveY;
                        break;
                }
            }

            // Check collision with walls and furniture
            if (gp.collisionChecker.checkCollision(worldX, worldY, gp.tileSize/2, gp.tileSize/2)){
                this.worldX = prevX;
                this.worldY = prevY;
                changeDirection();
            }

            // Animation
            spriteCounter++;
            if(spriteCounter > 5){
                spriteNum = (spriteNum % 5) + 1;
                spriteCounter = 0;
            }

            // Periodically change direction
            directionCounter++;
            if(directionCounter >= moveInterval){
                if(random.nextInt(100) < 30){
                    changeDirection();
                }
                directionCounter = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        if (!alive) return;

        BufferedImage image = null;

        switch (direction) {
            case "up":
                if (spriteNum == 1) image = up1;
                else if (spriteNum == 2) image = up2;
                else if (spriteNum == 3) image = up3;
                else if (spriteNum == 4) image = up4;
                else if (spriteNum == 5) image = up5;
                break;

            case "down":
                if (spriteNum == 1) image = down1;
                else if (spriteNum == 2) image = down2;
                else if (spriteNum == 3) image = down3;
                else if (spriteNum == 4) image = down4;
                else if (spriteNum == 5) image = down5;
                break;

            case "left":
                if (spriteNum == 1) image = left1;
                else if (spriteNum == 2) image = left2;
                else if (spriteNum == 3) image = left3;
                else if (spriteNum == 4) image = left4;
                else if (spriteNum == 5) image = left5;
                break;

            case "right":
                if (spriteNum == 1) image = right1;
                else if (spriteNum == 2) image = right2;
                else if (spriteNum == 3) image = right3;
                else if (spriteNum == 4) image = right4;
                else if (spriteNum == 5) image = right5;
                break;
        }

        int worldStartX = gp.cat.worldX - gp.cat.screenX;
        int worldStartY = gp.cat.worldY - gp.cat.screenY;

        //System.out.println("x screen:" + worldStartX + " y screen: " + worldStartY);
        if (image != null) {
            g2.drawImage(image, worldX - worldStartX, worldY - worldStartY, gp.tileSize / 2, gp.tileSize / 2, null);
        }
    }

    @Override
    public void run(){
        while(alive){
            update();
            try{
                Thread.sleep(1000/60);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    
}
