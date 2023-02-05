package ru.practicum.shareit.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserStorage extends Storage<User> {

    @Override
    public User save(User user) {
        Long id = generateId();
        user.setId(id);
        return saveElement(id, user);
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);
        return saveElement(id,user);
    }
}
