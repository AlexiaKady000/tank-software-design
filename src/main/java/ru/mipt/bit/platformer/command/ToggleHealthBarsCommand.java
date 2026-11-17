package ru.mipt.bit.platformer.command;

/**
 * Type - class, role - use case
 */
public class ToggleHealthBarsCommand implements Command {
    private final Runnable toggle;

    public ToggleHealthBarsCommand(Runnable toggle) {
        this.toggle = toggle;
    }

    @Override
    public void execute() {
        toggle.run();
    }
}
