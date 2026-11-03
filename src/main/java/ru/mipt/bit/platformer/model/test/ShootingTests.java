package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShootingTest {
    private Tank tank(Field world, int x, int y) {
        Tank tank = new Tank(new GridPoint2(x, y));
        world.addObject(tank);
        return tank;
    }

    private List<Bullet> bullets(Field world) {
        return world.getObjects().stream().filter(Bullet.class::isInstance)
                .map(Bullet.class::cast).collect(Collectors.toList());
    }

    private Bullet bullet(Field world, int x, int y, Direction direction) {
        Bullet bullet = new Bullet(new GridPoint2(x, y), direction, null, 0.08f, 1);
        world.addObject(bullet);
        return bullet;
    }

    @Test
    void shouldCreateBulletInNextCellOnlyWhenCommandExecutes() {
        Field world = new Field(10, 5);
        Tank shooter = tank(world, 1, 1);
        ShootCommand command = new ShootCommand(shooter, world);
        assertTrue(bullets(world).isEmpty());
        command.execute();
        assertEquals(1, bullets(world).size());
        assertEquals(new GridPoint2(2, 1), bullets(world).get(0).getCoordinates());
    }

    @Test
    void shouldShootInEveryOrientation() {
        for (Direction direction : Direction.values()) {
            Field world = new Field(10, 10);
            Tank shooter = tank(world, 5, 5);
            shooter.move(direction, coordinates -> false);
            shooter.shoot(world);
            Bullet bullet = bullets(world).get(0);
            assertEquals(direction.move(new GridPoint2(5, 5)), bullet.getCoordinates());
            world.live(0.08f);
            assertEquals(direction.move(direction.move(new GridPoint2(5, 5))), bullet.getCoordinates());
        }
    }

    @Test
    void shouldHitAdjacentTankImmediatelyAndRemoveItAfterThreeHits() {
        Field world = new Field(10, 5);
        Tank shooter = tank(world, 1, 1);
        Tank target = tank(world, 2, 1);
        for (int hit = 1; hit <= 3; hit++) {
            shooter.shoot(world);
            assertEquals(3 - hit, target.getHealth());
            assertTrue(bullets(world).isEmpty());
            assertEquals(hit < 3, world.contains(target));
            world.live(0.4f);
        }
        assertFalse(target.isAlive());
        assertTrue(world.canMoveTo(new GridPoint2(2, 1)));
    }

    @Test
    void shouldRespectCooldownAndAllowNextShotAfterItExpires() {
        Field world = new Field(10, 5);
        Tank shooter = tank(world, 0, 1);
        shooter.shoot(world);
        shooter.shoot(world);
        assertEquals(1, bullets(world).size());
        world.live(0.2f);
        shooter.shoot(world);
        assertEquals(1, bullets(world).size());
        world.live(0.2f);
        shooter.shoot(world);
        assertEquals(2, bullets(world).size());
    }

    @Test
    void shouldHitTreeWithoutDestroyingIt() {
        Field world = new Field(10, 5);
        Tank shooter = tank(world, 0, 1);
        Tree tree = new Tree(new GridPoint2(3, 1));
        world.addObject(tree);
        shooter.shoot(world);
        world.live(0.4f);
        assertTrue(bullets(world).isEmpty());
        assertTrue(world.contains(tree));
    }

    @Test
    void shouldNotSkipTargetOnLargeTick() {
        Field world = new Field(20, 5);
        Tank shooter = tank(world, 0, 1);
        Tank target = tank(world, 6, 1);
        shooter.shoot(world);
        world.live(2f);
        assertEquals(2, target.getHealth());
        assertTrue(bullets(world).isEmpty());
    }

    @Test
    void shouldDestroyBulletsOnSameCellOrCrossingEdges() {
        Field world = new Field(10, 5);
        Bullet first = bullet(world, 1, 1, Direction.RIGHT);
        Bullet second = bullet(world, 2, 1, Direction.LEFT);
        world.live(0.08f);
        assertFalse(world.contains(first));
        assertFalse(world.contains(second));

        first = bullet(world, 4, 1, Direction.RIGHT);
        second = bullet(world, 4, 1, Direction.LEFT);
        assertFalse(world.contains(first));
        assertFalse(world.contains(second));
    }

    @Test
    void shouldNotCollideParallelBulletsMovingAtSameSpeed() {
        Field world = new Field(10, 5);
        Bullet first = bullet(world, 1, 1, Direction.RIGHT);
        Bullet second = bullet(world, 2, 1, Direction.RIGHT);
        world.live(0.16f);
        assertTrue(world.contains(first));
        assertTrue(world.contains(second));
        assertEquals(new GridPoint2(3, 1), first.getCoordinates());
        assertEquals(new GridPoint2(4, 1), second.getCoordinates());
    }

    @Test
    void shouldRemoveBulletAtAllBoundaries() {
        GridPoint2[] positions = {new GridPoint2(1, 2), new GridPoint2(0, 1),
                new GridPoint2(1, 0), new GridPoint2(2, 1)};
        for (int i = 0; i < positions.length; i++) {
            Field world = new Field(3, 3);
            Tank shooter = tank(world, positions[i].x, positions[i].y);
            shooter.move(Direction.values()[i], coordinates -> false);
            shooter.shoot(world);
            assertTrue(bullets(world).isEmpty());
        }
        Field world = new Field(3, 3);
        Bullet bullet = bullet(world, 2, 1, Direction.RIGHT);
        world.live(0.08f);
        assertFalse(world.contains(bullet));
    }

    @Test
    void shouldNotHitOwnerWhenShootingDuringMovement() {
        Field world = new Field(10, 5);
        Tank shooter = tank(world, 1, 1);
        shooter.move(Direction.RIGHT, world);
        shooter.shoot(world);
        assertEquals(1, bullets(world).size());
        assertEquals(3, shooter.getHealth());
        world.live(0.4f);
        assertEquals(3, shooter.getHealth());
    }

    @Test
    void shouldHitBothCellsReservedByMovingTank() {
        for (GridPoint2 cell : new GridPoint2[] {new GridPoint2(2, 1), new GridPoint2(3, 1)}) {
            Field world = new Field(10, 5);
            Tank target = tank(world, 2, 1);
            target.move(Direction.RIGHT, world);
            bullet(world, cell.x, cell.y, Direction.RIGHT);
            assertEquals(2, target.getHealth());
            assertTrue(bullets(world).isEmpty());
        }
    }

    @Test
    void shouldHitTankEnteringBulletCell() {
        Field world = new Field(10, 5);
        Tank target = tank(world, 2, 1);
        bullet(world, 3, 1, Direction.UP);
        assertTrue(world.canMoveTo(new GridPoint2(3, 1)));
        new MoveCommand(target, Direction.RIGHT, world).execute();
        assertEquals(2, target.getHealth());
        assertTrue(bullets(world).isEmpty());
    }

    @Test
    void shouldCancelQueuedCommandsOfDestroyedOrRemovedTank() {
        Field world = new Field(10, 5);
        Tank target = tank(world, 2, 1);
        ShootCommand shoot = new ShootCommand(target, world);
        MoveCommand move = new MoveCommand(target, Direction.RIGHT, world);
        bullet(world, 2, 1, Direction.UP);
        bullet(world, 2, 1, Direction.UP);
        bullet(world, 2, 1, Direction.UP);
        shoot.execute();
        move.execute();
        assertTrue(bullets(world).isEmpty());
        assertFalse(target.isMoving());
        assertFalse(world.contains(target));

        Tank removed = tank(world, 5, 1);
        world.removeObject(removed);
        removed.shoot(world);
        assertTrue(bullets(world).isEmpty());
    }

    @Test
    void shouldStopUpdatingRemovedBulletAndCopyItsCoordinates() {
        Field world = new Field(10, 5);
        GridPoint2 position = new GridPoint2(1, 1);
        Bullet bullet = new Bullet(position, Direction.RIGHT, null, 0.08f, 1);
        world.addObject(bullet);
        position.set(9, 9);
        bullet.getCoordinates().set(8, 8);
        bullet.getPreviousCoordinates().set(7, 7);
        world.removeObject(bullet);
        world.live(1f);
        assertEquals(new GridPoint2(1, 1), bullet.getCoordinates());
        assertEquals(1, bullet.getDamage());
    }

    @Test
    void shouldValidateShootingParametersAndDamage() {
        for (float value : new float[] {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new Tank(new GridPoint2(), 0.4f, value, 0.08f));
            assertThrows(IllegalArgumentException.class, () -> new Tank(new GridPoint2(), 0.4f, 0.4f, value));
            assertThrows(IllegalArgumentException.class, () -> new Bullet(new GridPoint2(), Direction.UP, null, value, 1));
        }
        assertThrows(IllegalArgumentException.class, () -> new Bullet(new GridPoint2(), Direction.UP, null, 0.1f, 0));
        Tank tank = new Tank(new GridPoint2());
        assertThrows(IllegalArgumentException.class, () -> tank.damage(-1));
        tank.damage(10);
        assertEquals(0, tank.getHealth());
        assertFalse(tank.isAlive());
        tank.move(Direction.RIGHT, coordinates -> true);
        assertFalse(tank.isMoving());
    }

    @Test
    void shouldFinishCooldownAfterSeveralSmallTicks() {
        Field world = new Field(20, 5);
        Tank shooter = tank(world, 0, 1);
        shooter.shoot(world);
        for (int i = 0; i < 4; i++) {
            world.live(0.1f);
        }
        shooter.shoot(world);
        assertEquals(2, bullets(world).size());
    }

    @Test
    void shouldHitMovingTankBeforeItLeavesBulletPathOnLargeTick() {
        Field world = new Field(20, 5);
        Tank target = tank(world, 3, 1);
        target.move(Direction.UP, world);
        bullet(world, 1, 1, Direction.RIGHT);
        world.live(1f);
        assertEquals(2, target.getHealth());
        assertEquals(new GridPoint2(3, 2), target.getCoordinates());
    }

    @Test
    void shouldMatchSmallTicksAndLargeTick() {
        Field large = new Field(20, 5);
        Field small = new Field(20, 5);
        Tank largeTarget = tank(large, 3, 1);
        Tank smallTarget = tank(small, 3, 1);
        largeTarget.move(Direction.UP, large);
        smallTarget.move(Direction.UP, small);
        bullet(large, 0, 1, Direction.RIGHT);
        bullet(small, 0, 1, Direction.RIGHT);
        large.live(1f);
        for (int i = 0; i < 20; i++) {
            small.live(0.05f);
        }
        assertEquals(smallTarget.getHealth(), largeTarget.getHealth());
        assertEquals(smallTarget.getCoordinates(), largeTarget.getCoordinates());
        assertEquals(small.getObjects().size(), large.getObjects().size());
    }

}
