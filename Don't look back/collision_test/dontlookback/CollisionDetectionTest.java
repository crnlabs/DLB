package dontlookback;

/**
 * Collision Detection Test Suite
 * 
 * Comprehensive tests for the collision detection system including:
 * - Player-Monster collision detection
 * - Player-Wall collision detection
 * - Player-Item interaction detection
 * - Movement collision prevention
 * - Bounding box calculations
 * 
 * @author DLB Team
 * @version 1.0
 */
public class CollisionDetectionTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("           Collision Detection Test Suite                   ");
        System.out.println("=============================================================");
        
        // Run all collision detection tests
        testPlayerPositioning();
        testMonsterCreation();
        testPlayerMonsterCollision();
        testPlayerWallCollision();
        testPlayerItemInteraction();
        testCollisionAwareMovement();
        testBoundingBoxCalculations();
        testEdgeCases();
        
        // Print results
        System.out.println("\n=============================================================");
        System.out.println("COLLISION DETECTION TEST RESULTS:");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("✓ ALL COLLISION DETECTION TESTS PASSED!");
        } else {
            System.out.println("✗ Some tests failed. Check output above for details.");
        }
        System.out.println("=============================================================");
    }
    
    private static void testPlayerPositioning() {
        System.out.println("\n--- PLAYER POSITIONING TESTS ---");
        
        try {
            System.out.print("Testing player creation and positioning... ");
            Player player = new Player();
            
            // Test initial position
            assert player.positionX() == 0.0f : "Initial X position should be 0";
            assert player.positionY() == 0.0f : "Initial Y position should be 0";
            assert player.positionZ() == 0.0f : "Initial Z position should be 0";
            
            // Test position setting
            player.setPosition(5.0f, 10.0f, 15.0f);
            assert player.positionX() == 5.0f : "X position should be 5.0";
            assert player.positionY() == 10.0f : "Y position should be 10.0";
            assert player.positionZ() == 15.0f : "Z position should be 15.0";
            
            // Test dimensions
            assert player.height() > 0 : "Player height should be positive";
            assert player.width() > 0 : "Player width should be positive";
            assert player.depth() > 0 : "Player depth should be positive";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing player movement methods... ");
            Player player = new Player();
            float initialX = player.positionX();
            float initialZ = player.positionZ();
            
            // Test basic movement
            player.moveToRight();
            assert player.positionX() > initialX : "X position should increase when moving right";
            
            player.moveToFront();
            assert player.positionZ() < initialZ : "Z position should decrease when moving forward";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testMonsterCreation() {
        System.out.println("\n--- MONSTER CREATION TESTS ---");
        
        try {
            System.out.print("Testing basic monster creation... ");
            BasicMonster monster = new BasicMonster();
            
            // Test initial state
            assert monster.positionX() == 0.0f : "Initial monster X position should be 0";
            assert monster.positionY() == 0.0f : "Initial monster Y position should be 0";
            assert monster.positionZ() == 0.0f : "Initial monster Z position should be 0";
            
            // Test monster attributes
            assert monster.hostile() : "Basic monster should be hostile by default";
            assert monster.legLength() > 0 : "Monster should have positive leg length";
            assert monster.scale() > 0 : "Monster should have positive scale";
            assert monster.weight() > 0 : "Monster should have positive weight";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing monster positioning... ");
            BasicMonster monster = new BasicMonster(10.0f, 5.0f, 20.0f);
            
            assert monster.positionX() == 10.0f : "Monster X position should be 10.0";
            assert monster.positionY() == 5.0f : "Monster Y position should be 5.0";
            assert monster.positionZ() == 20.0f : "Monster Z position should be 20.0";
            
            // Test bounding box
            float[] boundingBox = monster.getBoundingBox();
            assert boundingBox.length == 6 : "Bounding box should have 6 elements";
            assert boundingBox[0] < boundingBox[3] : "minX should be less than maxX";
            assert boundingBox[1] < boundingBox[4] : "minY should be less than maxY";
            assert boundingBox[2] < boundingBox[5] : "minZ should be less than maxZ";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testPlayerMonsterCollision() {
        System.out.println("\n--- PLAYER-MONSTER COLLISION TESTS ---");
        
        try {
            System.out.print("Testing no collision when far apart... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            BasicMonster monster = new BasicMonster(10, 0, 10);
            
            boolean collision = CollisionDetector.checkPlayerMonsterCollision(player, monster);
            assert !collision : "Should not detect collision when entities are far apart";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing collision when overlapping... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            BasicMonster monster = new BasicMonster(0, 0, 0);
            
            boolean collision = CollisionDetector.checkPlayerMonsterCollision(player, monster);
            assert collision : "Should detect collision when entities overlap";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing collision boundary conditions... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            // Test monster just touching the player boundary
            float[] playerBox = player.getBoundingBox();
            float playerEdge = playerBox[3]; // maxX of player
            
            BasicMonster monster = new BasicMonster(playerEdge + 0.01f, 0, 0);
            boolean noCollision = CollisionDetector.checkPlayerMonsterCollision(player, monster);
            assert !noCollision : "Should not detect collision when just outside boundary";
            
            monster.setPosition(playerEdge - 0.01f, 0, 0);
            boolean hasCollision = CollisionDetector.checkPlayerMonsterCollision(player, monster);
            assert hasCollision : "Should detect collision when just inside boundary";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testPlayerWallCollision() {
        System.out.println("\n--- PLAYER-WALL COLLISION TESTS ---");
        
        try {
            System.out.print("Testing wall collision detection... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            // Create a wall bounding box
            float[] wall = CollisionDetector.createWallBoundingBox(5, 0, 0, 2, 3, 1);
            
            // Player should not collide with wall when far away
            boolean collision = CollisionDetector.checkPlayerWallCollision(player, wall);
            assert !collision : "Should not detect collision with distant wall";
            
            // Move player close to wall
            player.setPosition(5, 0, 0);
            collision = CollisionDetector.checkPlayerWallCollision(player, wall);
            assert collision : "Should detect collision when player overlaps wall";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testPlayerItemInteraction() {
        System.out.println("\n--- PLAYER-ITEM INTERACTION TESTS ---");
        
        try {
            System.out.print("Testing item interaction detection... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            // Test interaction with nearby item
            boolean canInteract = CollisionDetector.checkPlayerItemInteraction(player, 0.3f, 0, 0, 0.2f);
            assert canInteract : "Should be able to interact with nearby item";
            
            // Test no interaction with distant item
            boolean cantInteract = CollisionDetector.checkPlayerItemInteraction(player, 5.0f, 0, 0, 0.2f);
            assert !cantInteract : "Should not be able to interact with distant item";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testCollisionAwareMovement() {
        System.out.println("\n--- COLLISION-AWARE MOVEMENT TESTS ---");
        
        try {
            System.out.print("Testing safe movement without obstacles... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            float[][] noObstacles = new float[0][];
            
            boolean moved = player.moveRightSafe(noObstacles);
            assert moved : "Should be able to move when no obstacles present";
            assert player.positionX() > 0 : "Player X position should have increased";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing blocked movement with obstacles... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            // Create obstacle directly in path of movement
            float[] obstacle = CollisionDetector.createWallBoundingBox(0.1f, 0, 0, 1, 3, 1);
            float[][] obstacles = {obstacle};
            
            float initialX = player.positionX();
            boolean moved = player.moveRightSafe(obstacles);
            assert !moved : "Should not be able to move when obstacle blocks path";
            assert player.positionX() == initialX : "Player position should not have changed";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testBoundingBoxCalculations() {
        System.out.println("\n--- BOUNDING BOX CALCULATION TESTS ---");
        
        try {
            System.out.print("Testing player bounding box calculation... ");
            Player player = new Player();
            player.setPosition(5, 10, 15);
            
            float[] box = player.getBoundingBox();
            
            // Check that bounding box is centered on player position
            float centerX = (box[0] + box[3]) / 2.0f;
            float centerZ = (box[2] + box[5]) / 2.0f;
            
            assert Math.abs(centerX - 5.0f) < 0.001f : "Bounding box should be centered on player X";
            assert Math.abs(centerZ - 15.0f) < 0.001f : "Bounding box should be centered on player Z";
            assert box[1] == 10.0f : "Bounding box bottom should be at player Y";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing wall bounding box creation... ");
            float[] wall = CollisionDetector.createWallBoundingBox(0, 0, 0, 4, 6, 2);
            
            // Check wall dimensions
            float width = wall[3] - wall[0];
            float height = wall[4] - wall[1];
            float depth = wall[5] - wall[2];
            
            assert Math.abs(width - 4.0f) < 0.001f : "Wall width should be 4";
            assert Math.abs(height - 6.0f) < 0.001f : "Wall height should be 6";
            assert Math.abs(depth - 2.0f) < 0.001f : "Wall depth should be 2";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static void testEdgeCases() {
        System.out.println("\n--- EDGE CASE TESTS ---");
        
        try {
            System.out.print("Testing zero-size collision detection... ");
            Player player = new Player();
            player.setPosition(0, 0, 0);
            
            // Test with zero-size wall
            float[] zeroWall = CollisionDetector.createWallBoundingBox(0, 0, 0, 0, 0, 0);
            boolean collision = CollisionDetector.checkPlayerWallCollision(player, zeroWall);
            
            // Should handle gracefully without crashing
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
        
        try {
            System.out.print("Testing monster movement towards player... ");
            Player player = new Player();
            player.setPosition(10, 0, 10);
            
            BasicMonster monster = new BasicMonster(0, 0, 0);
            float initialDistance = getDistance(player, monster);
            
            // Monster moves towards player
            monster.moveTowards(player.positionX(), player.positionY(), player.positionZ());
            float newDistance = getDistance(player, monster);
            
            assert newDistance < initialDistance : "Monster should move closer to player";
            
            System.out.println("PASSED");
            testsPassed++;
            
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            testsFailed++;
        }
    }
    
    private static float getDistance(Player player, BasicMonster monster) {
        float dx = player.positionX() - monster.positionX();
        float dy = player.positionY() - monster.positionY();
        float dz = player.positionZ() - monster.positionZ();
        return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
}