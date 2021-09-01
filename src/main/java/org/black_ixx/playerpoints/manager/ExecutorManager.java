package org.black_ixx.playerpoints.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import dev.rosewood.rosegarden.manager.Manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ExecutorManager extends Manager {

    private final ExecutorService executor;

    public ExecutorManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        if (rosePlugin.getManager(AbstractConfigurationManager.class).getSettings().get("mysql-settings.enabled").getBoolean()) {
            // Remote database supports multiple connections
            executor = new ForkJoinPool();
        } else {
            // I/O Async Operations can give you a trouble
            executor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
