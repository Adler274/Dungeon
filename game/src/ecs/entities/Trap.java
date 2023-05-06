package ecs.entities;

public abstract class Trap extends Entity{
    protected boolean armed;
    protected abstract void effect();
    protected abstract void disarm();
}
