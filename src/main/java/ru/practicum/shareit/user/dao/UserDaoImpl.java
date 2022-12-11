package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private static long userIdCounter = 0;

    private long generateId() {
        return ++userIdCounter;
    }

    @Override
    public User create(User user) {
        long id = generateId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(Long id, User userForUpdate) {
        User user = users.get(id);
        if (userForUpdate.getName() != null && !userForUpdate.getName().isBlank()) {
            user.setName(userForUpdate.getName());
        }
        if (userForUpdate.getEmail() != null && !userForUpdate.getEmail().isBlank()) {
            user.setEmail(userForUpdate.getEmail());
        }
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isExistedByEmail(String email, Long excludedId) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email) && !user.getId().equals(excludedId));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
