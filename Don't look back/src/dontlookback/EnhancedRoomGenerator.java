package dontlookback;

import java.util.*;

/**
 * Enhanced Room Generation System for Don't Look Back
 * 
 * Implements the core mechanic where rooms regenerate after not being
 * observed for 5+ seconds. Includes door systems, room persistence,
 * and dynamic environment generation.
 * 
 * Features:
 * - Procedural room generation based on player view
 * - 5-second regeneration timer when not observed
 * - Door system with random connections
 * - Room persistence and state management
 * - Environmental storytelling placement
 * - Multiple room types and themes
 * 
 * @author DLB Team
 * @version 1.0
 */
public class EnhancedRoomGenerator {
    
    // === Room Generation Constants ===
    
    /** Time before room regenerates when not observed (seconds) */
    private static final double REGENERATION_TIME = 5.0;
    
    /** Maximum distance for room generation */
    private static final float MAX_VIEW_DISTANCE = 25.0f;
    
    /** Grid spacing for room placement */
    private static final float ROOM_SPACING = 10.0f;
    
    /** Maximum number of active rooms */
    private static final int MAX_ACTIVE_ROOMS = 50;
    
    /** Probability of door generation between rooms */
    private static final float DOOR_PROBABILITY = 0.3f;
    
    // === Enhanced Room Types ===
    
    public enum EnhancedRoomType {
        EMPTY("Empty Room", 0.4f, false, false),
        STORAGE("Storage Room", 0.2f, true, false),
        LIBRARY("Library", 0.1f, true, true),
        KITCHEN("Kitchen", 0.1f, true, false),
        BEDROOM("Bedroom", 0.1f, false, true),
        BATHROOM("Bathroom", 0.05f, false, false),
        BASEMENT("Basement", 0.03f, true, false),
        ATTIC("Attic", 0.02f, true, true),
        SAFE_ROOM("Safe Room", 0.01f, true, true);
        
        private final String displayName;
        private final float spawnProbability;
        private final boolean hasItems;
        private final boolean hasDocuments;
        
        EnhancedRoomType(String displayName, float spawnProbability, boolean hasItems, boolean hasDocuments) {
            this.displayName = displayName;
            this.spawnProbability = spawnProbability;
            this.hasItems = hasItems;
            this.hasDocuments = hasDocuments;
        }
        
        public String getDisplayName() { return displayName; }
        public float getSpawnProbability() { return spawnProbability; }
        public boolean hasItems() { return hasItems; }
        public boolean hasDocuments() { return hasDocuments; }
    }
    
    // === Room Themes ===
    
    public enum RoomTheme {
        MODERN("Modern", 0.4f),
        VICTORIAN("Victorian", 0.25f),
        INDUSTRIAL("Industrial", 0.2f),
        ABANDONED("Abandoned", 0.1f),
        MEDICAL("Medical", 0.05f);
        
        private final String displayName;
        private final float probability;
        
        RoomTheme(String displayName, float probability) {
            this.displayName = displayName;
            this.probability = probability;
        }
        
        public String getDisplayName() { return displayName; }
        public float getProbability() { return probability; }
    }
    
    // === Enhanced Room Class ===
    
    public static class EnhancedRoom extends Room {
        
        // Room state tracking
        private double lastObservedTime;
        private boolean isCurrentlyObserved;
        private boolean needsRegeneration;
        private int regenerationCount;
        
        // === Room properties
        private EnhancedRoomType roomType;  // Using our enhanced room type
        private RoomTheme roomTheme;
        private Set<Door> doors;
        private List<String> containedItems;
        private List<String> documents;
        private Map<String, Object> environmentalFeatures;
        
        // Room persistence
        private String roomSeed; // For consistent regeneration
        private boolean isPlayerOccupied;
        private float[] lastKnownPlayerPosition;
        
        public EnhancedRoom(int id, float[] position) {
            super(id, RoomType.SMALL_ROOM, position);
            
            this.lastObservedTime = getCurrentTime();
            this.isCurrentlyObserved = false;
            this.needsRegeneration = false;
            this.regenerationCount = 0;
            
            this.doors = new HashSet<>();
            this.containedItems = new ArrayList<>();
            this.documents = new ArrayList<>();
            this.environmentalFeatures = new HashMap<>();
            
            this.roomSeed = generateRoomSeed(position);
            this.isPlayerOccupied = false;
            this.lastKnownPlayerPosition = new float[]{0, 0, 0};
            
            // Generate initial room content
            generateRoomContent();
        }
        
        // === Room Generation ===
        
        /**
         * Generate room content based on type and theme
         */
        private void generateRoomContent() {
            // Select room type based on probabilities
            roomType = selectRoomType();
            roomTheme = selectRoomTheme();
            
            System.out.println("Generated " + roomTheme.getDisplayName() + " " + 
                             roomType.getDisplayName() + " at " + Arrays.toString(getPosition()));
            
            // Generate items based on room type
            if (roomType.hasItems()) {
                generateItems();
            }
            
            // Generate documents based on room type
            if (roomType.hasDocuments()) {
                generateDocuments();
            }
            
            // Generate environmental features
            generateEnvironmentalFeatures();
            
            // Generate doors to adjacent positions
            generateDoors();
        }
        
        /**
         * Select room type based on weighted probabilities
         */
        private EnhancedRoomType selectRoomType() {
            float random = (float) Math.random();
            float cumulative = 0.0f;
            
            for (EnhancedRoomType type : EnhancedRoomType.values()) {
                cumulative += type.getSpawnProbability();
                if (random <= cumulative) {
                    return type;
                }
            }
            
            return EnhancedRoomType.EMPTY; // Fallback
        }
        
        /**
         * Select room theme based on weighted probabilities
         */
        private RoomTheme selectRoomTheme() {
            float random = (float) Math.random();
            float cumulative = 0.0f;
            
            for (RoomTheme theme : RoomTheme.values()) {
                cumulative += theme.getProbability();
                if (random <= cumulative) {
                    return theme;
                }
            }
            
            return RoomTheme.MODERN; // Fallback
        }
        
        /**
         * Generate items for this room
         */
        private void generateItems() {
            Random rand = new Random(roomSeed.hashCode());
            int itemCount = 1 + rand.nextInt(4); // 1-4 items
            
            Set<String> possibleItems = getPossibleItemsForRoomType(roomType);
            
            for (int i = 0; i < itemCount && !possibleItems.isEmpty(); i++) {
                String[] itemArray = possibleItems.toArray(new String[0]);
                String item = itemArray[rand.nextInt(itemArray.length)];
                containedItems.add(item);
                possibleItems.remove(item); // Don't duplicate
            }
            
            if (!containedItems.isEmpty()) {
                System.out.println("  Items: " + containedItems);
            }
        }
        
        /**
         * Get possible items for room type
         */
        private Set<String> getPossibleItemsForRoomType(EnhancedRoomType type) {
            Set<String> items = new HashSet<>();
            
            switch (type) {
                case STORAGE:
                    items.addAll(Arrays.asList("candle", "torch", "battery", "oil", "bandage"));
                    break;
                case LIBRARY:
                    items.addAll(Arrays.asList("candle", "lantern", "lighter"));
                    break;
                case KITCHEN:
                    items.addAll(Arrays.asList("match", "candle", "lighter"));
                    break;
                case BEDROOM:
                    items.addAll(Arrays.asList("flashlight", "battery", "bandage"));
                    break;
                case BASEMENT:
                    items.addAll(Arrays.asList("torch", "lantern", "crowbar", "wire_cutters"));
                    break;
                case ATTIC:
                    items.addAll(Arrays.asList("lantern", "candle", "coin", "photograph"));
                    break;
                case SAFE_ROOM:
                    items.addAll(Arrays.asList("flashlight", "battery", "bandage", "oil", "candle"));
                    break;
                default:
                    // Empty rooms might have basic items
                    if (Math.random() < 0.3) {
                        items.addAll(Arrays.asList("match", "coin"));
                    }
                    break;
            }
            
            return items;
        }
        
        /**
         * Generate documents and notes
         */
        private void generateDocuments() {
            Random rand = new Random(roomSeed.hashCode() + 1);
            int docCount = 1 + rand.nextInt(3); // 1-3 documents
            
            Set<String> possibleDocs = getPossibleDocumentsForRoomType(roomType);
            
            for (int i = 0; i < docCount && !possibleDocs.isEmpty(); i++) {
                String[] docArray = possibleDocs.toArray(new String[0]);
                String doc = docArray[rand.nextInt(docArray.length)];
                documents.add(doc);
                possibleDocs.remove(doc);
            }
            
            if (!documents.isEmpty()) {
                System.out.println("  Documents: " + documents);
            }
        }
        
        /**
         * Get possible documents for room type
         */
        private Set<String> getPossibleDocumentsForRoomType(EnhancedRoomType type) {
            Set<String> docs = new HashSet<>();
            
            switch (type) {
                case LIBRARY:
                    docs.addAll(Arrays.asList("journal", "map", "note"));
                    break;
                case BEDROOM:
                    docs.addAll(Arrays.asList("note", "photograph", "journal"));
                    break;
                case ATTIC:
                    docs.addAll(Arrays.asList("photograph", "note", "map"));
                    break;
                case SAFE_ROOM:
                    docs.addAll(Arrays.asList("map", "journal"));
                    break;
                default:
                    if (Math.random() < 0.2) {
                        docs.add("note");
                    }
                    break;
            }
            
            return docs;
        }
        
        /**
         * Generate environmental features
         */
        private void generateEnvironmentalFeatures() {
            Random rand = new Random(roomSeed.hashCode() + 2);
            
            // Add theme-specific features
            switch (roomTheme) {
                case VICTORIAN:
                    environmentalFeatures.put("wallpaper", "faded_floral");
                    environmentalFeatures.put("furniture", "antique");
                    if (rand.nextFloat() < 0.5f) {
                        environmentalFeatures.put("fireplace", true);
                    }
                    break;
                    
                case INDUSTRIAL:
                    environmentalFeatures.put("walls", "concrete");
                    environmentalFeatures.put("lighting", "fluorescent");
                    if (rand.nextFloat() < 0.3f) {
                        environmentalFeatures.put("machinery", "industrial");
                    }
                    break;
                    
                case ABANDONED:
                    environmentalFeatures.put("condition", "deteriorated");
                    environmentalFeatures.put("debris", true);
                    if (rand.nextFloat() < 0.7f) {
                        environmentalFeatures.put("damage", "water_damage");
                    }
                    break;
                    
                case MEDICAL:
                    environmentalFeatures.put("walls", "sterile_white");
                    environmentalFeatures.put("equipment", "medical");
                    environmentalFeatures.put("smell", "antiseptic");
                    break;
                    
                default: // MODERN
                    environmentalFeatures.put("style", "contemporary");
                    environmentalFeatures.put("lighting", "led");
                    break;
            }
            
            // Add room-specific features
            switch (roomType) {
                case LIBRARY:
                    environmentalFeatures.put("bookshelves", true);
                    environmentalFeatures.put("reading_area", true);
                    break;
                case KITCHEN:
                    environmentalFeatures.put("appliances", true);
                    environmentalFeatures.put("counters", true);
                    break;
                case BEDROOM:
                    environmentalFeatures.put("bed", true);
                    environmentalFeatures.put("closet", true);
                    break;
                case SAFE_ROOM:
                    environmentalFeatures.put("reinforced_walls", true);
                    environmentalFeatures.put("security_door", true);
                    environmentalFeatures.put("emergency_supplies", true);
                    break;
            }
        }
        
        /**
         * Generate doors to adjacent rooms
         */
        private void generateDoors() {
            Random rand = new Random(roomSeed.hashCode() + 3);
            
            // Potential door directions (North, South, East, West)
            float[][] doorDirections = {
                {0, 0, ROOM_SPACING},  // North
                {0, 0, -ROOM_SPACING}, // South
                {ROOM_SPACING, 0, 0},  // East
                {-ROOM_SPACING, 0, 0}  // West
            };
            
            String[] doorNames = {"North Door", "South Door", "East Door", "West Door"};
            
            for (int i = 0; i < doorDirections.length; i++) {
                if (rand.nextFloat() < DOOR_PROBABILITY) {
                    float[] position = getPosition();
                    float[] doorPos = {
                        position[0] + doorDirections[i][0] / 2,
                        position[1],
                        position[2] + doorDirections[i][2] / 2
                    };
                    
                    float[] targetPos = {
                        position[0] + doorDirections[i][0],
                        position[1] + doorDirections[i][1],
                        position[2] + doorDirections[i][2]
                    };
                    
                    Door door = new Door(doorNames[i], doorPos, targetPos);
                    doors.add(door);
                }
            }
            
            if (!doors.isEmpty()) {
                System.out.println("  Doors: " + doors.size() + " connections");
            }
        }
        
        // === Room State Management ===
        
        /**
         * Update room observation status
         * @param isObserved Whether player is currently observing this room
         */
        public void updateObservation(boolean isObserved) {
            if (isObserved && !isCurrentlyObserved) {
                // Just started being observed
                lastObservedTime = getCurrentTime();
                needsRegeneration = false;
                System.out.println("Room " + getId() + " being observed");
            }
            
            isCurrentlyObserved = isObserved;
            
            if (isObserved) {
                lastObservedTime = getCurrentTime();
            }
        }
        
        /**
         * Update room state
         * @param deltaTime Time since last update
         */
        public void update(double deltaTime) {
            if (!isCurrentlyObserved) {
                double timeSinceLastObserved = getCurrentTime() - lastObservedTime;
                
                if (timeSinceLastObserved >= REGENERATION_TIME && !needsRegeneration) {
                    markForRegeneration();
                }
            }
        }
        
        /**
         * Mark room for regeneration
         */
        private void markForRegeneration() {
            if (!isPlayerOccupied) {
                needsRegeneration = true;
                System.out.println("Room " + getId() + " marked for regeneration after " + 
                                 String.format("%.1f", getCurrentTime() - lastObservedTime) + " seconds");
            }
        }
        
        /**
         * Regenerate room content
         */
        public void regenerate() {
            if (!needsRegeneration) return;
            
            regenerationCount++;
            
            // Clear old content
            doors.clear();
            containedItems.clear();
            documents.clear();
            environmentalFeatures.clear();
            
            // Generate new seed for variation
            roomSeed = generateRoomSeed(getPosition()) + "_" + regenerationCount;
            
            // Regenerate content
            generateRoomContent();
            
            needsRegeneration = false;
            lastObservedTime = getCurrentTime();
            
            System.out.println("Room " + getId() + " regenerated (count: " + regenerationCount + ")");
        }
        
        // === Player Interaction ===
        
        /**
         * Handle player entering room
         * @param playerPosition Player's position when entering
         */
        public void onPlayerEnter(float[] playerPosition) {
            isPlayerOccupied = true;
            lastKnownPlayerPosition = playerPosition.clone();
            updateObservation(true);
            
            System.out.println("Player entered " + roomType.getDisplayName() + 
                             " (Theme: " + roomTheme.getDisplayName() + ")");
            
            // Display room contents to player
            displayRoomContents();
        }
        
        /**
         * Handle player leaving room
         */
        public void onPlayerExit() {
            isPlayerOccupied = false;
            System.out.println("Player left " + roomType.getDisplayName());
        }
        
        /**
         * Display room contents to player
         */
        private void displayRoomContents() {
            System.out.println("📍 You are in a " + roomTheme.getDisplayName() + " " + 
                             roomType.getDisplayName());
            
            if (!environmentalFeatures.isEmpty()) {
                System.out.println("🏠 You notice: " + getEnvironmentalDescription());
            }
            
            if (!containedItems.isEmpty()) {
                System.out.println("📦 Items you can see: " + String.join(", ", containedItems));
            }
            
            if (!documents.isEmpty()) {
                System.out.println("📄 Documents here: " + String.join(", ", documents));
            }
            
            if (!doors.isEmpty()) {
                System.out.println("🚪 " + doors.size() + " door(s) lead to other areas");
                for (Door door : doors) {
                    System.out.println("  - " + door.getName());
                }
            }
        }
        
        /**
         * Get environmental description
         */
        private String getEnvironmentalDescription() {
            List<String> descriptions = new ArrayList<>();
            
            for (Map.Entry<String, Object> feature : environmentalFeatures.entrySet()) {
                String key = feature.getKey();
                Object value = feature.getValue();
                
                switch (key) {
                    case "fireplace":
                        if ((Boolean) value) descriptions.add("a warm fireplace");
                        break;
                    case "bookshelves":
                        if ((Boolean) value) descriptions.add("tall bookshelves");
                        break;
                    case "debris":
                        if ((Boolean) value) descriptions.add("scattered debris");
                        break;
                    case "damage":
                        descriptions.add(value.toString().replace("_", " "));
                        break;
                    case "walls":
                        descriptions.add(value.toString().replace("_", " ") + " walls");
                        break;
                    case "style":
                        descriptions.add(value.toString() + " styling");
                        break;
                }
            }
            
            return String.join(", ", descriptions);
        }
        
        // === Item Interaction ===
        
        /**
         * Collect item from room
         * @param itemId Item to collect
         * @return true if item was collected
         */
        public boolean collectItem(String itemId) {
            if (containedItems.remove(itemId)) {
                System.out.println("Collected " + itemId + " from " + roomType.getDisplayName());
                return true;
            }
            return false;
        }
        
        /**
         * Read document in room
         * @param documentId Document to read
         * @return document content or null if not found
         */
        public String readDocument(String documentId) {
            if (documents.contains(documentId)) {
                System.out.println("Reading " + documentId + " in " + roomType.getDisplayName());
                return generateDocumentContent(documentId);
            }
            return null;
        }
        
        /**
         * Generate document content based on type and room
         */
        private String generateDocumentContent(String documentType) {
            Random rand = new Random(roomSeed.hashCode() + documentType.hashCode());
            
            switch (documentType) {
                case "note":
                    return generateNote(rand);
                case "journal":
                    return generateJournalEntry(rand);
                case "map":
                    return generateMapInfo(rand);
                case "photograph":
                    return generatePhotographDescription(rand);
                default:
                    return "A " + documentType + " with faded text.";
            }
        }
        
        private String generateNote(Random rand) {
            String[] noteTemplates = {
                "Remember to check the %s before leaving.",
                "The %s is acting strange again. Need to investigate.",
                "Found strange noises coming from the %s.",
                "DO NOT go to the %s after dark!",
                "The %s door was left open again. Something's not right."
            };
            
            String[] locations = {"basement", "attic", "kitchen", "library", "storage room"};
            
            String template = noteTemplates[rand.nextInt(noteTemplates.length)];
            String location = locations[rand.nextInt(locations.length)];
            
            return String.format(template, location);
        }
        
        private String generateJournalEntry(Random rand) {
            String[] entries = {
                "Day " + (rand.nextInt(100) + 1) + ": The shadows seem longer today. I can feel something watching.",
                "I keep hearing footsteps in the empty rooms. When I check, there's nothing there.",
                "The lights keep flickering. I've replaced the bulbs twice this week.",
                "Something moved in my peripheral vision again. I'm starting to think I'm not alone here.",
                "Found another door today that wasn't there yesterday. This place is changing."
            };
            
            return entries[rand.nextInt(entries.length)];
        }
        
        private String generateMapInfo(Random rand) {
            return "A hand-drawn map showing the layout of nearby rooms. Some areas are marked with warning symbols.";
        }
        
        private String generatePhotographDescription(Random rand) {
            String[] descriptions = {
                "An old photograph showing people standing in front of this building. They look uneasy.",
                "A family portrait. One of the faces has been scratched out.",
                "A photograph of an empty room that looks exactly like this one, but something seems different.",
                "A polaroid showing the same room you're in now, but taken from an impossible angle."
            };
            
            return descriptions[rand.nextInt(descriptions.length)];
        }
        
        // === Utility Methods ===
        
        /**
         * Generate consistent room seed based on position
         */
        private String generateRoomSeed(float[] position) {
            return String.format("%.1f_%.1f_%.1f", position[0], position[1], position[2]);
        }
        
        /**
         * Get current time in seconds
         */
        private double getCurrentTime() {
            return System.currentTimeMillis() / 1000.0;
        }
        
        // === Getters ===
        
        public int getId() { return getRoomId(); }
        public float[] getPosition() { return getCenter(); }
        public EnhancedRoomType getRoomType() { return roomType; }
        public RoomTheme getRoomTheme() { return roomTheme; }
        public Set<Door> getDoors() { return new HashSet<>(doors); }
        public List<String> getContainedItems() { return new ArrayList<>(containedItems); }
        public List<String> getDocuments() { return new ArrayList<>(documents); }
        public boolean needsRegeneration() { return needsRegeneration; }
        public boolean isPlayerOccupied() { return isPlayerOccupied; }
        public int getRegenerationCount() { return regenerationCount; }
        public double getLastObservedTime() { return lastObservedTime; }
        public boolean isCurrentlyObserved() { return isCurrentlyObserved; }
        
        @Override
        public String toString() {
            return String.format("EnhancedRoom{id=%d, type=%s, theme=%s, observed=%s, regen=%d, items=%d, doors=%d}", 
                                getId(), roomType, roomTheme, isCurrentlyObserved, 
                                regenerationCount, containedItems.size(), doors.size());
        }
    }
    
    // === Door System ===
    
    public static class Door {
        private final String name;
        private final float[] position;
        private final float[] targetPosition;
        private boolean isOpen;
        private boolean isLocked;
        private String requiredKey;
        
        public Door(String name, float[] position, float[] targetPosition) {
            this.name = name;
            this.position = position.clone();
            this.targetPosition = targetPosition.clone();
            this.isOpen = false;
            this.isLocked = false;
            this.requiredKey = null;
        }
        
        public String getName() { return name; }
        public float[] getPosition() { return position.clone(); }
        public float[] getTargetPosition() { return targetPosition.clone(); }
        public boolean isOpen() { return isOpen; }
        public boolean isLocked() { return isLocked; }
        public String getRequiredKey() { return requiredKey; }
        
        public void setOpen(boolean open) { this.isOpen = open; }
        public void setLocked(boolean locked) { this.isLocked = locked; }
        public void setRequiredKey(String keyId) { this.requiredKey = keyId; }
        
        @Override
        public String toString() {
            return String.format("Door{%s, open=%s, locked=%s}", name, isOpen, isLocked);
        }
    }
    
    // === Room Generator State ===
    
    private final Map<String, EnhancedRoom> activeRooms;
    private final Queue<EnhancedRoom> roomsToRegenerate;
    private final Random random;
    private float[] playerPosition;
    private float[] playerViewDirection;
    private int nextRoomId;
    
    /**
     * Create enhanced room generator
     */
    public EnhancedRoomGenerator() {
        this.activeRooms = new HashMap<>();
        this.roomsToRegenerate = new ArrayDeque<>();
        this.random = new Random();
        this.playerPosition = new float[]{0, 0, 0};
        this.playerViewDirection = new float[]{0, 0, 1}; // Looking forward
        this.nextRoomId = 1;
        
        System.out.println("Enhanced Room Generator initialized");
    }
    
    // === Main Generator Methods ===
    
    /**
     * Update room generation based on player position and view
     * @param playerPos Player position [x, y, z]
     * @param viewDirection Player view direction [x, y, z]
     * @param deltaTime Time since last update
     */
    public void update(float[] playerPos, float[] viewDirection, double deltaTime) {
        System.arraycopy(playerPos, 0, playerPosition, 0, 3);
        System.arraycopy(viewDirection, 0, playerViewDirection, 0, 3);
        
        // Update existing rooms
        updateExistingRooms(deltaTime);
        
        // Generate new rooms in view
        generateRoomsInView();
        
        // Process room regenerations
        processRoomRegenerations();
        
        // Clean up distant rooms
        cleanupDistantRooms();
    }
    
    /**
     * Update all existing rooms
     */
    private void updateExistingRooms(double deltaTime) {
        for (EnhancedRoom room : activeRooms.values()) {
            // Update observation status
            boolean inView = isRoomInView(room);
            room.updateObservation(inView);
            
            // Update room state
            room.update(deltaTime);
            
            // Queue for regeneration if needed
            if (room.needsRegeneration()) {
                roomsToRegenerate.offer(room);
            }
        }
    }
    
    /**
     * Generate rooms in player's view
     */
    private void generateRoomsInView() {
        List<float[]> potentialPositions = generatePotentialRoomPositions();
        
        for (float[] position : potentialPositions) {
            String posKey = positionToKey(position);
            
            if (!activeRooms.containsKey(posKey) && 
                activeRooms.size() < MAX_ACTIVE_ROOMS &&
                isPositionInView(position)) {
                
                EnhancedRoom newRoom = new EnhancedRoom(nextRoomId++, position);
                activeRooms.put(posKey, newRoom);
                newRoom.updateObservation(true);
                
                System.out.println("Generated new room at " + Arrays.toString(position));
            }
        }
    }
    
    /**
     * Process room regenerations
     */
    private void processRoomRegenerations() {
        while (!roomsToRegenerate.isEmpty()) {
            EnhancedRoom room = roomsToRegenerate.poll();
            if (room.needsRegeneration()) {
                room.regenerate();
            }
        }
    }
    
    /**
     * Clean up rooms that are too far away
     */
    private void cleanupDistantRooms() {
        Iterator<Map.Entry<String, EnhancedRoom>> iterator = activeRooms.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, EnhancedRoom> entry = iterator.next();
            EnhancedRoom room = entry.getValue();
            
            float distance = calculateDistance(playerPosition, room.getPosition());
            
            if (distance > MAX_VIEW_DISTANCE * 1.5f && !room.isPlayerOccupied()) {
                iterator.remove();
                System.out.println("Removed distant room " + room.getId());
            }
        }
    }
    
    // === Utility Methods ===
    
    /**
     * Generate potential room positions around player
     */
    private List<float[]> generatePotentialRoomPositions() {
        List<float[]> positions = new ArrayList<>();
        
        int gridRadius = (int) (MAX_VIEW_DISTANCE / ROOM_SPACING) + 1;
        
        for (int x = -gridRadius; x <= gridRadius; x++) {
            for (int z = -gridRadius; z <= gridRadius; z++) {
                float roomX = playerPosition[0] + (x * ROOM_SPACING);
                float roomY = playerPosition[1];
                float roomZ = playerPosition[2] + (z * ROOM_SPACING);
                
                // Snap to grid
                roomX = Math.round(roomX / ROOM_SPACING) * ROOM_SPACING;
                roomZ = Math.round(roomZ / ROOM_SPACING) * ROOM_SPACING;
                
                positions.add(new float[]{roomX, roomY, roomZ});
            }
        }
        
        return positions;
    }
    
    /**
     * Check if room is in player's view
     */
    private boolean isRoomInView(EnhancedRoom room) {
        return isPositionInView(room.getPosition());
    }
    
    /**
     * Check if position is in player's view
     */
    private boolean isPositionInView(float[] position) {
        float distance = calculateDistance(playerPosition, position);
        
        if (distance > MAX_VIEW_DISTANCE) {
            return false;
        }
        
        // Simple view frustum check
        float[] directionToRoom = {
            position[0] - playerPosition[0],
            position[1] - playerPosition[1], 
            position[2] - playerPosition[2]
        };
        
        // Normalize
        float length = (float) Math.sqrt(
            directionToRoom[0] * directionToRoom[0] +
            directionToRoom[1] * directionToRoom[1] +
            directionToRoom[2] * directionToRoom[2]
        );
        
        if (length == 0) return true; // Same position
        
        directionToRoom[0] /= length;
        directionToRoom[1] /= length;
        directionToRoom[2] /= length;
        
        // Dot product with view direction
        float dot = directionToRoom[0] * playerViewDirection[0] +
                   directionToRoom[1] * playerViewDirection[1] +
                   directionToRoom[2] * playerViewDirection[2];
        
        // Wide view cone (120 degrees)
        return dot > -0.5f;
    }
    
    /**
     * Calculate distance between two positions
     */
    private float calculateDistance(float[] pos1, float[] pos2) {
        float dx = pos1[0] - pos2[0];
        float dy = pos1[1] - pos2[1];
        float dz = pos1[2] - pos2[2];
        
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Convert position to string key
     */
    private String positionToKey(float[] position) {
        return String.format("%.1f_%.1f_%.1f", position[0], position[1], position[2]);
    }
    
    // === Public Interface ===
    
    /**
     * Get room at specific position
     */
    public EnhancedRoom getRoomAt(float[] position) {
        String key = positionToKey(position);
        return activeRooms.get(key);
    }
    
    /**
     * Get all active rooms
     */
    public Collection<EnhancedRoom> getActiveRooms() {
        return activeRooms.values();
    }
    
    /**
     * Handle player entering room
     */
    public void onPlayerEnterRoom(float[] position) {
        EnhancedRoom room = getRoomAt(position);
        if (room != null) {
            room.onPlayerEnter(position);
        }
    }
    
    /**
     * Handle player leaving room
     */
    public void onPlayerExitRoom(float[] position) {
        EnhancedRoom room = getRoomAt(position);
        if (room != null) {
            room.onPlayerExit();
        }
    }
    
    /**
     * Get status report
     */
    public String getStatusReport() {
        int totalRooms = activeRooms.size();
        int observedRooms = (int) activeRooms.values().stream()
            .mapToLong(room -> room.isCurrentlyObserved() ? 1 : 0).sum();
        int roomsPendingRegen = roomsToRegenerate.size();
        
        return String.format(
            "Enhanced Room Generator:\n" +
            "  Active Rooms: %d/%d\n" +
            "  Currently Observed: %d\n" +
            "  Pending Regeneration: %d\n" +
            "  Player Position: [%.1f, %.1f, %.1f]",
            totalRooms, MAX_ACTIVE_ROOMS,
            observedRooms,
            roomsPendingRegen,
            playerPosition[0], playerPosition[1], playerPosition[2]
        );
    }
    
    @Override
    public String toString() {
        return String.format("EnhancedRoomGenerator{rooms=%d, pending=%d}", 
                           activeRooms.size(), roomsToRegenerate.size());
    }
}