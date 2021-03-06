package haraldr.dockspace.uicomponents;

import haraldr.dockspace.ControlPanel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.MouseButton;
import haraldr.input.Input;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class Checkbox extends LabeledComponent
{
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f), ON_COLOR = new Vector4f(0.3f, 0.8f, 0.2f, 1f);

    private Vector2f boxPosition = new Vector2f(), boxSize;
    private boolean state;

    private CheckboxStateChangeAction checkboxStateChangeAction;

    public Checkbox(String name, ControlPanel parent)
    {
        this(name, parent, (state) -> {});
    }

    public Checkbox(String name, ControlPanel parent, CheckboxStateChangeAction checkboxStateChangeAction)
    {
        super(name, parent);
        boxSize = new Vector2f(parent.getComponentDivisionSize(), label.getFont().getSize());
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    @Override
    public void setComponentPosition(Vector2f position)
    {
        boxPosition = position;
    }

    @Override
    public void setWidth(float width)
    {
        boxSize.setX(width);
    }

    public void setCheckboxStateChangeAction(CheckboxStateChangeAction checkboxStateChangeAction)
    {
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    @Override
    public boolean onEvent(Event event)
    {
        boolean requireRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), boxPosition, boxSize))
                {
                    state = !state;
                    checkboxStateChangeAction.run(state);
                    requireRedraw = true;
                }
            }
        }
        return requireRedraw;
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(boxPosition, boxSize, state ? ON_COLOR : OFF_COLOR);
    }

    @Override
    public float getVerticalSize()
    {
        return boxSize.getY();
    }

    public boolean isChecked()
    {
        return state;
    }

    @FunctionalInterface
    public interface CheckboxStateChangeAction
    {
        void run(boolean state);
    }
}
