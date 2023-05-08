package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.skill.*;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import graphic.Animation;
import tools.Point;

import java.util.Set;


/**
 * The Hero is the player character. It's entity in the ECS. This class helps to setup the hero with
 * all its components and attributes .
 */
public class Hero extends Entity {

    private final int fireballCoolDown = 1;
    private final float xSpeed = 0.3f;
    private final float ySpeed = 0.3f;

    private final String pathToIdleLeft = "knight/idleLeft";
    private final String pathToIdleRight = "knight/idleRight";
    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";
    private Skill firstSkill;

    private SkillComponent skills;

    /** Entity with Components */
    public Hero() {
        super();
        new PositionComponent(this);
        this.skills = new SkillComponent(this); //new
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        setupHealthComponent();
        PlayableComponent pc = new PlayableComponent(this);
        setupSwordSkill();
        pc.setSkillSlot1(firstSkill);
    }

    private void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(pathToRunRight);
        Animation moveLeft = AnimationBuilder.buildAnimation(pathToRunLeft);
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
    }

    private void setupAnimationComponent() {
        Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
        Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
        new AnimationComponent(this, idleLeft, idleRight);
    }

    private void setupFireballSkill() {
        firstSkill =
                new Skill(
                        new FireballSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown);
        skills.addSkill(firstSkill);
    }

    private void setupHitboxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) -> System.out.println("heroCollisionEnter: " + other.getClass().getSimpleName()),
                (you, other, direction) -> System.out.println("heroCollisionLeave: " + other.getClass().getSimpleName()));
    }

    private void setupHealthComponent(){
        HealthComponent hc = new HealthComponent(this);
        hc.setMaximalHealthpoints(7);
        hc.setCurrentHealthpoints(7);
    }

    private void setupSwordSkill(){
        firstSkill =
            new Skill(
                new SwordSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown);
        skills.addSkill(firstSkill);
    }
}
