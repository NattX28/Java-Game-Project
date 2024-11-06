package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import main.GamePanel;



public class MouseManager {
    GamePanel gp;
    ArrayList<Mouse> mice;
    Random random;
    private int maxMice;
    private int spawnInterval;
    private int spawnCounter;
    public int spawnedMiceCount = 0;  // Counter to track the number of mice spawned
    
    public MouseManager(GamePanel gp){
        this.gp = gp;
        this.mice = new ArrayList<>();
        this.random = new Random();
        this.maxMice = 20; // maximun number of mice at once
        this.spawnInterval = 180; // Frames between spawn attemps
        this.spawnCounter = 0;
    }
    
    public void update(){
        //remove dead mice
        mice.removeIf(mouse -> !mouse.isAlive());
        
        // Try to spawn new mouse
        spawnCounter++;
        if(spawnCounter >= spawnInterval){
            if(mice.size() < maxMice){
                spawnMouse();
            }
            spawnCounter = 0;
        }
        
        // update all mice
        for(Mouse mouse : mice){
            mouse.update();    
        }
        
    }
    
    public void spawnMouse(){
        // Only spawn if spawnedMiceCount is less than targetMicePerLevel from GamePanel
        if (spawnedMiceCount >= gp.targetMicePerLevel+5) return;
        
        Random r = new Random();
        int range;
        if(gp.level == 1){
            int [][] spawnPoints = {
                {r.nextInt(1150,1200),r.nextInt(1000,1150)},
                {r.nextInt(1200,1400),r.nextInt(1480,1800)},
                {r.nextInt(1500,1800),r.nextInt(1730,1780)},
                {r.nextInt(2100,2200),r.nextInt(710,800)},
            };
            int[] spawnPoint = spawnPoints[random.nextInt(spawnPoints.length)];
            Mouse newMouse = new Mouse(gp,spawnPoint[0], spawnPoint[1]);
            mice.add(newMouse);
            spawnedMiceCount++;
        }else if(gp.level == 2){
            int [][] spawnPoints = {
                {r.nextInt(1150,1200),r.nextInt(1000,1150)},
                {r.nextInt(1200,1400),r.nextInt(1480,1800)},
                {r.nextInt(1500,1800),r.nextInt(1730,1780)},
                {r.nextInt(2100,2200),r.nextInt(710,800)},
                {r.nextInt(640,800),r.nextInt(1490,1600)},
                {r.nextInt(2460,2600),r.nextInt(1515,1530)},
                {r.nextInt(2370,2550),r.nextInt(1770,1790)},
            };
            int[] spawnPoint = spawnPoints[random.nextInt(spawnPoints.length)];
            Mouse newMouse = new Mouse(gp,spawnPoint[0], spawnPoint[1]);
            newMouse.speed = gp.level;
            mice.add(newMouse);
            spawnedMiceCount++;
        }else if(gp.level == 3){
            int [][] spawnPoints = {
                {r.nextInt(1150,1200),r.nextInt(1000,1150)},
                {r.nextInt(1200,1400),r.nextInt(1480,1800)},
                {r.nextInt(1500,1800),r.nextInt(1730,1780)},
                {r.nextInt(910,1950),r.nextInt(2048,2100)},
                {r.nextInt(750,1250),r.nextInt(2380,2400)},
                {r.nextInt(1900,2100),r.nextInt(710,800)},
                {r.nextInt(640,900),r.nextInt(1490,1600)},
                {r.nextInt(2460,2600),r.nextInt(1515,1530)},
                {r.nextInt(2370,2550),r.nextInt(1770,1790)},
                {r.nextInt(1416,2050),r.nextInt(2100,2150)},
                {r.nextInt(1500,1750),r.nextInt(2750,2800)},
            };
            int[] spawnPoint = spawnPoints[random.nextInt(spawnPoints.length)];
            Mouse newMouse = new Mouse(gp,spawnPoint[0], spawnPoint[1]);
            newMouse.speed = gp.level;
            mice.add(newMouse);
            spawnedMiceCount++;
        }
        
    }
    
    public void draw(Graphics2D g2){
         // Create a copy of the mice list to avoid concurrent modification
        ArrayList<Mouse> miceCopy = new ArrayList<>(mice);
        for(Mouse mouse : miceCopy){
            mouse.draw(g2);
        }
    }
    
    public boolean checkCatCollision(int catX, int catY, int size){
        Rectangle catBounds = new Rectangle(catX, catY, size, size);
        for(Mouse mouse : mice){
            Rectangle mouseBounds = new Rectangle(mouse.worldX, mouse.worldY, size/2, size/2);
            if(catBounds.intersects(mouseBounds)){
                mouse.setAlive(false);
                return true;
            }
        }
        return false;
    }
}