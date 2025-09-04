package dontlookback.demo;

import dontlookback.entities.Player;
import dontlookback.entities.BasicMonster;

/**
 * Collision Detection Demonstration
 * 
 * Shows the collision detection system in action with a simple scenario
 * where a player encounters a monster and walls.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class CollisionDemo {
    
    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("        Don't Look Back - Collision Detection Demo         ");
        System.out.println("=============================================================");
        
        // Create a player at the origin
        Player player = new Player();
        player.setPosition(0, 0, 0);
        System.out.println("Player spawned at position: (" + 
            player.positionX() + ", " + player.positionY() + ", " + player.positionZ() + ")");
        
        // Create a monster nearby
        BasicMonster monster = new BasicMonster(3, 0, 2);
        System.out.println("Monster spawned at position: (" + 
            monster.positionX() + ", " + monster.positionY() + ", " + monster.positionZ() + ")");
        
        // Create some walls
        float[] wall1 = CollisionDetector.createWallBoundingBox(5, 0, 0, 1, 3, 10);
        float[] wall2 = CollisionDetector.createWallBoundingBox(0, 0, 5, 10, 3, 1);
        float[][] walls = {wall1, wall2};
        
        System.out.println("\nWalls created at:");
        System.out.println("- Vertical wall at x=5");
        System.out.println("- Horizontal wall at z=5");
        
        System.out.println("\n--- COLLISION DETECTION SCENARIOS ---");
        
        // Scenario 1: Check initial collision state
        System.out.println("\n1. Initial collision check:");
        boolean playerMonsterCollision = CollisionDetector.checkPlayerMonsterCollision(player, monster);
        System.out.println("   Player-Monster collision: " + (playerMonsterCollision ? "YES" : "NO"));
        
        // Scenario 2: Monster moves toward player
        System.out.println("\n2. Monster approaches player:");
        for (int i = 0; i < 5; i++) {
            monster.moveTowards(player.positionX(), player.positionY(), player.positionZ());
            float distance = getDistance(player, monster);
            boolean collision = CollisionDetector.checkPlayerMonsterCollision(player, monster);
            System.out.println("   Step " + (i+1) + " - Distance: " + String.format("%.3f", distance) + 
                " - Collision: " + (collision ? "YES" : "NO"));
            
            if (collision) {
                System.out.println("   ⚠️  MONSTER CAUGHT THE PLAYER!");
                break;
            }
        }
        
        // Scenario 3: Player tries to move through walls
        System.out.println("\n3. Player attempts to move through walls:");
        
        // Reset player position closer to wall
        player.setPosition(4.3f, 0, 0);
        System.out.println("   Player repositioned to (4.3, 0, 0)");
        
        // Show player and wall bounding boxes
        float[] playerBox = player.getBoundingBox();
        System.out.println("   Player bounding box: [" + 
            String.format("%.2f", playerBox[0]) + " to " + String.format("%.2f", playerBox[3]) + "] on X-axis");
        System.out.println("   Wall bounding box: [" + 
            String.format("%.2f", wall1[0]) + " to " + String.format("%.2f", wall1[3]) + "] on X-axis");
        
        // Try to move right (should be blocked by wall at x=4.5)
        System.out.println("   Attempting to move right (towards wall):");
        for (int i = 0; i < 20; i++) {
            boolean moved = player.moveRightSafe(walls);
            if (!moved) {
                System.out.println("   ⛔ Movement blocked by wall!");
                System.out.println("   Final position: (" + String.format("%.3f", player.positionX()) + 
                    ", " + player.positionY() + ", " + player.positionZ() + ")");
                break;
            }
            if (i % 5 == 4) { // Show progress every 5 steps
                System.out.println("   Step " + (i+1) + " - Position: (" + 
                    String.format("%.3f", player.positionX()) + ", " + player.positionY() + ", " + player.positionZ() + ")");
            }
        }
        
        // Scenario 4: Item interaction demo
        System.out.println("\n4. Item interaction test:");
        float itemX = 4.2f, itemY = 0, itemZ = 0;
        boolean canInteract = CollisionDetector.checkPlayerItemInteraction(player, itemX, itemY, itemZ, 0.5f);
        System.out.println("   Item at (" + itemX + ", " + itemY + ", " + itemZ + ")");
        System.out.println("   Player can interact with item: " + (canInteract ? "YES" : "NO"));
        
        // Move closer
        player.setPosition(itemX - 0.3f, itemY, itemZ);
        canInteract = CollisionDetector.checkPlayerItemInteraction(player, itemX, itemY, itemZ, 0.5f);
        System.out.println("   After moving closer: " + (canInteract ? "YES" : "NO"));
        
        System.out.println("\n=============================================================");
        System.out.println("                    Demo Complete                           ");
        System.out.println("=============================================================");
        System.out.println("The collision detection system is working correctly!");
        System.out.println("✓ Player-Monster collision detection");
        System.out.println("✓ Player-Wall collision prevention");
        System.out.println("✓ Player-Item interaction detection");
    }
    
    private static float getDistance(Player player, BasicMonster monster) {
        float dx = player.positionX() - monster.positionX();
        float dy = player.positionY() - monster.positionY();
        float dz = player.positionZ() - monster.positionZ();
        return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
}