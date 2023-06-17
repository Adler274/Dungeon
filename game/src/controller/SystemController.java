package controller;

import ecs.systems.ECS_System;
import logging.CustomLogLevel;

/** used to integrate ECS_Systems in PM-Dungeon game loop */
public class SystemController extends AbstractController<ECS_System> {

    public SystemController() {
        super();
    }

    @Override
    public void process(ECS_System e) {
        if (e.isRunning()) {
            e.systemLogger.log(
                    CustomLogLevel.TRACE,
                    "System '" + e.getClass().getSimpleName() + "' is running.");
            e.update();
        }
    }

    /**
     * Toggles run for all systems (in the SystemController) of the provided class
     *
     * @param ecs_system class of the systems to toggle
     */
    public void toggleSystem(Class<? extends ECS_System> ecs_system) {
        for (ECS_System system : toList()) {
            if (system.getClass() == ecs_system) {
                system.toggleRun();
                if (system.isRunning()) {
                    system.systemLogger.log(
                            CustomLogLevel.INFO,
                            system.getClass().getSimpleName() + " has been started.");
                } else {
                    system.systemLogger.log(
                            CustomLogLevel.INFO,
                            system.getClass().getSimpleName() + " has been paused.");
                }
            }
        }
    }
}
