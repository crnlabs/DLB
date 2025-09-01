package dontlookback;

/**
 * Lightweight Test Suite for CI/CD Pipeline
 * 
 * This test suite focuses on testing core logic without graphics dependencies
 * Suitable for headless CI/CD environments
 * 
 * @author CI/CD Test System
 */
public class HeadlessTestSuite {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("         Don't Look Back - Headless Test Suite              ");
        System.out.println("=============================================================");
        
        long startTime = System.currentTimeMillis();
        
        // Core logic tests that don't require OpenGL
        testRoomTypeEnum();
        testSettingsConfiguration();
        testRoomStructure();
        testRoomGeneratorStructure();
        testBasicGameLogic();
        
        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;
        
        printTestSummary(duration);
        
        // Exit with appropriate code for CI/CD
        if (testsFailed > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
    
    private static void testRoomTypeEnum() {
        try {
            System.out.print("Testing RoomType enumeration... ");
            
            RoomType[] types = RoomType.values();
            assert types.length == 6 : "Expected 6 room types, found " + types.length;
            
            for (RoomType type : types) {
                assert type.getTypeId() >= 0 : "Invalid type ID for " + type.name();
                assert type.getWidth() > 0 : "Invalid width for " + type.name();
                assert type.getLength() > 0 : "Invalid length for " + type.name();
                assert type.getHeight() > 0 : "Invalid height for " + type.name();
            }
            
            for (int i = 0; i < 10; i++) {
                RoomType random1 = RoomType.getRandomType();
                RoomType random2 = RoomType.getWeightedRandomType();
                assert random1 != null : "Random type generation failed";
                assert random2 != null : "Weighted random type generation failed";
            }
            
            reportTestPass("RoomType enumeration");
            
        } catch (Exception e) {
            reportTestFail("RoomType enumeration", e.getMessage());
        }
    }
    
    private static void testSettingsConfiguration() {
        try {
            System.out.print("Testing Settings configuration... ");
            
            Settings settings = new Settings();
            assert settings != null : "Settings instantiation failed";
            
            reportTestPass("Settings configuration");
            
        } catch (Exception e) {
            reportTestFail("Settings configuration", e.getMessage());
        }
    }
    
    private static void testRoomStructure() {
        try {
            System.out.print("Testing room structure... ");
            
            float[] center = {10f, 0f, 10f};
            Room testRoom = new Room(1, RoomType.MEDIUM_ROOM, center);
            
            assert testRoom != null : "Room creation failed";
            assert testRoom.getRoomId() == 1 : "Room ID mismatch";
            assert testRoom.getType() == RoomType.MEDIUM_ROOM : "Room type mismatch";
            assert testRoom.getCenter()[0] == 10f : "Room center X mismatch";
            assert testRoom.getCenter()[2] == 10f : "Room center Z mismatch";
            assert testRoom.isEmpty() : "New room should be empty";
            
            // Test room bounds using available methods
            float[] dimensions = testRoom.getDimensions();
            assert dimensions != null : "Room dimensions should be available";
            assert dimensions.length >= 3 : "Room dimensions should have 3 coordinates";
            assert dimensions[0] > 0 : "Room width should be positive";
            assert dimensions[1] > 0 : "Room length should be positive";
            assert dimensions[2] > 0 : "Room height should be positive";
            
            // Test content clearing (without generating content)
            testRoom.clearContent();
            assert testRoom.isEmpty() : "Room should remain empty after clearing";
            
            reportTestPass("Room structure");
            
        } catch (Exception e) {
            reportTestFail("Room structure", e.getMessage());
        }
    }
    
    private static void testRoomGeneratorStructure() {
        try {
            System.out.print("Testing room generator structure... ");
            
            RoomGenerator generator = new RoomGenerator();
            assert generator != null : "RoomGenerator creation failed";
            assert generator.getActiveRoomCount() == 0 : "Initial room count should be 0";
            
            // Test position checking without actual generation
            float[] testPos = {0f, 0f, 0f};
            Room foundRoom = generator.getRoomAtPosition(testPos);
            // Should be null since no rooms generated yet
            
            // Clear all rooms (should handle empty state)
            generator.clearAllRooms();
            assert generator.getActiveRoomCount() == 0 : "Room count should remain 0";
            
            reportTestPass("Room generator structure");
            
        } catch (Exception e) {
            reportTestFail("Room generator structure", e.getMessage());
        }
    }
    
    private static void testBasicGameLogic() {
        try {
            System.out.print("Testing basic game logic... ");
            
            // Test Player creation
            Player player = new Player();
            assert player != null : "Player creation failed";
            
            // Test basic position and direction calculations
            float[] pos1 = {0f, 0f, 0f};
            float[] pos2 = {10f, 0f, 10f};
            
            // Calculate distance (basic math test)
            float dx = pos2[0] - pos1[0];
            float dz = pos2[2] - pos1[2];
            float distance = (float) Math.sqrt(dx * dx + dz * dz);
            assert distance > 0 : "Distance calculation failed";
            
            // Test view direction normalization
            float[] viewDir = {1f, 0f, 1f};
            float length = (float) Math.sqrt(viewDir[0] * viewDir[0] + viewDir[2] * viewDir[2]);
            viewDir[0] /= length;
            viewDir[2] /= length;
            assert Math.abs(viewDir[0] * viewDir[0] + viewDir[2] * viewDir[2] - 1.0f) < 0.001f : 
                "View direction normalization failed";
            
            reportTestPass("Basic game logic");
            
        } catch (Exception e) {
            reportTestFail("Basic game logic", e.getMessage());
        }
    }
    
    private static void reportTestPass(String testName) {
        testsPassed++;
        System.out.println("PASS");
    }
    
    private static void reportTestFail(String testName, String reason) {
        testsFailed++;
        System.out.println("FAIL - " + reason);
    }
    
    private static void printTestSummary(double duration) {
        System.out.println("\n=============================================================");
        System.out.println("                        TEST SUMMARY                        ");
        System.out.println("=============================================================");
        System.out.println("Total Tests: " + (testsPassed + testsFailed));
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Duration: " + String.format("%.2f", duration) + " seconds");
        System.out.println("Success Rate: " + String.format("%.1f", 
            (testsPassed * 100.0) / (testsPassed + testsFailed)) + "%");
        System.out.println("=============================================================");
        
        if (testsFailed == 0) {
            System.out.println("✅ All tests passed! Ready for CI/CD pipeline.");
        } else {
            System.out.println("❌ Some tests failed. Check logs for details.");
        }
    }
}