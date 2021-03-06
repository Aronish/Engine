package haraldr.dockspace;

import haraldr.dockspace.uicomponents.Font;
import haraldr.dockspace.uicomponents.TextBatch;
import haraldr.dockspace.uicomponents.TextLabel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class DockablePanel
{
    private static final float HEADER_SIZE = 20f;
    protected static final Vector4f HEADER_COLOR = new Vector4f(0.15f, 0.15f, 0.15f, 1f);

    protected Vector2f position, size, headerSize;
    protected Vector4f color;
    private boolean held, pressed; // held = moving, held by header ; pressed = content area pressed

    private TextBatch textBatch = new TextBatch(Font.DEFAULT_FONT);
    protected TextLabel name;

    protected Batch2D renderBatch = new Batch2D();

    private PanelDimensionChangeAction panelDimensionChangeAction = (position, size) -> {};

    public DockablePanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        this.position = new Vector2f(position);
        headerSize = new Vector2f(size.getX(), HEADER_SIZE);
        this.size = size;
        this.color = color;
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));
        renderToBatch();
    }

    public void onEvent(Event event, Window window)
    {
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            held = Physics2D.pointInsideAABB(mousePoint, position, headerSize);
            pressed = Physics2D.pointInsideAABB(mousePoint, Vector2f.add(position, new Vector2f(0f, HEADER_SIZE)), Vector2f.add(size, new Vector2f(0f, -HEADER_SIZE)));
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) held = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            Vector2f mousePoint = new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos);
            if (held)
            {
                setPosition(mousePoint);
            }
        }
    }

    protected void renderToBatch()
    {
        renderBatch.begin();
        renderBatch.drawQuad(position, size, color);
        renderBatch.drawQuad(position, headerSize, HEADER_COLOR);
        renderBatch.end();
    }

    public void render()
    {
        renderBatch.render();
    }

    public void setPanelResizeAction(PanelDimensionChangeAction panelDimensionChangeAction)
    {
        this.panelDimensionChangeAction = panelDimensionChangeAction;
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        name.setPosition(position);
        textBatch.refreshTextMeshData();
        panelDimensionChangeAction.run(
                Vector2f.add(this.position, new Vector2f(0f, headerSize.getY())),
                Vector2f.add(size, new Vector2f(0f, -headerSize.getY()))
        );
        renderToBatch();
    }

    public void setSize(Vector2f size)
    {
        this.size.set(size);
        headerSize.setX(size.getX());
        panelDimensionChangeAction.run(
                Vector2f.add(position, new Vector2f(0f, headerSize.getY())),
                Vector2f.add(size, new Vector2f(0f, -headerSize.getY()))
        );
        renderToBatch();
    }

    public void dispose()
    {
    }

    public void renderText()
    {
        textBatch.render();
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public float getHeaderHeight()
    {
        return headerSize.getY();
    }

    public boolean isHeld()
    {
        return held;
    }

    public boolean isPressed()
    {
        return pressed;
    }

    public TextBatch getTextBatch()
    {
        return textBatch;
    }

    public interface PanelDimensionChangeAction
    {
        void run(Vector2f position, Vector2f size);
    }
}
