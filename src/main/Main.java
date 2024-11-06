package main;

import javax.swing.JFrame;


public class Main {

    
    public static void main(String[] args) {
        // Create Window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("PredaMeow");
        
        // Game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel); // add gamePanel to window
        
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        gamePanel.startGameThread();
    }
    
}
