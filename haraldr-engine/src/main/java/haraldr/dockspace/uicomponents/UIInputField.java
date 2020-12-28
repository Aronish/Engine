package haraldr.dockspace.uicomponents;

import haraldr.event.CharTypedEvent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.math.MathUtils;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import org.jetbrains.annotations.Contract;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

@SuppressWarnings("rawtypes")
public class UIInputField<T extends UIInputField.InputFieldValue> extends UIComponent
{
    private static final Vector4f SELECTED_COLOR = new Vector4f(1f), UNSELECTED_COLOR = new Vector4f(0f, 0f, 0f, 1f);
    private static final float BORDER_WIDTH = 2f;

    private Vector2f borderSize;
    private Vector2f fieldPosition = new Vector2f(), fieldSize;
    private boolean selected, held;
    private float lastMouseX;

    private T value;
    private TextLabel textLabel;
    private TextBatch parentTextBatch;
    private InputFieldChangeAction<T> inputFieldChangeAction;

    public UIInputField(TextBatch parentTextBatch, T defaultValue)
    {
        this(parentTextBatch, defaultValue, value -> {});
    }

    public UIInputField(TextBatch parentTextBatch, T defaultValue, InputFieldChangeAction<T> inputFieldChangeAction)
    {
        fieldSize = new Vector2f(0f, parentTextBatch.getFont().getSize() - 2f * BORDER_WIDTH);
        borderSize = new Vector2f(0f, parentTextBatch.getFont().getSize());
        value = defaultValue;
        textLabel = parentTextBatch.createTextLabel(value.toString(), fieldPosition, new Vector4f(0f, 0f, 0f, 1f));
        this.parentTextBatch = parentTextBatch;
        this.inputFieldChangeAction = inputFieldChangeAction;
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        fieldPosition.set(Vector2f.add(position, BORDER_WIDTH));
        textLabel.setPosition(position);
    }

    @Override
    public void setWidth(float width)
    {
        fieldSize.setX(width - 2f * BORDER_WIDTH);
        borderSize.setX(width);
    }

    @Override
    public float getVerticalSize()
    {
        return borderSize.getY();
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
                boolean lastSelectedState = selected;
                held = selected = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), fieldPosition, fieldSize);
                if (held) lastMouseX = mousePressedEvent.xPos;

                if (value.toString().length() == 0) value.setDefaultValue();
                updateTextLabel();
                inputFieldChangeAction.run(value);

                requireRedraw = lastSelectedState != selected;
            }
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_2) && selected)
            {
                value.clear();
                updateTextLabel();
                inputFieldChangeAction.run(value);
            }
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) held = false;
        if (selected)
        {
            if (event.eventType == EventType.KEY_PRESSED)
            {
                if (Input.wasKeyPressed(event, KeyboardKey.KEY_BACKSPACE) && value.toString().length() > 0)
                {
                    value.onCharacterTyped('\b');
                    updateTextLabel();
                    inputFieldChangeAction.run(value);
                }
            }
            if (event.eventType == EventType.CHAR_TYPED)
            {
                var charTypedEvent = (CharTypedEvent) event;
                value.onCharacterTyped(charTypedEvent.character);
                updateTextLabel();
                inputFieldChangeAction.run(value);
            }
        }
        if (held && event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            value.onMouseDragged((float)mouseMovedEvent.xPos - lastMouseX);
            lastMouseX = (float)mouseMovedEvent.xPos;
            updateTextLabel();
            inputFieldChangeAction.run(value);
        }
        if (event.eventType == EventType.PARENT_COLLAPSED)
        {
            textLabel.setEnabled(((ParentCollapsedEvent) event).collapsed);
        }
        return requireRedraw;
    }

    public void updateTextLabel()
    {
        textLabel.setText(value.toString());
        parentTextBatch.refreshTextMeshData();
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(position, borderSize, selected ? SELECTED_COLOR : UNSELECTED_COLOR);
        batch.drawQuad(fieldPosition, fieldSize, new Vector4f(0.8f, 0.8f, 0.8f, 1f));
    }

    public T getValue()
    {
        return value;
    }

    public interface InputFieldChangeAction<UnderlyingType extends InputFieldValue>
    {
        void run(UnderlyingType inputFieldValue);
    }

    public interface InputFieldValue<UnderlyingType>
    {
        default void onMouseDragged(float xOffset) {}
        void onCharacterTyped(char enteredCharacter);
        void setValue(UnderlyingType value);
        void setDefaultValue();
        void clear();
        String toString();
        UnderlyingType getValue();
    }

    public static class IntValue implements InputFieldValue<Integer>
    {
        private String intValue;

        public IntValue(int intValue)
        {
            this.intValue = Integer.toString(intValue);
        }

        @Override
        public void onMouseDragged(float xOffset)
        {
            intValue = Integer.toString(Integer.parseInt(intValue) + MathUtils.fastFloor(xOffset));
        }

        @Override
        public void onCharacterTyped(char enteredCharacter)
        {
            if (enteredCharacter != '\b')
            {
                if (List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').contains(enteredCharacter))
                {
                    intValue += enteredCharacter;
                }
            } else if (intValue.length() > 0)
            {
                intValue = intValue.substring(0, intValue.length() - 1);
            }
        }

        @Override
        public void setValue(Integer value)
        {
            intValue = Integer.toString(value);
        }

        @Override
        public void setDefaultValue()
        {
            intValue = "0";
        }

        @Override
        public void clear()
        {
            intValue = "";
        }

        @Override
        public Integer getValue()
        {
            return intValue.isEmpty() ? 0 : Integer.parseInt(intValue);
        }

        @Override
        public String toString()
        {
            return intValue;
        }
    }

    public static class FloatValue implements InputFieldValue<Float>
    {
        private String floatValue;
        private final float DRAG_SENSITIVITY;

        @Contract(pure = true)
        public FloatValue(float floatValue)
        {
            this.floatValue = Float.toString(floatValue);
            DRAG_SENSITIVITY = 0.02f;
        }

        public FloatValue(float floatValue, float dragSensitivity)
        {
            this.floatValue = Float.toString(floatValue);
            DRAG_SENSITIVITY = dragSensitivity;
        }

        @Override
        public void onMouseDragged(float xOffset)
        {
            // Requires Djava.locale.providers=COMPAT on some systems (cannot parse some numbers correctly with certain locales)
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#.###", decimalFormatSymbols);
            decimalFormat.setRoundingMode(RoundingMode.CEILING);

            floatValue = decimalFormat.format(Float.parseFloat(floatValue) + xOffset * DRAG_SENSITIVITY);
        }

        @Override
        public void onCharacterTyped(char enteredCharacter)
        {
            if (enteredCharacter != '\b')
            {
                if (List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-').contains(enteredCharacter))
                {
                    if (enteredCharacter != '.' && enteredCharacter != '-') floatValue += enteredCharacter;
                    else if (enteredCharacter == '.' && !floatValue.contains(".") && !floatValue.equals("-")) floatValue += enteredCharacter;
                    else if (enteredCharacter == '-' && floatValue.isEmpty()) floatValue += enteredCharacter;
                }
            } else if (floatValue.length() > 0)
            {
                floatValue = floatValue.substring(0, floatValue.length() - 1);
            }
        }

        @Override
        public void setValue(Float value)
        {
            floatValue = Float.toString(value);
        }

        @Override
        public void setDefaultValue()
        {
            floatValue = "0.0";
        }

        @Override
        public void clear()
        {
            floatValue = "";
        }

        @Override
        public Float getValue()
        {
            if (floatValue.isEmpty() || floatValue.equals("-")) return 0f;
            return Float.parseFloat(floatValue);
        }

        @Override
        public String toString()
        {
            return floatValue;
        }
    }

    public static class StringValue implements InputFieldValue<String>
    {
        private String stringValue;

        @Contract(pure = true)
        public StringValue(String stringValue)
        {
            this.stringValue = stringValue;
        }

        @Override
        public void onCharacterTyped(char enteredCharacter)
        {
            if (enteredCharacter != '\b')
            {
                stringValue += enteredCharacter;
            } else
            {
                stringValue = stringValue.substring(0, stringValue.length() - 1);
            }
        }

        @Override
        public void setValue(String value)
        {
            stringValue = value;
        }

        @Override
        public void setDefaultValue()
        {
            stringValue = "";
        }

        @Override
        public void clear()
        {
            stringValue = "";
        }

        @Override
        public String getValue()
        {
            return stringValue;
        }

        @Override
        public String toString()
        {
            return stringValue;
        }
    }
}