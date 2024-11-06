package tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import main.GamePanel;

public class TileMap {
    GamePanel gp;
    private TileManager[] tileManagers; // Array to hold multiple TileManagers
    private int [][][] mapData; // [layer][row][column]
    private final int maxScreenCol;
    private final int maxScreenRow;
    private final int tileSize;
    private static final int LAYERS = 9;
    public int level;
    
    public TileMap(int maxScreenCol, int maxScreenRow, int tileSize, GamePanel gp, int level){
        this.gp = gp;
        this.maxScreenCol = maxScreenCol;
        this.maxScreenRow = maxScreenRow;
        this.tileSize = tileSize;
        this.level = level;
        
        // Initialize map data array
        this.mapData = new int[LAYERS][maxScreenRow][maxScreenCol];
        
        // Initialize TileManagers
        setupTileManagers();
        
        // Load map data
        loadMap(level);
    }
    
    private void setupTileManagers(){
        tileManagers = new TileManager[2]; // for two tilesets
        
        // Define tile sizes and offsets
        int[] tileSizes = {32,32,0,0,0,0};
        
        // Create TileManagers for each tileset
        tileManagers[0] = new TileManager("/res/tiles/mainMap.png", tileSizes, true, true);
    }
    
    public void loadMap(int level){
        try{
            // Load each layer
            mapData = new int[LAYERS][50][50];
            for(int layer = 0 ; layer < LAYERS ; layer++){
                String layerPath = String.format("/res/maps/level%d/L_layer%d.csv",level, layer);
                InputStream is = getClass().getResourceAsStream(layerPath);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                
                int row = 0;
                String line;
                
                while((line = br.readLine()) != null){
                    String[] values = line.split(",");
                    int col = 0;
                    
//                    System.out.println(values.length);
                    for (int i = 0; i < values.length; i++) {
                        int temp = Integer.parseInt(values[i]);
                        mapData[layer][row][col++] = temp;
                    }
                    row++;
                }
                
                br.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void changeLevel(int newLevel){
        this.level = newLevel;
        loadMap(level);
    }
    
    public void draw(Graphics2D g2){
        // Calculate camera bounds based on cat position
        int worldStartX = gp.cat.worldX - gp.cat.screenX;
        int worldStartY = gp.cat.worldY - gp.cat.screenY;

        
        int tileStartScreenX = gp.cat.worldX - gp.cat.screenX;
        int tileStartScreenY = gp.cat.worldY - gp.cat.screenY;

        int startRow = Math.max(0, (tileStartScreenY / gp.tileSize));
        int startCol = Math.max(0, (tileStartScreenX / gp.tileSize));
        int endRow = Math.min(startRow + gp.maxScreenRow, 49);
        int endCol = Math.min(startCol + gp.maxScreenCol, 49);
        
        
        // Draw each layer
        for(int layer = 0 ; layer < LAYERS; layer++){
            for(int row = startRow; row < endRow+1; row++){
                for(int col = startCol; col < endCol+1; col++){
                    int tileNum = mapData[layer][row][col];
                    
                    // skip empty tiles
                    if (tileNum == -1) continue;
                    
                    // calculate screen position
                    int screenX = col * tileSize - worldStartX;
                    int screenY = row * tileSize - worldStartY;
                    
                    // Determine which tileset to use
                    TileManager targetTileset;
                    if(tileNum < tileManagers[0].newMaxColumn){
                        targetTileset = tileManagers[0];
                    }else{
                        targetTileset = tileManagers[1];
                        tileNum -= tileManagers[0].newMaxColumn;
                    }
                    // Draw the tile
                    BufferedImage tile = targetTileset.GetTile(0, tileNum);
                    if(tile != null){
                        g2.drawImage(tile,screenX,screenY,tileSize, tileSize, null);
                    }
                }
            }
        }
    }
    
    // Method to get collision data from the collision layer (usually layer 0)
    public int[][] getCollisionLayer(){
        return mapData[0];
    }
    
    // Method to get tile data data for a specific position
    public int getTileAt(int layer, int row, int col){
        // Check bounds
        if (layer >= 0 && layer < LAYERS &&
            row >= 0 && row < 50 &&  // Using fixed size instead of maxScreenRow
            col >= 0 && col < 50) {  // Using fixed size instead of maxScreenCol
            
            int tileNum = mapData[layer][row][col];
            return tileNum;
        }
        return -1;
    }
    
    // Method boolean isSolidTile(int tileNum
    public boolean isSolidTile(int tileNum){
        return tileNum != -1 && tileNum != 0; // Assuming 0 and -1 are non-solid tiles
    }
}
    

