package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userRepository.getAll());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        throwIfExists(userDto.getEmail(), null);
        User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        UserDto userDtoExisted = getUserById(id);
        if (userDto.getEmail() != null && !userDto.getEmail().equals(userDtoExisted.getEmail())) {
            throwIfExists(userDto.getEmail(), id);
        }
        return UserMapper.toUserDto(userRepository.update(id, UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto deleteUser(Long id) {
        UserDto userDto = getUserById(id);
        userRepository.delete(id);
        return userDto;
    }

    private void throwIfExists(String email, Long excludedId) {
        if (userRepository.isExistedByEmail(email, excludedId)) {
            throw new ConflictExistsException("User with email = " + email + " already exists");
        }
    }
}
