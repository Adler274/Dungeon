package ecs.entities.monster;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.MeleeAI;
import ecs.components.ai.idle.NoneWalk;
import ecs.components.ai.transition.SelfDefendTransition;
import ecs.components.skill.FireballSkill;
import ecs.components.skill.Skill;
import ecs.components.skill.SkillComponent;
import ecs.components.skill.SkillTools;
import ecs.components.stats.StatsComponent;
import ecs.components.xp.XPComponent;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import ecs.entities.Chest;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;

/** A type of monster which disguises as chest but attacks upon interaction */
public class Mimic extends Entity {

    private final int lootXP = 25;
    private final String DEFAULT_ANIMATION_FRAME =
            "objects/treasurechest/chest_full_open_anim_f0.png";

    private SkillComponent skills;

    /** Entity with Components. Creates a Mimic which uses fireballs to attack. */
    public Mimic() {
        super();
        new InteractionComponent(this, 1, false, this::onInteract);

        skills = new SkillComponent(this);
        Skill fireball = new Skill(new FireballSkill(SkillTools::getHeroPosition), 3);
        skills.addSkill(fireball);
        new AIComponent(this, new MeleeAI(5, fireball), new NoneWalk(), new SelfDefendTransition());

        new PositionComponent(this);
        new AnimationComponent(this, AnimationBuilder.buildAnimation((DEFAULT_ANIMATION_FRAME)));
        setupHitboxComponent();
        setupHealthComponent();
        setupXpComponent();
        new StatsComponent(this);
    }

    /** Crestes XpComponent and sets lootXP */
    private void setupXpComponent() {
        XPComponent xc = new XPComponent(this);
        xc.setLootXP(lootXP);
    }

    /** Setting up HitboxComponent to deal damage to player when colliding */
    private void setupHitboxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) -> {
                    if (other.getComponent(PlayableComponent.class).isPresent()) {
                        other.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc ->
                                                ((HealthComponent) hc)
                                                        .receiveHit(
                                                                new Damage(
                                                                        2,
                                                                        DamageType.PHYSICAL,
                                                                        this)));
                    }
                },
                null);
    }

    /** Creates HealthComponent */
    private void setupHealthComponent() {
        Animation hcAnimation = AnimationBuilder.buildAnimation(DEFAULT_ANIMATION_FRAME);
        HealthComponent hc = new HealthComponent(this, 5, this::onDeath, hcAnimation, hcAnimation);
        hc.setMaximalHealthpoints(5);
        hc.setCurrentHealthpoints(5);
    }

    /**
     * player earns xp and a new chest spawns upon death
     *
     * @param entity is required and unused
     */
    private void onDeath(Entity entity) {
        Game.getHero()
                .get()
                .getComponent(XPComponent.class)
                .ifPresent(
                        xc -> {
                            ((XPComponent) xc).addXP(lootXP);
                        });

        Chest mimic = Chest.createNewChest();
        this.getComponent(PositionComponent.class)
                .ifPresent(
                        pc -> {
                            mimic.getComponent(PositionComponent.class)
                                    .ifPresent(
                                            mpc ->
                                                    ((PositionComponent) mpc)
                                                            .setPosition(
                                                                    ((PositionComponent) pc)
                                                                            .getPosition()));
                            Game.addEntity(mimic);
                        });
    }

    /**
     * Damages mimic on interaction to change into fight mode
     *
     * @param entity is required and unused
     */
    private void onInteract(Entity entity) {
        this.getComponent(HealthComponent.class)
                .ifPresent(
                        hc ->
                                ((HealthComponent) hc)
                                        .receiveHit(new Damage(1, DamageType.PHYSICAL, null)));
    }
}
