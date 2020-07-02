package engine.event;

public interface EventObserver<T extends Event>
{
    void onEvent(T event);
}