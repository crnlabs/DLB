package dontlookback.systems;

/**
 * Input Manager for Controller and Keyboard Support
 * 
 * Manages input from various sources including keyboards, gamepads, and other controllers.
 * Provides a unified interface for game input handling with configurable mappings.
 * 
 * Features:
 * - Multi-controller support
 * - Configurable button mappings  
 * - Analog stick and trigger support
 * - Input buffering and timing
 * - Accessibility options
 * 
 * @author DLB Team
 * @version 1.0
 */
public class InputManager {
    
    // === Controller Constants ===
    
    /** Controller button mappings */
    public static final int BUTTON_MOVE_FORWARD = 0;
    public static final int BUTTON_MOVE_BACKWARD = 1; 
    public static final int BUTTON_MOVE_LEFT = 2;
    public static final int BUTTON_MOVE_RIGHT = 3;
    public static final int BUTTON_INTERACT = 4;
    public static final int BUTTON_INVENTORY = 5;
    public static final int BUTTON_PAUSE = 6;
    public static final int BUTTON_LOOK_BACK = 7; // The dangerous button!
    
    /** Analog stick axes */
    public static final int AXIS_LEFT_X = 0;
    public static final int AXIS_LEFT_Y = 1;
    public static final int AXIS_RIGHT_X = 2;
    public static final int AXIS_RIGHT_Y = 3;
    public static final int AXIS_TRIGGER_LEFT = 4;
    public static final int AXIS_TRIGGER_RIGHT = 5;
    
    // === Input State ===
    
    /** Current button states (true = pressed) */
    private boolean[] buttonStates = new boolean[16];
    
    /** Previous frame button states for edge detection */
    private boolean[] previousButtonStates = new boolean[16];
    
    /** Current analog axis values (-1.0 to 1.0) */
    private float[] axisValues = new float[8];
    
    /** Controller deadzone threshold */
    private static final float DEADZONE = 0.15f;
    
    /** Input sensitivity multiplier */
    private float sensitivity = 1.0f;
    
    /** Whether controller input is enabled */
    private boolean controllerEnabled = true;
    
    /** Whether keyboard input is enabled */
    private boolean keyboardEnabled = true;
    
    // === Input Processing ===
    
    /**
     * Update input state (call once per frame)
     */
    public void update() {
        // Save previous states for edge detection
        System.arraycopy(buttonStates, 0, previousButtonStates, 0, buttonStates.length);
        
        // Poll controller inputs (placeholder - would integrate with LWJGL input)
        updateControllerInputs();
        
        // Poll keyboard inputs (placeholder)
        updateKeyboardInputs();
    }
    
    /**
     * Check if a button is currently pressed
     * @param button button constant
     * @return true if pressed
     */
    public boolean isButtonPressed(int button) {
        if (button < 0 || button >= buttonStates.length) return false;
        return buttonStates[button];
    }
    
    /**
     * Check if a button was just pressed this frame
     * @param button button constant
     * @return true if just pressed
     */
    public boolean isButtonJustPressed(int button) {
        if (button < 0 || button >= buttonStates.length) return false;
        return buttonStates[button] && !previousButtonStates[button];
    }
    
    /**
     * Check if a button was just released this frame
     * @param button button constant  
     * @return true if just released
     */
    public boolean isButtonJustReleased(int button) {
        if (button < 0 || button >= buttonStates.length) return false;
        return !buttonStates[button] && previousButtonStates[button];
    }
    
    /**
     * Get analog axis value with deadzone applied
     * @param axis axis constant
     * @return value from -1.0 to 1.0
     */
    public float getAxisValue(int axis) {
        if (axis < 0 || axis >= axisValues.length) return 0.0f;
        
        float value = axisValues[axis];
        
        // Apply deadzone
        if (Math.abs(value) < DEADZONE) {
            return 0.0f;
        }
        
        // Scale beyond deadzone for smoother control
        float sign = value > 0 ? 1.0f : -1.0f;
        float scaledValue = (Math.abs(value) - DEADZONE) / (1.0f - DEADZONE);
        
        return sign * scaledValue * sensitivity;
    }
    
    /**
     * Get movement vector from controller
     * @return float array {x, y} representing movement direction
     */
    public float[] getMovementVector() {
        float x = 0.0f;
        float y = 0.0f;
        
        // Check analog stick first
        if (controllerEnabled) {
            x = getAxisValue(AXIS_LEFT_X);
            y = getAxisValue(AXIS_LEFT_Y);
        }
        
        // Override with digital inputs if pressed
        if (isButtonPressed(BUTTON_MOVE_LEFT)) x = -1.0f;
        if (isButtonPressed(BUTTON_MOVE_RIGHT)) x = 1.0f;
        if (isButtonPressed(BUTTON_MOVE_FORWARD)) y = -1.0f;
        if (isButtonPressed(BUTTON_MOVE_BACKWARD)) y = 1.0f;
        
        return new float[]{x, y};
    }
    
    // === Configuration ===
    
    /**
     * Set input sensitivity
     * @param sensitivity sensitivity multiplier (0.1 to 2.0)
     */
    public void setSensitivity(float sensitivity) {
        this.sensitivity = Math.max(0.1f, Math.min(2.0f, sensitivity));
    }
    
    /**
     * Enable or disable controller input
     * @param enabled true to enable controller
     */
    public void setControllerEnabled(boolean enabled) {
        this.controllerEnabled = enabled;
    }
    
    /**
     * Enable or disable keyboard input
     * @param enabled true to enable keyboard
     */
    public void setKeyboardEnabled(boolean enabled) {
        this.keyboardEnabled = enabled;
    }
    
    /**
     * Check if any controller is connected
     * @return true if controller available
     */
    public boolean isControllerConnected() {
        // Placeholder - would check LWJGL controller status
        return controllerEnabled;
    }
    
    // === Internal Update Methods ===
    
    /**
     * Update controller input states (placeholder)
     */
    private void updateControllerInputs() {
        // Placeholder implementation
        // In real implementation, would poll LWJGL Controllers.poll()
        // and update buttonStates and axisValues accordingly
        
        // For now, just clear states
        if (!controllerEnabled) {
            for (int i = 0; i < buttonStates.length; i++) {
                buttonStates[i] = false;
            }
            for (int i = 0; i < axisValues.length; i++) {
                axisValues[i] = 0.0f;
            }
        }
    }
    
    /**
     * Update keyboard input states (placeholder)
     */
    private void updateKeyboardInputs() {
        // Placeholder implementation  
        // In real implementation, would integrate with LWJGL Keyboard
        // and map keyboard keys to button constants
        
        if (!keyboardEnabled) {
            return;
        }
        
        // Example mapping (would use actual LWJGL Keyboard.isKeyDown):
        // buttonStates[BUTTON_MOVE_FORWARD] = Keyboard.isKeyDown(Keyboard.KEY_W);
        // buttonStates[BUTTON_MOVE_BACKWARD] = Keyboard.isKeyDown(Keyboard.KEY_S);
        // etc.
    }
    
    // === Safety Features ===
    
    /**
     * Check if the dangerous "look back" action was triggered
     * This is the core horror mechanic - looking back spawns monsters!
     * @return true if player attempted to look back
     */
    public boolean isLookBackTriggered() {
        return isButtonJustPressed(BUTTON_LOOK_BACK) || 
               getAxisValue(AXIS_RIGHT_X) < -0.8f; // Hard right stick left
    }
    
    /**
     * Get a warning message for dangerous inputs
     * @return warning message or null if safe
     */
    public String getDangerWarning() {
        if (getAxisValue(AXIS_RIGHT_X) < -0.5f) {
            return "WARNING: Don't look back!";
        }
        return null;
    }
}