package org.black_ixx.playerpoints.models;

/**
 * Variable flags.
 * 
 * @author Mitsugaru
 */
public enum Flag {
    NAME("%name"),
    TAG("%tag"),
    PLAYER("%player"),
    EXTRA("%extra"),
    AMOUNT("%amount");

    /**
     * Flag field.
     */
    private final String flag;

    /**
     * Private constructor.
     * 
     * @param flag
     *            - Field to use.
     */
    Flag(String flag) {
        this.flag = flag;
    }

    /**
     * Get the flag.
     * 
     * @return Flag.
     */
    public String getFlag() {
        return flag;
    }
}