package dontlookback;

import java.util.*;

/**
 * Comprehensive Inventory System for Don't Look Back
 * 
 * Manages player inventory including light sources, keys, tools, and collectibles.
 * Supports item stacking, categorization, quick-use slots, and persistence.
 * 
 * Features:
 * - Multi-category item organization
 * - Stack management for consumables
 * - Quick-use hotbar for essential items
 * - Weight and capacity limits
 * - Item condition tracking
 * - Auto-sorting and filtering
 * 
 * @author DLB Team
 * @version 1.0
 */
public class InventorySystem {
    
    // === Inventory Constants ===
    
    /** Maximum total inventory slots */
    public static final int MAX_INVENTORY_SLOTS = 40;
    
    /** Number of quick-use hotbar slots */
    public static final int HOTBAR_SLOTS = 8;
    
    /** Maximum weight capacity (arbitrary units) */
    public static final float MAX_WEIGHT_CAPACITY = 100.0f;
    
    /** Maximum stack size for stackable items */
    public static final int MAX_STACK_SIZE = 50;
    
    // === Item Categories ===
    
    public enum ItemCategory {
        LIGHT_SOURCES("Light Sources", 15),
        KEYS("Keys & Access", 10),
        TOOLS("Tools & Equipment", 8),
        CONSUMABLES("Consumables", 25),
        DOCUMENTS("Documents & Notes", 20),
        COLLECTIBLES("Collectibles", 10),
        QUEST_ITEMS("Quest Items", 5),
        MISCELLANEOUS("Miscellaneous", 15);
        
        private final String displayName;
        private final int maxSlots;
        
        ItemCategory(String displayName, int maxSlots) {
            this.displayName = displayName;
            this.maxSlots = maxSlots;
        }
        
        public String getDisplayName() { return displayName; }
        public int getMaxSlots() { return maxSlots; }
    }
    
    // === Item Definitions ===
    
    public static class InventoryItem {
        private final String id;
        private final String displayName;
        private final String description;
        private final ItemCategory category;
        private final float weight;
        private final boolean stackable;
        private final boolean consumable;
        private final int maxStackSize;
        private final String iconPath;
        
        // Item state
        private int quantity;
        private float condition; // 0.0 to 1.0 (1.0 = perfect condition)
        private Map<String, Object> properties;
        
        public InventoryItem(String id, String displayName, String description, 
                           ItemCategory category, float weight, boolean stackable, 
                           boolean consumable, int maxStackSize, String iconPath) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.weight = weight;
            this.stackable = stackable;
            this.consumable = consumable;
            this.maxStackSize = stackable ? Math.min(maxStackSize, MAX_STACK_SIZE) : 1;
            this.iconPath = iconPath;
            this.quantity = 1;
            this.condition = 1.0f;
            this.properties = new HashMap<>();
        }
        
        // Getters
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public ItemCategory getCategory() { return category; }
        public float getWeight() { return weight; }
        public boolean isStackable() { return stackable; }
        public boolean isConsumable() { return consumable; }
        public int getMaxStackSize() { return maxStackSize; }
        public String getIconPath() { return iconPath; }
        public int getQuantity() { return quantity; }
        public float getCondition() { return condition; }
        public Map<String, Object> getProperties() { return properties; }
        
        // Setters
        public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }
        public void setCondition(float condition) { this.condition = Math.max(0.0f, Math.min(1.0f, condition)); }
        public void setProperty(String key, Object value) { properties.put(key, value); }
        public Object getProperty(String key) { return properties.get(key); }
        
        // Utility methods
        public float getTotalWeight() { return weight * quantity; }
        public boolean canStack(InventoryItem other) {
            return stackable && other.stackable && id.equals(other.id) && 
                   Math.abs(condition - other.condition) < 0.01f;
        }
        
        public int addToStack(int amount) {
            if (!stackable) return amount;
            int canAdd = Math.min(amount, maxStackSize - quantity);
            quantity += canAdd;
            return amount - canAdd;
        }
        
        public int removeFromStack(int amount) {
            int removed = Math.min(amount, quantity);
            quantity -= removed;
            return removed;
        }
        
        public InventoryItem copy() {
            InventoryItem copy = new InventoryItem(id, displayName, description, category, 
                                                 weight, stackable, consumable, maxStackSize, iconPath);
            copy.quantity = this.quantity;
            copy.condition = this.condition;
            copy.properties = new HashMap<>(this.properties);
            return copy;
        }
        
        @Override
        public String toString() {
            String conditionStr = condition < 1.0f ? String.format(" (%.0f%%)", condition * 100) : "";
            String quantityStr = stackable && quantity > 1 ? " x" + quantity : "";
            return displayName + quantityStr + conditionStr;
        }
    }
    
    // === Inventory Slots ===
    
    private static class InventorySlot {
        private InventoryItem item;
        private final int slotIndex;
        private boolean locked;
        
        public InventorySlot(int slotIndex) {
            this.slotIndex = slotIndex;
            this.item = null;
            this.locked = false;
        }
        
        public boolean isEmpty() { return item == null; }
        public InventoryItem getItem() { return item; }
        public void setItem(InventoryItem item) { this.item = item; }
        public void clear() { this.item = null; }
        public int getSlotIndex() { return slotIndex; }
        public boolean isLocked() { return locked; }
        public void setLocked(boolean locked) { this.locked = locked; }
    }
    
    // === Inventory State ===
    
    /** Main inventory slots */
    private final InventorySlot[] inventorySlots;
    
    /** Quick-use hotbar slots (subset of main inventory) */
    private final int[] hotbarSlotIndices;
    
    /** Currently selected hotbar slot */
    private int selectedHotbarSlot;
    
    /** Inventory organization by category */
    private final Map<ItemCategory, List<InventorySlot>> categorySlots;
    
    /** Total weight of all items */
    private float currentWeight;
    
    /** Inventory settings */
    private boolean autoSort;
    private boolean autoStack;
    private boolean allowOverweight;
    
    /** Item registry for creating new items */
    private final Map<String, InventoryItem> itemRegistry;
    
    // === Statistics ===
    private int totalItemsCollected;
    private int totalItemsUsed;
    private float totalWeightCarried;
    
    /**
     * Create a new inventory system
     */
    public InventorySystem() {
        this.inventorySlots = new InventorySlot[MAX_INVENTORY_SLOTS];
        this.hotbarSlotIndices = new int[HOTBAR_SLOTS];
        this.selectedHotbarSlot = 0;
        this.categorySlots = new HashMap<>();
        this.currentWeight = 0.0f;
        this.autoSort = true;
        this.autoStack = true;
        this.allowOverweight = false;
        this.itemRegistry = new HashMap<>();
        
        // Initialize slots
        for (int i = 0; i < MAX_INVENTORY_SLOTS; i++) {
            inventorySlots[i] = new InventorySlot(i);
        }
        
        // Initialize hotbar (first 8 slots)
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            hotbarSlotIndices[i] = i;
        }
        
        // Initialize category slot tracking
        for (ItemCategory category : ItemCategory.values()) {
            categorySlots.put(category, new ArrayList<>());
        }
        
        // Register default items
        registerDefaultItems();
        
        System.out.println("Inventory system initialized with " + MAX_INVENTORY_SLOTS + 
                          " slots and " + HOTBAR_SLOTS + " hotbar slots");
    }
    
    // === Item Registry ===
    
    /**
     * Register default game items
     */
    private void registerDefaultItems() {
        // Light sources
        registerItem(new InventoryItem("match", "Match", "A small wooden match. Burns for about 10 seconds.",
                                     ItemCategory.LIGHT_SOURCES, 0.1f, true, true, 50, "match.png"));
        
        registerItem(new InventoryItem("candle", "Candle", "A wax candle. Provides steady light for several minutes.",
                                     ItemCategory.LIGHT_SOURCES, 0.5f, true, false, 10, "candle.png"));
        
        registerItem(new InventoryItem("torch", "Torch", "A wooden torch. Bright light but burns quickly.",
                                     ItemCategory.LIGHT_SOURCES, 1.0f, true, false, 5, "torch.png"));
        
        registerItem(new InventoryItem("flashlight", "Flashlight", "Battery-powered flashlight. Reliable but limited battery.",
                                     ItemCategory.LIGHT_SOURCES, 2.0f, false, false, 1, "flashlight.png"));
        
        registerItem(new InventoryItem("lantern", "Lantern", "Oil lantern. Long-lasting and bright.",
                                     ItemCategory.LIGHT_SOURCES, 3.0f, false, false, 1, "lantern.png"));
        
        // Keys and access items
        registerItem(new InventoryItem("brass_key", "Brass Key", "An old brass key. Opens certain doors.",
                                     ItemCategory.KEYS, 0.2f, false, false, 1, "brass_key.png"));
        
        registerItem(new InventoryItem("skeleton_key", "Skeleton Key", "A master key that opens many locks.",
                                     ItemCategory.KEYS, 0.3f, false, false, 1, "skeleton_key.png"));
        
        registerItem(new InventoryItem("keycard", "Access Keycard", "Electronic keycard for secure areas.",
                                     ItemCategory.KEYS, 0.1f, false, false, 1, "keycard.png"));
        
        // Tools
        registerItem(new InventoryItem("lighter", "Lighter", "Refillable lighter. Can light candles and torches.",
                                     ItemCategory.TOOLS, 0.3f, false, false, 1, "lighter.png"));
        
        registerItem(new InventoryItem("crowbar", "Crowbar", "Heavy iron crowbar. Can force open doors.",
                                     ItemCategory.TOOLS, 5.0f, false, false, 1, "crowbar.png"));
        
        registerItem(new InventoryItem("wire_cutters", "Wire Cutters", "For cutting wires and small obstacles.",
                                     ItemCategory.TOOLS, 1.5f, false, false, 1, "wire_cutters.png"));
        
        // Consumables
        registerItem(new InventoryItem("bandage", "Bandage", "Medical bandage. Restores health when used.",
                                     ItemCategory.CONSUMABLES, 0.2f, true, true, 20, "bandage.png"));
        
        registerItem(new InventoryItem("battery", "Battery", "AA battery. Can recharge flashlights.",
                                     ItemCategory.CONSUMABLES, 0.3f, true, true, 10, "battery.png"));
        
        registerItem(new InventoryItem("oil", "Lamp Oil", "Refuel for oil lamps and lanterns.",
                                     ItemCategory.CONSUMABLES, 0.8f, true, true, 5, "oil.png"));
        
        // Documents
        registerItem(new InventoryItem("note", "Handwritten Note", "A note with important information.",
                                     ItemCategory.DOCUMENTS, 0.1f, true, false, 30, "note.png"));
        
        registerItem(new InventoryItem("map", "Area Map", "A map showing the layout of nearby rooms.",
                                     ItemCategory.DOCUMENTS, 0.2f, false, false, 1, "map.png"));
        
        registerItem(new InventoryItem("journal", "Personal Journal", "Someone's personal journal with entries.",
                                     ItemCategory.DOCUMENTS, 0.5f, false, false, 1, "journal.png"));
        
        // Collectibles
        registerItem(new InventoryItem("coin", "Old Coin", "An antique coin. Might be valuable.",
                                     ItemCategory.COLLECTIBLES, 0.1f, true, false, 50, "coin.png"));
        
        registerItem(new InventoryItem("photograph", "Photograph", "An old photograph. Shows something important.",
                                     ItemCategory.COLLECTIBLES, 0.1f, true, false, 20, "photograph.png"));
        
        System.out.println("Registered " + itemRegistry.size() + " default items");
    }
    
    /**
     * Register a new item type
     * @param item Item template to register
     */
    public void registerItem(InventoryItem item) {
        itemRegistry.put(item.getId(), item);
    }
    
    /**
     * Create an item instance from registry
     * @param itemId ID of item to create
     * @param quantity Quantity to create
     * @return New item instance, or null if not found
     */
    public InventoryItem createItem(String itemId, int quantity) {
        InventoryItem template = itemRegistry.get(itemId);
        if (template == null) {
            System.out.println("Warning: Unknown item ID: " + itemId);
            return null;
        }
        
        InventoryItem item = template.copy();
        item.setQuantity(quantity);
        return item;
    }
    
    // === Core Inventory Operations ===
    
    /**
     * Add an item to inventory
     * @param item Item to add
     * @return true if successfully added, false if inventory full
     */
    public boolean addItem(InventoryItem item) {
        if (item == null) return false;
        
        // Check weight limit
        if (!allowOverweight && currentWeight + item.getTotalWeight() > MAX_WEIGHT_CAPACITY) {
            System.out.println("Cannot add " + item.getDisplayName() + " - would exceed weight limit");
            return false;
        }
        
        // Try to stack with existing items first
        if (autoStack && item.isStackable()) {
            InventoryItem remaining = tryStackWithExisting(item);
            if (remaining == null) {
                updateWeight();
                totalItemsCollected += item.getQuantity();
                System.out.println("Added " + item.getDisplayName() + " (stacked)");
                return true;
            }
            item = remaining; // Continue with remainder
        }
        
        // Find empty slot
        InventorySlot emptySlot = findEmptySlot(item.getCategory());
        if (emptySlot != null) {
            emptySlot.setItem(item);
            categorySlots.get(item.getCategory()).add(emptySlot);
            updateWeight();
            totalItemsCollected += item.getQuantity();
            
            System.out.println("Added " + item.getDisplayName() + " to slot " + emptySlot.getSlotIndex());
            
            if (autoSort) {
                sortInventory();
            }
            
            return true;
        }
        
        System.out.println("Cannot add " + item.getDisplayName() + " - inventory full");
        return false;
    }
    
    /**
     * Add item by ID and quantity
     * @param itemId Item ID from registry
     * @param quantity Quantity to add
     * @return true if successfully added
     */
    public boolean addItem(String itemId, int quantity) {
        InventoryItem item = createItem(itemId, quantity);
        return item != null && addItem(item);
    }
    
    /**
     * Remove item from inventory
     * @param slotIndex Slot to remove from
     * @param quantity Quantity to remove (for stackable items)
     * @return Removed item, or null if slot empty
     */
    public InventoryItem removeItem(int slotIndex, int quantity) {
        if (slotIndex < 0 || slotIndex >= MAX_INVENTORY_SLOTS) {
            return null;
        }
        
        InventorySlot slot = inventorySlots[slotIndex];
        if (slot.isEmpty() || slot.isLocked()) {
            return null;
        }
        
        InventoryItem item = slot.getItem();
        
        if (item.isStackable() && quantity < item.getQuantity()) {
            // Remove partial stack
            InventoryItem removed = item.copy();
            removed.setQuantity(quantity);
            item.setQuantity(item.getQuantity() - quantity);
            
            updateWeight();
            System.out.println("Removed " + removed.getDisplayName() + " from slot " + slotIndex);
            return removed;
        } else {
            // Remove entire item
            slot.clear();
            categorySlots.get(item.getCategory()).remove(slot);
            updateWeight();
            
            System.out.println("Removed " + item.getDisplayName() + " from slot " + slotIndex);
            return item;
        }
    }
    
    /**
     * Use/consume an item
     * @param slotIndex Slot containing item to use
     * @return true if item was used successfully
     */
    public boolean useItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= MAX_INVENTORY_SLOTS) {
            return false;
        }
        
        InventorySlot slot = inventorySlots[slotIndex];
        if (slot.isEmpty()) {
            return false;
        }
        
        InventoryItem item = slot.getItem();
        
        // Perform item-specific use action
        boolean used = performItemUse(item);
        
        if (used) {
            totalItemsUsed++;
            
            if (item.isConsumable()) {
                // Remove one from stack or entire item
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    slot.clear();
                    categorySlots.get(item.getCategory()).remove(slot);
                }
                updateWeight();
            } else {
                // Non-consumable items may degrade with use
                item.setCondition(item.getCondition() - 0.01f);
                if (item.getCondition() <= 0.0f) {
                    slot.clear();
                    categorySlots.get(item.getCategory()).remove(slot);
                    updateWeight();
                    System.out.println(item.getDisplayName() + " broke from use!");
                }
            }
            
            System.out.println("Used " + item.getDisplayName());
            return true;
        }
        
        return false;
    }
    
    /**
     * Perform item-specific use action
     * @param item Item being used
     * @return true if use was successful
     */
    private boolean performItemUse(InventoryItem item) {
        switch (item.getId()) {
            case "match":
                System.out.println("🔥 Match lit! Provides light for 10 seconds.");
                // Would create a temporary light source
                return true;
                
            case "candle":
                System.out.println("🕯️ Candle lit! Place it somewhere for steady light.");
                // Would create a placeable light source
                return true;
                
            case "bandage":
                System.out.println("🩹 Applied bandage. Health restored.");
                // Would heal player
                return true;
                
            case "battery":
                // Find flashlight to recharge
                for (InventorySlot slot : inventorySlots) {
                    if (!slot.isEmpty() && "flashlight".equals(slot.getItem().getId())) {
                        System.out.println("🔋 Flashlight battery recharged!");
                        slot.getItem().setCondition(1.0f);
                        return true;
                    }
                }
                System.out.println("No flashlight found to recharge.");
                return false;
                
            case "lighter":
                System.out.println("🔥 Lighter used. Can light candles and torches.");
                // Would be used with other items
                return true;
                
            default:
                System.out.println("Cannot use " + item.getDisplayName());
                return false;
        }
    }
    
    // === Inventory Management ===
    
    /**
     * Try to stack item with existing items
     * @param newItem Item to stack
     * @return Remaining item that couldn't be stacked, or null if fully stacked
     */
    private InventoryItem tryStackWithExisting(InventoryItem newItem) {
        InventoryItem remaining = newItem.copy();
        
        for (InventorySlot slot : inventorySlots) {
            if (slot.isEmpty()) continue;
            
            InventoryItem existing = slot.getItem();
            if (existing.canStack(remaining)) {
                int added = existing.addToStack(remaining.getQuantity());
                remaining.setQuantity(remaining.getQuantity() - added);
                
                if (remaining.getQuantity() <= 0) {
                    return null; // Fully stacked
                }
            }
        }
        
        return remaining.getQuantity() > 0 ? remaining : null;
    }
    
    /**
     * Find empty slot, preferring category-appropriate slots
     * @param category Preferred category
     * @return Empty slot, or null if none available
     */
    private InventorySlot findEmptySlot(ItemCategory category) {
        // First try to find slot in appropriate category area
        int categoryStart = getCategoryStartSlot(category);
        int categoryEnd = Math.min(categoryStart + category.getMaxSlots(), MAX_INVENTORY_SLOTS);
        
        for (int i = categoryStart; i < categoryEnd; i++) {
            if (inventorySlots[i].isEmpty() && !inventorySlots[i].isLocked()) {
                return inventorySlots[i];
            }
        }
        
        // If no category slot available, find any empty slot
        for (InventorySlot slot : inventorySlots) {
            if (slot.isEmpty() && !slot.isLocked()) {
                return slot;
            }
        }
        
        return null;
    }
    
    /**
     * Get starting slot index for a category
     * @param category Item category
     * @return Starting slot index
     */
    private int getCategoryStartSlot(ItemCategory category) {
        int offset = 0;
        for (ItemCategory cat : ItemCategory.values()) {
            if (cat == category) break;
            offset += cat.getMaxSlots();
        }
        return Math.min(offset, MAX_INVENTORY_SLOTS - category.getMaxSlots());
    }
    
    /**
     * Sort inventory by category and item type
     */
    public void sortInventory() {
        List<InventoryItem> allItems = new ArrayList<>();
        
        // Collect all items
        for (InventorySlot slot : inventorySlots) {
            if (!slot.isEmpty()) {
                allItems.add(slot.getItem());
                slot.clear();
            }
        }
        
        // Clear category tracking
        for (List<InventorySlot> slots : categorySlots.values()) {
            slots.clear();
        }
        
        // Sort items by category and name
        allItems.sort((a, b) -> {
            int categoryCompare = a.getCategory().compareTo(b.getCategory());
            if (categoryCompare != 0) return categoryCompare;
            return a.getDisplayName().compareTo(b.getDisplayName());
        });
        
        // Place items back in organized slots
        int slotIndex = 0;
        for (InventoryItem item : allItems) {
            if (slotIndex < MAX_INVENTORY_SLOTS) {
                inventorySlots[slotIndex].setItem(item);
                categorySlots.get(item.getCategory()).add(inventorySlots[slotIndex]);
                slotIndex++;
            }
        }
        
        System.out.println("Inventory sorted");
    }
    
    /**
     * Update total weight calculation
     */
    private void updateWeight() {
        currentWeight = 0.0f;
        for (InventorySlot slot : inventorySlots) {
            if (!slot.isEmpty()) {
                currentWeight += slot.getItem().getTotalWeight();
            }
        }
        totalWeightCarried = Math.max(totalWeightCarried, currentWeight);
    }
    
    // === Hotbar Management ===
    
    /**
     * Set hotbar slot to point to inventory slot
     * @param hotbarIndex Hotbar slot index (0-7)
     * @param inventoryIndex Inventory slot index
     * @return true if successfully set
     */
    public boolean setHotbarSlot(int hotbarIndex, int inventoryIndex) {
        if (hotbarIndex < 0 || hotbarIndex >= HOTBAR_SLOTS ||
            inventoryIndex < 0 || inventoryIndex >= MAX_INVENTORY_SLOTS) {
            return false;
        }
        
        hotbarSlotIndices[hotbarIndex] = inventoryIndex;
        System.out.println("Hotbar slot " + hotbarIndex + " set to inventory slot " + inventoryIndex);
        return true;
    }
    
    /**
     * Get item in hotbar slot
     * @param hotbarIndex Hotbar slot index
     * @return Item in hotbar slot, or null if empty
     */
    public InventoryItem getHotbarItem(int hotbarIndex) {
        if (hotbarIndex < 0 || hotbarIndex >= HOTBAR_SLOTS) {
            return null;
        }
        
        int inventoryIndex = hotbarSlotIndices[hotbarIndex];
        if (inventoryIndex < 0 || inventoryIndex >= MAX_INVENTORY_SLOTS) {
            return null;
        }
        
        return inventorySlots[inventoryIndex].getItem();
    }
    
    /**
     * Use item in currently selected hotbar slot
     * @return true if item was used
     */
    public boolean useSelectedHotbarItem() {
        int inventoryIndex = hotbarSlotIndices[selectedHotbarSlot];
        return useItem(inventoryIndex);
    }
    
    /**
     * Select next hotbar slot
     */
    public void selectNextHotbarSlot() {
        selectedHotbarSlot = (selectedHotbarSlot + 1) % HOTBAR_SLOTS;
        System.out.println("Selected hotbar slot " + selectedHotbarSlot);
    }
    
    /**
     * Select previous hotbar slot
     */
    public void selectPreviousHotbarSlot() {
        selectedHotbarSlot = (selectedHotbarSlot - 1 + HOTBAR_SLOTS) % HOTBAR_SLOTS;
        System.out.println("Selected hotbar slot " + selectedHotbarSlot);
    }
    
    /**
     * Select specific hotbar slot
     * @param slot Slot index to select
     */
    public void selectHotbarSlot(int slot) {
        if (slot >= 0 && slot < HOTBAR_SLOTS) {
            selectedHotbarSlot = slot;
            System.out.println("Selected hotbar slot " + selectedHotbarSlot);
        }
    }
    
    // === Query Methods ===
    
    /**
     * Get item in specific slot
     * @param slotIndex Slot index
     * @return Item in slot, or null if empty
     */
    public InventoryItem getItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= MAX_INVENTORY_SLOTS) {
            return null;
        }
        return inventorySlots[slotIndex].getItem();
    }
    
    /**
     * Find first item of specific type
     * @param itemId Item ID to find
     * @return First matching item, or null if not found
     */
    public InventoryItem findItem(String itemId) {
        for (InventorySlot slot : inventorySlots) {
            if (!slot.isEmpty() && itemId.equals(slot.getItem().getId())) {
                return slot.getItem();
            }
        }
        return null;
    }
    
    /**
     * Count total quantity of specific item type
     * @param itemId Item ID to count
     * @return Total quantity
     */
    public int countItem(String itemId) {
        int total = 0;
        for (InventorySlot slot : inventorySlots) {
            if (!slot.isEmpty() && itemId.equals(slot.getItem().getId())) {
                total += slot.getItem().getQuantity();
            }
        }
        return total;
    }
    
    /**
     * Get all items in specific category
     * @param category Category to search
     * @return List of items in category
     */
    public List<InventoryItem> getItemsByCategory(ItemCategory category) {
        List<InventoryItem> items = new ArrayList<>();
        for (InventorySlot slot : categorySlots.get(category)) {
            if (!slot.isEmpty()) {
                items.add(slot.getItem());
            }
        }
        return items;
    }
    
    /**
     * Check if inventory has space for item
     * @param item Item to check
     * @return true if item can be added
     */
    public boolean hasSpaceFor(InventoryItem item) {
        if (!allowOverweight && currentWeight + item.getTotalWeight() > MAX_WEIGHT_CAPACITY) {
            return false;
        }
        
        if (autoStack && item.isStackable()) {
            // Check if can stack with existing items
            for (InventorySlot slot : inventorySlots) {
                if (!slot.isEmpty() && slot.getItem().canStack(item)) {
                    int canAdd = slot.getItem().getMaxStackSize() - slot.getItem().getQuantity();
                    if (canAdd >= item.getQuantity()) {
                        return true;
                    }
                }
            }
        }
        
        // Check for empty slots
        return findEmptySlot(item.getCategory()) != null;
    }
    
    // === Status and Information ===
    
    /**
     * Get current weight percentage
     * @return Weight as percentage of capacity (0.0 to 1.0+)
     */
    public float getWeightPercentage() {
        return currentWeight / MAX_WEIGHT_CAPACITY;
    }
    
    /**
     * Get number of empty slots
     * @return Number of empty slots
     */
    public int getEmptySlots() {
        int empty = 0;
        for (InventorySlot slot : inventorySlots) {
            if (slot.isEmpty()) empty++;
        }
        return empty;
    }
    
    /**
     * Get inventory summary
     * @return Summary string
     */
    public String getInventorySummary() {
        int totalItems = 0;
        for (InventorySlot slot : inventorySlots) {
            if (!slot.isEmpty()) {
                totalItems += slot.getItem().getQuantity();
            }
        }
        
        return String.format(
            "Inventory: %d/%d slots | %d items | %.1f/%.1f kg (%.0f%%)",
            MAX_INVENTORY_SLOTS - getEmptySlots(),
            MAX_INVENTORY_SLOTS,
            totalItems,
            currentWeight,
            MAX_WEIGHT_CAPACITY,
            getWeightPercentage() * 100
        );
    }
    
    /**
     * Get detailed status report
     * @return Detailed status string
     */
    public String getStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY STATUS ===\n");
        sb.append(getInventorySummary()).append("\n");
        sb.append("Selected Hotbar Slot: ").append(selectedHotbarSlot).append("\n");
        sb.append("Total Items Collected: ").append(totalItemsCollected).append("\n");
        sb.append("Total Items Used: ").append(totalItemsUsed).append("\n");
        sb.append("Max Weight Carried: ").append(String.format("%.1f", totalWeightCarried)).append(" kg\n");
        
        sb.append("\nItems by Category:\n");
        for (ItemCategory category : ItemCategory.values()) {
            List<InventoryItem> items = getItemsByCategory(category);
            if (!items.isEmpty()) {
                sb.append("  ").append(category.getDisplayName()).append(": ").append(items.size()).append(" types\n");
                for (InventoryItem item : items) {
                    sb.append("    - ").append(item.toString()).append("\n");
                }
            }
        }
        
        return sb.toString();
    }
    
    // === Settings ===
    
    public boolean isAutoSort() { return autoSort; }
    public void setAutoSort(boolean autoSort) { this.autoSort = autoSort; }
    
    public boolean isAutoStack() { return autoStack; }
    public void setAutoStack(boolean autoStack) { this.autoStack = autoStack; }
    
    public boolean isAllowOverweight() { return allowOverweight; }
    public void setAllowOverweight(boolean allowOverweight) { this.allowOverweight = allowOverweight; }
    
    public int getSelectedHotbarSlot() { return selectedHotbarSlot; }
    public float getCurrentWeight() { return currentWeight; }
    
    // === Utility Methods ===
    
    /**
     * Clear entire inventory
     */
    public void clearInventory() {
        for (InventorySlot slot : inventorySlots) {
            slot.clear();
        }
        for (List<InventorySlot> slots : categorySlots.values()) {
            slots.clear();
        }
        currentWeight = 0.0f;
        System.out.println("Inventory cleared");
    }
    
    @Override
    public String toString() {
        return String.format("InventorySystem{slots=%d/%d, weight=%.1f/%.1f, selected=%d}", 
                           MAX_INVENTORY_SLOTS - getEmptySlots(), MAX_INVENTORY_SLOTS,
                           currentWeight, MAX_WEIGHT_CAPACITY, selectedHotbarSlot);
    }
}