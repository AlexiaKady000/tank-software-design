package ru.mipt.bit.platformer.input.test;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.input.InputController;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InputControllerTest {
    @Test
    void shouldCallAllInputHandlers() {
        InputController controller = new InputController();
        AtomicInteger calls = new AtomicInteger();

        controller.addHandler(calls::incrementAndGet);
        controller.addHandler(calls::incrementAndGet);
        controller.handleInput();

        assertEquals(2, calls.get());
    }

    @Test
    void shouldHandleEmptyController() {
        new InputController().handleInput();
    }

    @Test
    void shouldPreserveHandlerOrder() {
        InputController controller = new InputController();
        StringBuilder calls = new StringBuilder();
        controller.addHandler(() -> calls.append('1'));
        controller.addHandler(() -> calls.append('2'));
        controller.handleInput();
        assertEquals("12", calls.toString());
    }

}
