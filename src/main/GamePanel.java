package main;

import entity.Cat;
import entity.MouseManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import tile.TileMap;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 4;

    public final int tileSize = originalTileSize * scale; // 64x64 tile
    public final int maxScreenCol = 20; // 20 column
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 1280px
    public final int screenHeight = tileSize * maxScreenRow; // 768px

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // FPS
    int FPS = 60;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread,updateThread;
    public Cat cat = new Cat(this, keyH);
    public MouseManager mouseManager;
    public TileMap tileMap;
    public Collision collisionChecker;

    // Game States
    public enum GameState { TITLE, PLAYING, FINISHED, LEVEL_COMPLETED }
    public GameState gameState = GameState.TITLE;
    // Setting for each level
    public int level = 1;
    private int timeRemaining = 120; // 120 second
    public int targetMicePerLevel = 20;

    // Button positions and sizes for custom-drawn buttons
    private final int playButtonX = screenWidth / 2 - 50;
    private final int playButtonY = screenHeight / 2 + 200;
    private final int buttonWidth = 100;
    private final int buttonHeight = 50;
    private final int quitButtonY = screenHeight / 2 + 280;
    // Continue Button Position and Dimensions
    private final int continueButtonX = screenWidth / 2 - 50;
    private final int continueButtonY = screenHeight / 2 + 200;


    // Constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Initialize collision checker, tile map, and mouse manager
        tileMap = new TileMap(maxScreenCol, maxScreenRow, tileSize, this,level);
        collisionChecker = new Collision(this);
        mouseManager = new MouseManager(this);

        // Add MouseListener for custom buttons
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                int mouseX = e.getX();
                int mouseY = e.getY();

                // Check if Play button is clicked
                if (gameState == GameState.TITLE) {
                    if (mouseX >= playButtonX && mouseX <= playButtonX + buttonWidth &&
                        mouseY >= playButtonY && mouseY <= playButtonY + buttonHeight) {
                        startGame();
                    }

                    // Check if Quit button is clicked
                    if (mouseX >= playButtonX && mouseX <= playButtonX + buttonWidth &&
                        mouseY >= quitButtonY && mouseY <= quitButtonY + buttonHeight) {
                        System.exit(0);
                    }
                    
                } else if (gameState == GameState.LEVEL_COMPLETED) {
                // Continue button
                    if (mouseX >= continueButtonX && mouseX <= continueButtonX + buttonWidth &&
                        mouseY >= continueButtonY && mouseY <= continueButtonY + buttonHeight) {
                        startNextLevel();
                    }
                }
            } 
        });
    }

    private void startGame() {
        gameState = GameState.PLAYING;
        startGameThread();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        updateThread = new Thread(() -> {
            double delta = 0;
            double drawInterval = 1000000000/FPS;
            long currentTime;
            long lastTime = System.nanoTime();
            while (updateThread != null) {
                currentTime = System.nanoTime();
                delta += (currentTime - lastTime) / drawInterval;
                lastTime = currentTime;
                if (delta >= 1) {
                    update();
                    delta--;
                }
            }
        });
        gameThread.start();
        updateThread.start();
    }
    
    public GameState getGameState(){
        return gameState;
    }
    
    private void startNextLevel() {
    // Prepare for the next level
        level++;
        if (level <= 3) {
            cat.score = 0;
            timeRemaining = 120 - (level * 10);
            targetMicePerLevel += 10;
            mouseManager.spawnedMiceCount = 0;
            tileMap.changeLevel(level);
            cat.worldX = 1500;
            cat.worldY = 1500;
            gameState = GameState.PLAYING; // Resume playing
        } else {
            gameState = GameState.FINISHED; // End the game if max level is reached
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }
    private long lastTimeUpdate = System.nanoTime(); 
    public void update() {
        if (gameState == GameState.PLAYING) {
            cat.update();
            mouseManager.update();
             // down timeRemaining everysecond
            long currentTime = System.nanoTime();
            if ((currentTime - lastTimeUpdate) >= 1000000000) { 
                timeRemaining--;
                lastTimeUpdate = currentTime; // lastTimeUpdate
            }
        }
        if(cat.score >= targetMicePerLevel && timeRemaining > 0 ){
            if(level <3){
                gameState = GameState.LEVEL_COMPLETED;
            }else{
                gameState = GameState.FINISHED;
            }
        }
        if(timeRemaining <= 0){
           levelFailed();
        } 
    }
    
    public void drawTimer(Graphics2D g2){
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 30));
        g2.setColor(Color.WHITE);
        g2.drawString("Timer : ", 1000, 64);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 30));
        g2.drawString(""+timeRemaining, 1120, 64);
    }
    
    public void drawCatchedMice(Graphics2D g2){
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 30));
        g2.setColor(Color.WHITE);
        g2.drawString("Catched : ", 1000, 128);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 30));
        g2.drawString(""+ cat.score , 1160, 128);
        g2.drawString(" / "+ targetMicePerLevel, 1190, 128);
    }
    
    
    
    private void levelFailed(){
        level = 1;
        cat.score = 0;
        timeRemaining = 0;
        targetMicePerLevel = 20;
        
        gameState = GameState.TITLE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == GameState.TITLE) {
            BufferedImage bg = null;
            BufferedImage catImg = null;
            try {
                bg = ImageIO.read(getClass().getResource("/res/bg/House_map_bg.png"));
                catImg = ImageIO.read(getClass().getResource("/res/bg/cat_bg.png"));
               
            } catch (Exception e) {
                System.err.println("Error loading title screen or player image: " + e.getMessage());
            }
            g2.drawImage(bg, -200, -500, null);
            g2.drawImage(catImg, screenWidth/2-256,screenHeight/2-256, 512,512,this);
            
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(Color.WHITE);
            g2.drawString("PredaMeow", screenWidth / 2 - 150, screenHeight/2 - 250);

            // Draw Play button
            g2.setFont(new Font("Arial", Font.PLAIN, 36));
            g2.setColor(Color.WHITE);
            g2.fillRect(playButtonX, playButtonY, buttonWidth, buttonHeight);
            g2.setColor(Color.BLACK);
            g2.drawString("Play", playButtonX + 15, playButtonY + 35);

            // Draw Quit button
            g2.setColor(Color.WHITE);
            g2.fillRect(playButtonX, quitButtonY, buttonWidth, buttonHeight);
            g2.setColor(Color.BLACK);
            g2.drawString("Quit", playButtonX + 15, quitButtonY + 35);


        } else if (gameState == GameState.PLAYING) {
            tileMap.draw(g2);
            cat.draw(g2);
            mouseManager.draw(g2);
            drawTimer(g2);
            drawCatchedMice(g2);
            
        } else if (gameState == GameState.LEVEL_COMPLETED) {
            BufferedImage catLevelCompleted = null;
            
            try {
                catLevelCompleted = ImageIO.read(getClass().getResource("/res/bg/cat_level_completed.png"));
                
            } catch (Exception e) {
                System.err.println("Error loading title screen or player image: " + e.getMessage());
            }
            g2.drawImage(catLevelCompleted, screenWidth/2-256,screenHeight/2-256, 512,512,this);
            // Draw a message and "Continue" button after level completion
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(Color.WHITE);
            g2.drawString("Level Complete!", screenWidth / 2 - 180, screenHeight / 2 - 200 );

            g2.setFont(new Font("Arial", Font.PLAIN, 36));
            g2.setColor(Color.YELLOW);
            g2.fillRect(continueButtonX-40, continueButtonY, buttonWidth*2, buttonHeight);
            g2.setColor(Color.BLACK);
            g2.drawString("Continue", continueButtonX - 15 , continueButtonY + 35);
            
        }else if (gameState == GameState.FINISHED) {
            BufferedImage finishedBg = null;
            BufferedImage catFinished = null;
            try {
                finishedBg = ImageIO.read(getClass().getResource("/res/bg/finished_bg.png"));
                catFinished = ImageIO.read(getClass().getResource("/res/bg/cat_finished.png"));
            } catch (Exception e) {
                System.err.println("Error loading title screen or player image: " + e.getMessage());
            }
            g2.drawImage(finishedBg, -290, -400, null);
            g2.drawImage(catFinished, screenWidth/2-256,screenHeight/2-256, 512,512,this);
        }
        g2.dispose();
    }
}
