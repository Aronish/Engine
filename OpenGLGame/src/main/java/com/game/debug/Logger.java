package com.game.debug;

import java.time.LocalTime;

/**
 * Logger with different log levels. Much nicer than println.
 */
public class Logger
{
    private LogLevel logLevel = LogLevel.INFO;
    private String prefix;

    public Logger(String prefix)
    {
        this.prefix = prefix;
    }

    private void setInfoLevel()
    {
        logLevel = LogLevel.INFO;
    }

    private void setWarningLevel()
    {
        logLevel = LogLevel.WARNING;
    }

    private void setErrorLevel()
    {
        logLevel = LogLevel.ERROR;
    }

    /**
     * Logs a message as info.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public <T> void info(T message)
    {
        setInfoLevel();
        log(message);
    }

    @SafeVarargs//TODO: Possibly risky
    public final <T> void info(T... messages)
    {
        setInfoLevel();
        for (T message : messages)
        {
            log(message + ", ");
        }
    }

    /**
     * Logs a message as a warning.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public <T> void warn(T message)
    {
        setWarningLevel();
        log(message);
    }

    /**
     * Logs an error message.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public <T> void error(T message)
    {
        setErrorLevel();
        log(message);
    }

    private <T> void log(T message)
    {
        System.out.println(String.format("%s [%s] [%s]: %s", LocalTime.now(), prefix, logLevel, message));
    }

    private enum LogLevel
    {
        ERROR,
        WARNING,
        INFO
    }
}
