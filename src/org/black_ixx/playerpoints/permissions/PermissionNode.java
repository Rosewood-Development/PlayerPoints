package org.black_ixx.playerpoints.permissions;

/**
 * Enumeration of permission nodes used through the plugin. Allows for a
 * centralized location and combats typos.
 * 
 * @author Mitsugaru
 * 
 */
public enum PermissionNode {

    GIVE(".give"),
    GIVEALL(".giveall"),
    TAKE(".take"),
    LOOK(".look"),
    PAY(".pay"),
    SET(".set"),
    RESET(".reset"),
    ME(".me"),
    LEAD(".lead"),
    RELOAD(".reload"),
    BROADCAST(".broadcast");

    /**
     * Permission prefix.
     */
    private static final String prefix = "PlayerPoints";

    /**
     * Individual node path.
     */
    private String node;

    /**
     * Private constructor.
     * 
     * @param node
     *            - Specific node.
     */
    PermissionNode(String node) {
        this.node = prefix + node;
    }

    /**
     * Get the full permission node path.
     * 
     * @return Permission string.
     */
    public String getNode() {
        return node;
    }
}