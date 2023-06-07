package ecs.components.ai.idle;

import ecs.entities.Entity;

/** IdleAI which does nothing */
public class NoneWalk implements IIdleAI {

    /** IdleAI which does nothing */
    public NoneWalk() {}

    @Override
    public void idle(Entity entity) {}
}
