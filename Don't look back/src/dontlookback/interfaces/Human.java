package dontlookback.interfaces;

/**
 * Human interface extending Biped with human-specific functionality
 * 
 * Defines the contract for human characters in the game,
 * adding human-specific capabilities to the basic biped interface.
 */
public interface Human extends Biped {

    /**
     * Get debuffs currently affecting this human
     * @return array of debuff identifiers
     */
    int[] deBuffs();
}