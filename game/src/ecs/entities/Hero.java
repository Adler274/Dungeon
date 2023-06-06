package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.skill.*;
import ecs.components.stats.StatsComponent;
import ecs.components.xp.XPComponent;
import ecs.damage.DamageType;
import graphic.Animation;
import starter.Game;

/**
 * The Hero is the player character. It's entity in the ECS. This class helps to setup the hero with
 * all its components and attributes .
 */
public class Hero extends Entity {

    private final int swordCoolDown = 1;
    private final int fireballCoolDown = 1;
    private final int piercingArrowCoolDown = 5;
    private final int physicalWeaknessCoolDown = 1;
    private final int basicHealingCoolDown = 10;
    private final int basicHealingPotency = 2;
    private final float xSpeed = 0.3f;
    private final float ySpeed = 0.3f;
    private Skill meleeSkill;
    private Skill firstSkill;
    private Skill secondSkill;
    private Skill thirdSkill;
    private Skill fourthSkill;
    private SkillComponent skills;
    private Character character;

    /**
     * Entity with Components
     *
     * @param character: chosen character class
     */
    public Hero(Character character) {
        super();
        this.character = character;
        new PositionComponent(this);
        this.skills = new SkillComponent(this);
        setupVelocityComponent();
        setupHitboxComponent();
        new PlayableComponent(this);
        new XPComponent(this, this::onLevelUp);
        setupAnimationComponent();
        setupHealthComponent();
        setupStatsComponent();
        if (character == Character.WIZARD) {
            setupFireballSkill();
        } else {
            setupSwordSkill();
        }
    }

    private void setupVelocityComponent() {
        switch (character) {
            case WIZARD -> {
                Animation moveRight = AnimationBuilder.buildAnimation("wizzard_m_run_anim_f0.png");
                Animation moveLeft =
                        AnimationBuilder.buildAnimation("wizzard_left_run_anim_f0.png");
                new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
            }
            case KNIGHT -> {
                Animation moveRight = AnimationBuilder.buildAnimation("knight/runRight");
                Animation moveLeft = AnimationBuilder.buildAnimation("knight/runLeft");
                new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
            }
            case ELF -> {
                Animation moveRight = AnimationBuilder.buildAnimation("elf_f_run_anim_f0.png");
                Animation moveLeft = AnimationBuilder.buildAnimation("elf_left_run_anim_f0.png");
                new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
            }
            default -> {
                Animation moveRight = AnimationBuilder.buildAnimation("missingTexture.png");
                Animation moveLeft = AnimationBuilder.buildAnimation("missingTexture.png");
                new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
            }
        }
    }

    private void setupAnimationComponent() {
        switch (character) {
            case WIZARD -> {
                Animation idleRight = AnimationBuilder.buildAnimation("wizzard_m_idle_anim_f0.png");
                Animation idleLeft =
                        AnimationBuilder.buildAnimation("wizzard_left_idle_anim_f0.png");
                new AnimationComponent(this, idleLeft, idleRight);
            }
            case KNIGHT -> {
                Animation idleRight = AnimationBuilder.buildAnimation("knight/idleRight");
                Animation idleLeft = AnimationBuilder.buildAnimation("knight/idleLeft");
                new AnimationComponent(this, idleLeft, idleRight);
            }
            case ELF -> {
                Animation idleRight = AnimationBuilder.buildAnimation("elf_f_idle_anim_f0.png");
                Animation idleLeft = AnimationBuilder.buildAnimation("elf_left_idle_anim_f0.png");
                new AnimationComponent(this, idleLeft, idleRight);
            }
            default -> {
                Animation idleRight = AnimationBuilder.buildAnimation("missingTexture.png");
                Animation idleLeft = AnimationBuilder.buildAnimation("missingTexture.png");
                new AnimationComponent(this, idleLeft, idleRight);
            }
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
        switch (character) {
            case WIZARD -> {
                Animation hcAnimation =
                        AnimationBuilder.buildAnimation("animation/missingTexture.png");
                HealthComponent hc =
                        new HealthComponent(this, 5, this::onDeath, hcAnimation, hcAnimation);
                hc.setMaximalHealthpoints(5);
                hc.setCurrentHealthpoints(5);
            }
            case KNIGHT -> {
                Animation hcAnimation =
                        AnimationBuilder.buildAnimation("animation/missingTexture.png");
                HealthComponent hc =
                        new HealthComponent(this, 7, this::onDeath, hcAnimation, hcAnimation);
                hc.setMaximalHealthpoints(7);
                hc.setCurrentHealthpoints(7);
            }
            case ELF -> {
                Animation hcAnimation =
                        AnimationBuilder.buildAnimation("animation/missingTexture.png");
                HealthComponent hc =
                        new HealthComponent(this, 8, this::onDeath, hcAnimation, hcAnimation);
                hc.setMaximalHealthpoints(8);
                hc.setCurrentHealthpoints(8);
            }
            default -> {
                Animation hcAnimation =
                        AnimationBuilder.buildAnimation("animation/missingTexture.png");
                HealthComponent hc =
                        new HealthComponent(this, 20, this::onDeath, hcAnimation, hcAnimation);
                hc.setMaximalHealthpoints(20);
                hc.setCurrentHealthpoints(20);
            }
        }
    }

    private void setupStatsComponent() {
        StatsComponent sc = new StatsComponent(this);
        switch (character) {
            case WIZARD -> {
                sc.getDamageModifiers().setMultiplier(DamageType.PHYSICAL, 1.5f);
                sc.getDamageModifiers().setMultiplier(DamageType.MAGIC, 0.8f);
            }
            case KNIGHT -> {
                sc.getDamageModifiers().setMultiplier(DamageType.PHYSICAL, 0.8f);
                sc.getDamageModifiers().setMultiplier(DamageType.FIRE, 1.5f);
            }
            case ELF -> {
                sc.getDamageModifiers().setMultiplier(DamageType.MAGIC, 1.5f);
                sc.getDamageModifiers().setMultiplier(DamageType.FIRE, 0.8f);
            }
        }
    }

    private void setupSwordSkill() {
        meleeSkill = new Skill(new SwordSkill(SkillTools::getCursorPositionAsPoint), swordCoolDown);
        skills.addSkill(meleeSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(pc -> ((PlayableComponent) pc).setSkillSlotMelee(meleeSkill));
    }

    private void setupFireballSkill() {
        firstSkill =
                new Skill(
                        new FireballSkill(SkillTools::getCursorPositionAsPoint), fireballCoolDown);
        skills.addSkill(firstSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(pc -> ((PlayableComponent) pc).setSkillSlot1(firstSkill));
    }

    private void setupHomingFireballSkill() {
        firstSkill =
                new Skill(
                        new HomingFireballSkill(SkillTools::getClosestEnemyPositionAsPoint),
                        fireballCoolDown);
        skills.addSkill(firstSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(pc -> ((PlayableComponent) pc).setSkillSlot1(firstSkill));
    }

    private void setupPiercingArrowSkill() {
        secondSkill =
                new Skill(
                        new PiercingArrowSkill(SkillTools::getCursorPositionAsPoint),
                        piercingArrowCoolDown);
        skills.addSkill(secondSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(pc -> ((PlayableComponent) pc).setSkillSlot2(secondSkill));
    }

    private void setupBasicHealingSpell() {
        thirdSkill = new Skill(new BasicHealingSpell(basicHealingPotency), basicHealingCoolDown);
        skills.addSkill(thirdSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(pc -> ((PlayableComponent) pc).setSkillSlot3(thirdSkill));
    }

    private void setupPhysicalWeaknessSpell() {
        fourthSkill =
                new Skill(
                        new PhysicalWeaknessSpell(SkillTools::getCursorPositionAsPoint),
                        physicalWeaknessCoolDown);
        skills.addSkill(fourthSkill);
        this.getComponent(PlayableComponent.class)
                .ifPresent(pc -> ((PlayableComponent) pc).setSkillSlot4(fourthSkill));
    }

    /**
     * Increases players health upon levelUp and learns new skills
     *
     * @param nexLevel: level to which the hero gets
     */
    public void onLevelUp(long nexLevel) {
        boolean healthUp = false;
        switch (character) {
            case WIZARD -> {
                switch ((int) nexLevel) {
                    case 2 -> this.setupBasicHealingSpell();
                    case 5 -> this.setupHomingFireballSkill();
                }
                if (nexLevel % 2 == 0) {
                    healthUp = true;
                }
            }
            case KNIGHT -> {
                switch ((int) nexLevel) {
                    case 2 -> this.setupBasicHealingSpell();
                    case 5 -> this.setupPhysicalWeaknessSpell();
                }
                healthUp = true;
            }
            case ELF -> {
                switch ((int) nexLevel) {
                    case 2 -> this.setupPiercingArrowSkill();
                    case 5 -> this.setupPhysicalWeaknessSpell();
                }
                if (nexLevel % 2 == 0 || nexLevel % 3 == 0) {
                    healthUp = true;
                }
            }
            default -> {
                switch ((int) nexLevel) {
                    case 1 -> this.setupFireballSkill();
                    case 2 -> this.setupBasicHealingSpell();
                    case 3 -> this.setupPhysicalWeaknessSpell();
                    case 4 -> this.setupHomingFireballSkill();
                    case 5 -> this.setupPiercingArrowSkill();
                }
                healthUp = true;
            }
        }
        if (healthUp) {
            this.getComponent(HealthComponent.class)
                    .ifPresent(
                            hc -> {
                                ((HealthComponent) hc)
                                        .setMaximalHealthpoints(
                                                ((HealthComponent) hc).getMaximalHealthpoints()
                                                        + 1);
                                ((HealthComponent) hc)
                                        .setCurrentHealthpoints(
                                                ((HealthComponent) hc).getCurrentHealthpoints()
                                                        + 1);
                            });
        }
    }

    public float getXSpeed() {
        return xSpeed;
    }

    public float getYSpeed() {
        return ySpeed;
    }

    public int getHeroClass() {
        switch (character) {
            case WIZARD -> {
                return 1;
            }
            case KNIGHT -> {
                return 2;
            }
            case ELF -> {
                return 3;
            }
            default -> {
                return 0;
            }
        }
    }

    /** Deletes the current savefile and opens the GameOverMenu upon player death */
    public void onDeath(Entity entity) {
        Game.deleteSave();
        Game.getGameOverMenu().showMenu();
    }
}
