package graphics;

import org.jetbrains.annotations.Nullable;

import static main.Application.MAIN_LOGGER;

public enum RenderSystemType
{
    MULTI_DRAW,
    INSTANCING;

    @Nullable
    public static RenderSystemType validateArgument(String argument) throws Exception
    {
        for (RenderSystemType renderSystemType : RenderSystemType.values())
        {
            if (renderSystemType.toString().equals(argument))
            {
                return renderSystemType;
            }
        }
        MAIN_LOGGER.fatal(new IllegalArgumentException("Unknown render system argument!"));
        return null;
    }
}
