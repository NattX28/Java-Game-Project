
package main;


import tile.TileMap;

public class Collision {
    GamePanel gp;
    TileMap tileMap;
    
    // Define collision layers
    private static final int[] COLLISION_LAYERS = {4, 5, 6, 7};
    
    public Collision(GamePanel gp) {
        this.gp = gp;
        this.tileMap = gp.tileMap;
    }
    
    public boolean checkCollision(int worldX, int worldY, int entityWidth, int entityHeight) {
        // Convert world coordinates to tile coordinates
        int entityLeftCol = worldX / gp.tileSize;
        int entityRightCol = (worldX + entityWidth - 1) / gp.tileSize;
        int entityTopRow = worldY / gp.tileSize;
        int entityBottomRow = (worldY + entityHeight - 1) / gp.tileSize;
        
        // Check bounds first
        if (!isWithinMapBounds(entityTopRow, entityLeftCol) || 
            !isWithinMapBounds(entityBottomRow, entityRightCol)) {
            return true;
        }
        
        // Check each collision layer
        for (int layer : COLLISION_LAYERS) {
            // Check all tiles that the entity might overlap with
            for (int row = entityTopRow; row <= entityBottomRow; row++) {
                for (int col = entityLeftCol; col <= entityRightCol; col++) {
                    // Get tile at current position
                    int tileNum = tileMap.getTileAt(layer, row, col);
                    if (tileNum > 0) {  
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    // Helper method to check if specific tile coordinates are within map bounds
    private boolean isWithinMapBounds(int row, int col) {
        return row >= 0 && row < gp.maxWorldRow && 
               col >= 0 && col < gp.maxWorldCol;
    }
}
