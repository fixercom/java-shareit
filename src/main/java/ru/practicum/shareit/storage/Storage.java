package ru.practicum.shareit.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Storage<T> {
    protected Long currentId;
    private final Map<Long, T> elements;

    public Storage() {
        currentId = 0L;
        elements = new HashMap<>();
    }

    public abstract T save(T element);

    public abstract T update(Long id, T element);

    protected T saveElement(Long id, T element) {
        elements.put(id, element);
        return element;
    }

    protected Long generateId() {
        return ++currentId;
    }

    public T findById(Long id) {
        return elements.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(elements.values());
    }

    public int delete(Long id) {
        return elements.remove(id) == null ? 0 : 1;
    }
}
