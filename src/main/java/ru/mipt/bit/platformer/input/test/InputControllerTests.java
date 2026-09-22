package ru.mipt.bit.platformer.input.test;

import org.junit.jupiter.api.Test;
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
}