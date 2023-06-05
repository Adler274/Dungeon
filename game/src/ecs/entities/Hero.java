package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.skill.*;
import ecs.components.xp.XPComponent;
import graphic.Animation;
import starter.Game;

/**
 * The Hero is the player character. It's entity in the ECS. This class helps to setup the hero with
 * all its components and attributes .
 */
public class Hero extends Entity {

    private final int swordCoolDown = 1;
    private final int fireballCoolDown = 2;
    private final int piercingArrowCoolDown = 5;
    private final int physicalWeaknessCoolDown = 1;
    private final int basicHealingCoolDown = 10;
    private final int basicHealingPotency = 2;
    private final int health = 7;
    private final float xSpeed = 0.3f;
    private final float ySpeed = 0.3f;
    private final String pathToIdleLeft = "knight/idleLeft";
    private final String pathToIdleRight = "knight/idleRight";
    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";
    private Skill meleeSkill;
    private Skill firstSkill;
    private Skill secondSkill;
    private Skill thirdSkill;
    private Skill fourthSkill;
    private SkillComponent skills;
    private Character character;
    private enum Character {
        WIZARD,KNIGHT,DWARF
    }

    public Hero() {
        super();
        new PositionComponent(this);
        this.skills = new SkillComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        setupHealthComponent();
        setupXpComponent();
        new PlayableComponent(this);
        setupSwordSkill();
    }


    /** Entity with Components */
    public Hero(Character character) {
        super();
        this.character=character;
        new PositionComponent(this);
        this.skills = new SkillComponent(this);
        setupVelocityComponent();
        setupHitboxComponent();
        new PlayableComponent(this);
        switch (character){
            case WIZARD:
                setupFireballSkill();
                setupAnimationComponent();
                setupHealthComponent();
                setupXpComponent();
            case KNIGHT:
                setupSwordSkill();
                setupAnimationComponent();
                setupHealthComponent();
                setupXpComponent();
            case DWARF:
                setupSwordSkill();
                setupAnimationComponent();
                setupHealthComponent();
                setupXpComponent();


        }
    }



    private void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(pathToRunRight);
        Animation moveLeft = AnimationBuilder.buildAnimation(pathToRunLeft);
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
    }

    private void setupAnimationComponent() {

        if (character==Character.WIZARD) {

    } else if (character==Character.KNIGHT) {
            Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
            Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
            new AnimationComponent(this, idleLeft, idleRight);

    } else if (character==Character.DWARF) {

    }

        else{
            Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
            Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
            new AnimationComponent(this, idleLeft, idleRight);
        }
    }

    private void setupHitboxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) ->
                        System.out.println(
                                "heroCollisionEnter: " + other.getClass().getSimpleName()),
                (you, other, direction) ->
                        System.out.println(
                                "heroCollisionLeave: " + other.getClass().getSimpleName()));
    }

    private void setupHealthComponent() {
        if(character==Character.WIZARD){
            Animation hcAnimation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
            HealthComponent hc =
                new HealthComponent(this, 5, this::onDeath, hcAnimation, hcAnimation);
            hc.setMaximalHealthpoints(5);
            hc.setCurrentHealthpoints(5);
        } else if (character==Character.KNIGHT) {
            Animation hcAnimation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
            HealthComponent hc =
                new HealthComponent(this, health, this::onDeath, hcAnimation, hcAnimation);
            hc.setMaximalHealthpoints(health);
            hc.setCurrentHealthpoints(health);

        } else if (character==Character.DWARF) {
            Animation hcAnimation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
            HealthComponent hc =
                new HealthComponent(this, 8, this::onDeath, hcAnimation, hcAnimation);
            hc.setMaximalHealthpoints(8);
            hc.setCurrentHealthpoints(8);

        }
        else {
            Animation hcAnimation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
            HealthComponent hc =
                new HealthComponent(this, health, this::onDeath, hcAnimation, hcAnimation);
            hc.setMaximalHealthpoints(health);
            hc.setCurrentHealthpoints(health);
        }
    }

    private void setupXpComponent() {
        XPComponent xc = new XPComponent(this, this::onLevelUp);
    }

    private void setupSwordSkill() {
        meleeSkill = new Skill(new SwordSkill(SkillTools::getCursorPositionAsPoint), swordCoolDown);
        skills.addSkill(meleeSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(
                        pc -> {
                            ((PlayableComponent) pc).setSkillSlotMelee(meleeSkill);
                        });
    }

    private void setupFireballSkill() {
        firstSkill =
                new Skill(
                        new FireballSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown);
        skills.addSkill(firstSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(
                        pc -> {
                            ((PlayableComponent) pc).setSkillSlot1(firstSkill);
                        });
    }

    private void setupHomingFireballSkill() {
        firstSkill =
                new Skill(
                        new HomingFireballSkill(SkillTools::getClosestEnemyPositionAsPoint),
                        fireballCoolDown);
        skills.addSkill(firstSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(
                        pc -> {
                            ((PlayableComponent) pc).setSkillSlot1(firstSkill);
                        });
    }

    private void setupPiercingArrowSkill() {
        secondSkill =
                new Skill(
                        new PiercingArrowSkill(SkillTools::getCursorPositionAsPoint),
                        piercingArrowCoolDown);
        skills.addSkill(secondSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(
                        pc -> {
                            ((PlayableComponent) pc).setSkillSlot2(secondSkill);
                        });
    }

    private void setupBasicHealingSpell() {
        thirdSkill = new Skill(new BasicHealingSpell(basicHealingPotency), basicHealingCoolDown);
        skills.addSkill(thirdSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(
                        pc -> {
                            ((PlayableComponent) pc).setSkillSlot3(thirdSkill);
                        });
    }

    private void setupPhysicalWeaknessSpell() {
        fourthSkill =
                new Skill(
                        new PhysicalWeaknessSpell(SkillTools::getCursorPositionAsPoint),
                        physicalWeaknessCoolDown);
        skills.addSkill(fourthSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(
                        pc -> {
                            ((PlayableComponent) pc).setSkillSlot4(fourthSkill);
                        });
    }

    /** Increases players health upon levelUp and learns new skills */
    public void onLevelUp(long nexLevel) {
        this.getComponent(HealthComponent.class)
                .ifPresent(
                        hc -> {
                            ((HealthComponent) hc)
                                    .setMaximalHealthpoints(
                                            ((HealthComponent) hc).getMaximalHealthpoints() + 1);
                            ((HealthComponent) hc)
                                    .setCurrentHealthpoints(
                                            ((HealthComponent) hc).getCurrentHealthpoints() + 1);
                        });
        if (character==Character.WIZARD) {
            switch ((int) nexLevel) {
                case 2 -> this.setupBasicHealingSpell();
                case 5 -> this.setupHomingFireballSkill();
            }
        } else if (character==Character.KNIGHT) {
            switch ((int)nexLevel){
                case 2 -> this.setupPhysicalWeaknessSpell();
                case 5 -> this.setupPiercingArrowSkill();
            }

        } else if (character==Character.DWARF) {
            switch ((int)nexLevel) {
                case 2 -> this.setupBasicHealingSpell();
                case 5 -> this.setupPhysicalWeaknessSpell();
            }
        }
        else {
            switch ((int) nexLevel) {
                case 1 -> this.setupFireballSkill();
                case 2 -> this.setupBasicHealingSpell();
                case 3 -> this.setupPhysicalWeaknessSpell();
                case 4 -> this.setupHomingFireballSkill();
                case 5 -> this.setupPiercingArrowSkill();
            }
        }
    }

    public float getXSpeed() {
        return xSpeed;
    }

    public float getYSpeed() {
        return ySpeed;
    }

    /** Deletes the current savefile and opens the GameOverMenu upon player death */
    public void onDeath(Entity entity) {
        Game.deleteSave();
        Game.getGameOverMenu().showMenu();
    }
}
