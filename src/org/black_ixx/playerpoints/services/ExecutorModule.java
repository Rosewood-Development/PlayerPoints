package org.black_ixx.playerpoints.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A module wrapper around an ExecutorService.
 * <p>
 *     This module provides a simple way to run tasks in the background, so
 *     the server performance is not affected. Only a single backing thread is
 *     used, as the intended use is saving the points storage.
 * </p>
 *
 * @author Tobias Laundal
 */
public class ExecutorModule implements IModule {

    private ExecutorService service = null;

    @Override
    public void starting() {
        if (this.service != null) {
            throw new IllegalStateException("An ExecutorModule instance may only be started once!");
        }
        this.service = Executors.newSingleThreadExecutor();
    }

    @Override
    public void closing() {
        this.service.shutdown();
    }

    public void submit(Runnable task) {
        this.service.submit(task);
    }

}