package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ecs.components.HealthComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.entities.Entity;
import starter.Game;
import tools.Point;

public class SkillTools {

    /**
     * calculates the last position in range regardless of aimed position
     *
     * @param startPoint position to start the calculation
     * @param aimPoint point to aim for
     * @param range range from start to
     * @return last position in range if you follow the directon from startPoint to aimPoint
     */
    public static Point calculateLastPositionInRange(
            Point startPoint, Point aimPoint, float range) {

        // calculate distance from startPoint to aimPoint
        float dx = aimPoint.x - startPoint.x;
        float dy = aimPoint.y - startPoint.y;

        // vector from startPoint to aimPoint
        Vector2 scv = new Vector2(dx, dy);

        // normalize the vector (length of 1)
        scv.nor();

        // resize the vector to the length of the range
        scv.scl(range);

        return new Point(startPoint.x + scv.x, startPoint.y + scv.y);
    }

    public static Point calculateVelocity(Point start, Point goal, float speed) {
        float x1 = start.x;
        float y1 = start.y;
        float x2 = goal.x;
        float y2 = goal.y;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float velocityX = dx / distance * speed;
        float velocityY = dy / distance * speed;
        return new Point(velocityX, velocityY);
    }

    /**
     * gets the current cursor position as Point
     *
     * @return mouse cursor position as Point
     */
    public static Point getCursorPositionAsPoint() {
        Vector3 mousePosition =
                Game.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Point(mousePosition.x, mousePosition.y);
    }

    public static Point getClosestEntityPositionAsPoint(Entity user){
        PositionComponent userPC =
            (PositionComponent)
                user.getComponent(PositionComponent.class).orElseThrow();
        float maxDistance = -1;
        Point targetPoint = SkillTools.getCursorPositionAsPoint();
        for (Entity target : Game.getEntities()) {
            if (target.getComponent(HealthComponent.class).isPresent()
                && target.getComponent(PositionComponent.class).isPresent()
                && target != user){
                PositionComponent targetPC =
                    (PositionComponent)
                        target.getComponent(PositionComponent.class).orElseThrow();
                Point userEntityPosition = userPC.getPosition();
                Point targetEntityPosition = targetPC.getPosition();
                float distance = Point.calculateDistance(userEntityPosition, targetEntityPosition);
                if (distance < maxDistance && distance >= 0 || maxDistance == -1) {
                    maxDistance = distance;
                    targetPoint = targetEntityPosition;
                }
            }
        }
        return targetPoint;
    }

    public static Point getClosestEnemyPositionAsPoint(){
        return getClosestEntityPositionAsPoint(Game.getHero().get());
    }

    public static void applyKnockback(Point hitDirection, Entity entity, float knockback){
        PositionComponent pc = (PositionComponent) entity.getComponent(PositionComponent.class).orElseThrow();
        Point position = pc.getPosition();
        Point direction = Point.getUnitDirectionalVector(position,hitDirection);
        entity.getComponent(VelocityComponent.class).ifPresent(
            vc ->{
                ((VelocityComponent)vc).setCurrentXVelocity(direction.x*knockback);
                ((VelocityComponent)vc).setCurrentYVelocity(direction.y*knockback);
            }
        );
    }
}
