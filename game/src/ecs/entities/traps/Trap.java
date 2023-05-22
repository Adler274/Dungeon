package ecs.entities.traps;

import ecs.entities.Entity;

public abstract class Trap extends Entity {
    protected boolean armed;
    /** effect of the trap */
    protected abstract void effect();
    /** method to disarm the trap */
    protected abstract void disarm();
}
