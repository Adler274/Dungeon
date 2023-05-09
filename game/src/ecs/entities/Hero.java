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


    private final int fireballCoolDown = 2;
    private final int piercingArrowCoolDown = 5;
    private final float xSpeed = 0.3f;
    private final float ySpeed = 0.3f;

    private final String pathToIdleLeft = "knight/idleLeft";
    private final String pathToIdleRight = "knight/idleRight";
    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";
    private Skill firstSkill;
    private Skill secondSkill;

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
        setupHomingFireballSkill();
        setupSwordSkill();
        setupPiercingArrowSkill();
        pc.setSkillSlot1(firstSkill);
        pc.setSkillSlot2(secondSkill);
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

    private void setupFireballSkill() {
        firstSkill =
            new Skill(
                new FireballSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown);
        skills.addSkill(firstSkill);
    }

    private void setupHomingFireballSkill(){
        firstSkill =
            new Skill(
                new HomingFireballSkill(SkillTools::getClosestEnemyPositionAsPoint), fireballCoolDown);
        skills.addSkill(firstSkill);
    }

    private void setupPiercingArrowSkill(){
        secondSkill =
            new Skill(
                new PiercingArrowSkill(SkillTools::getCursorPositionAsPoint), piercingArrowCoolDown);
        skills.addSkill(secondSkill);
    }
  
    public float getXSpeed() {
        return xSpeed;
    }
    public float getYSpeed() {
        return ySpeed;
}
