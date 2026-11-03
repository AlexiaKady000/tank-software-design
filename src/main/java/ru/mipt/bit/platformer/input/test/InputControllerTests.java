package ru.mipt.bit.platformer.input.test;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.input.InputController;
import ru.mipt.bit.platformer.input.InputHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class InputControllerTest {
    @Test
    void shouldCallAllInputHandlers() {
        InputController controller = new InputController();
        AtomicInteger calls = new AtomicInteger();
        controller.addHandler(() -> calls::incrementAndGet);
        controller.addHandler(() -> calls::incrementAndGet);
        for (Command command : controller.collectCommands()) {
            command.execute();
        }
        assertEquals(2, calls.get());
    }

    @Test
    void shouldHandleEmptyController() {
        assertTrue(new InputController().collectCommands().isEmpty());
    }

    @Test
    void shouldCollectAllCommandsBeforeExecutingAny() {
        InputController controller = new InputController();
        StringBuilder calls = new StringBuilder();
        controller.addHandler(() -> {
            calls.append('1');
            return () -> calls.append('3');
        });
        controller.addHandler(() -> {
            calls.append('2');
            return () -> calls.append('4');
        });
        List<Command> commands = controller.collectCommands();
        assertEquals("12", calls.toString());
        commands.forEach(Command::execute);
        assertEquals("1234", calls.toString());
    }

    @Test
    void shouldRemoveInactiveHandlers() {
        InputController controller = new InputController();
        controller.addHandler(new InputHandler() {

            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public Command getCommand() {
                fail("Inactive handler was queried");
                return Command.NONE;
            }
        });
        assertTrue(controller.collectCommands().isEmpty());
    }
}
