package ecs.systems;

import com.badlogic.gdx.Gdx;
import configuration.KeyboardConfig;
import ecs.components.InventoryComponent;
import ecs.components.MissingComponentException;
import ecs.components.PlayableComponent;
import ecs.components.VelocityComponent;
import ecs.entities.Entity;
import ecs.tools.interaction.InteractionTool;
import starter.Game;

/** Used to control the player */
public class PlayerSystem extends ECS_System {

    private record KSData(
            Entity e, PlayableComponent pc, VelocityComponent vc, InventoryComponent ic) {}

    boolean inventoryOpen = false; // inventory need to be open for items to be used

    @Override
    public void update() {
        Game.getEntities().stream()
                .flatMap(e -> e.getComponent(PlayableComponent.class).stream())
                .map(pc -> buildDataObject((PlayableComponent) pc))
                .forEach(this::checkKeystroke);
    }

    private void checkKeystroke(KSData ksd) {
        if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_UP.get()))
            ksd.vc.setCurrentYVelocity(1 * ksd.vc.getYVelocity());
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_DOWN.get()))
            ksd.vc.setCurrentYVelocity(-1 * ksd.vc.getYVelocity());
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_RIGHT.get()))
            ksd.vc.setCurrentXVelocity(1 * ksd.vc.getXVelocity());
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MOVEMENT_LEFT.get()))
            ksd.vc.setCurrentXVelocity(-1 * ksd.vc.getXVelocity());

        if (Gdx.input.isKeyPressed(KeyboardConfig.INTERACT_WORLD.get()))
            InteractionTool.interactWithClosestInteractable(ksd.e);

        if (Gdx.input.isKeyPressed(KeyboardConfig.SHOW_INVENTORY.get())) {
            ksd.ic.showInventory();
            inventoryOpen = true;
        }
        if (Gdx.input.isKeyPressed(KeyboardConfig.CLOSE_BAG_INVENTORY.get())) {
            ksd.ic.closeBag();
            inventoryOpen = false;
        }

        // check item use
        if (Gdx.input.isKeyPressed(KeyboardConfig.USE_ITEM_ONE.get())) {
            if (inventoryOpen) {
                ksd.ic.useItem(0, ksd.e);
                inventoryOpen = false;
            }
        } else if (Gdx.input.isKeyPressed(KeyboardConfig.USE_ITEM_TWO.get())) {
            if (inventoryOpen) {
                ksd.ic.useItem(1, ksd.e);
                inventoryOpen = false;
            }
        } else if (Gdx.input.isKeyPressed(KeyboardConfig.USE_ITEM_THREE.get())) {
            if (inventoryOpen) {
                ksd.ic.useItem(2, ksd.e);
                inventoryOpen = false;
            }
        }

        // check skills
        else if (Gdx.input.isKeyPressed(KeyboardConfig.MELEE_SKILL.get()))
            ksd.pc.getSkillSlotMelee().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.FIRST_SKILL.get()))
            ksd.pc.getSkillSlot1().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.SECOND_SKILL.get()))
            ksd.pc.getSkillSlot2().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.THIRD_SKILL.get()))
            ksd.pc.getSkillSlot3().ifPresent(skill -> skill.execute(ksd.e));
        else if (Gdx.input.isKeyPressed(KeyboardConfig.FOURTH_SKILL.get()))
            ksd.pc.getSkillSlot4().ifPresent(skill -> skill.execute(ksd.e));
    }

    private KSData buildDataObject(PlayableComponent pc) {
        Entity e = pc.getEntity();

        VelocityComponent vc =
                (VelocityComponent)
                        e.getComponent(VelocityComponent.class)
                                .orElseThrow(PlayerSystem::missingVC);

        InventoryComponent ic =
                (InventoryComponent)
                        e.getComponent(InventoryComponent.class)
                                .orElseThrow(PlayerSystem::missingIC);

        return new KSData(e, pc, vc, ic);
    }

    private static MissingComponentException missingVC() {
        return new MissingComponentException("VelocityComponent");
    }

    private static MissingComponentException missingIC() {
        return new MissingComponentException("InventoryComponent");
    }
}
