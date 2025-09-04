package dontlookback.demo;

/**
 * Simple standalone test for room generation logic
 * Tests core functionality without OpenGL dependencies
 * 
 * @author Room Generator System
 */
public class SimpleRoomTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Simple Room Test...");
        
        testRoomTypeEnum();
        testBasicRoom();
        
        System.out.println("Simple Room Test completed successfully!");
    }
    
    private static void testRoomTypeEnum() {
        System.out.println("\n--- Testing RoomType Enum ---");
        
        // Test all room types
        for (RoomType type : RoomType.values()) {
            System.out.println("Room Type: " + type.name() + 
                              " - ID: " + type.getTypeId() + 
                              " - Dimensions: " + type.getWidth() + "x" + 
                              type.getLength() + "x" + type.getHeight());
        }
        
        // Test random generation
        System.out.println("\nRandom room types:");
        for (int i = 0; i < 5; i++) {
            System.out.println("Random: " + RoomType.getRandomType().name());
            System.out.println("Weighted: " + RoomType.getWeightedRandomType().name());
        }
    }
    
    private static void testBasicRoom() {
        System.out.println("\n--- Testing Basic Room Logic ---");
        
        // Test room positioning logic
        float[] center = {10f, 0f, 10f};
        System.out.println("Room center: [" + center[0] + ", " + center[1] + ", " + center[2] + "]");
        
        // Test room type selection
        RoomType selectedType = RoomType.getWeightedRandomType();
        System.out.println("Selected room type: " + selectedType.name());
        System.out.println("Room dimensions: " + selectedType.getWidth() + "x" + 
                          selectedType.getLength() + "x" + selectedType.getHeight());
        
        // Test room bounds calculation
        float[] dimensions = selectedType.getDimensions();
        float halfWidth = dimensions[0] / 2f;
        float halfLength = dimensions[1] / 2f;
        
        System.out.println("Room bounds:");
        System.out.println("  X: " + (center[0] - halfWidth) + " to " + (center[0] + halfWidth));
        System.out.println("  Z: " + (center[2] - halfLength) + " to " + (center[2] + halfLength));
        System.out.println("  Y: " + center[1] + " to " + (center[1] + dimensions[2]));
        
        // Test position within room
        float testX = center[0] + 2f;
        float testZ = center[2] + 2f;
        boolean isInRoom = (testX >= center[0] - halfWidth && testX <= center[0] + halfWidth &&
                           testZ >= center[2] - halfLength && testZ <= center[2] + halfLength);
        System.out.println("Position [" + testX + ", " + testZ + "] is in room: " + isInRoom);
    }
}