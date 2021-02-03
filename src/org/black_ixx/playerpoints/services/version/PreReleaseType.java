package org.black_ixx.playerpoints.services.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents pre-release version types.
 * 
 * @author Mitsugaru
 */
public class PreReleaseType implements Comparable<PreReleaseType> {

    /**
     * Empty prerelease type.
     */
    public static final PreReleaseType NONE = new PreReleaseType("");

    /**
     * Normal string found in version.
     */
    private final String type;

    /**
     * Base pre-release type.
     */
    private String base;

    /**
     * List of identifiers after the base type.
     */
    private final List<String> identifiers = new ArrayList<String>();

    /**
     * Private constructor.
     * 
     * @param in
     *            - Type's normal string.
     */
    public PreReleaseType(final String in) {
        this.type = in;
        try {
            this.base = in.substring(0, in.indexOf("."));
        } catch(IndexOutOfBoundsException e) {
            this.base = in;
        }

        String[] ids = in.split("\\.");
        identifiers.addAll(Arrays.asList(ids).subList(1, ids.length));
    }

    public String getType() {
        return type;
    }

    public String getBase() {
        return base;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public int compareTo(PreReleaseType o) {
        if (type.isEmpty() && !o.getType().isEmpty()) {
            return -1;
        }
        int compare = o.getType().toLowerCase().compareTo(type.toLowerCase());

        // Compare identifiers if matching base types
        if(compare == 0) {
            int max = Math.max(identifiers.size(), o.getIdentifiers().size());
            for(int i = 0; i < max; i++) {
                String ours = "";
                try {
                    ours = identifiers.get(i);
                } catch(IndexOutOfBoundsException e) {
                    compare = 1;
                    break;
                }
                String theirs = "";
                try {
                    theirs = o.getIdentifiers().get(i);
                } catch(IndexOutOfBoundsException e) {
                    compare = -1;
                    break;
                }
                if(theirs.compareTo(ours) != 0) {
                    compare = theirs.compareTo(ours);
                    break;
                }
            }
        }

        return compare;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PreReleaseType) {
            return type.equals(((PreReleaseType) obj).getType());
        }
        return false;
    }

}
