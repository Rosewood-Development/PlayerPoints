package org.black_ixx.playerpoints.services;

/**
 * Represents a module.
 */
public interface IModule {
    /**
     * Called when the module has been registered to the API.
     */
    void starting();

    /**
     * Called when the module has been removed from the API.
     */
    void closing();
}