package ru.practicum.shareit.usertests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserDto userDto1;
    private User user1;
    private User user2;
    private UserDto userForUpdate;

    @BeforeEach
    void init() {
        userDto1 = UserDto.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        user1 = User.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("name2")
                .email("user2@email.com")
                .build();

        userForUpdate = UserDto.builder()
                .id(userDto1.getId())
                .name("Updated name")
                .email("updateduser@email.com")
                .build();

    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> found = userService.getAll();
        assertNotNull(found);
        assertEquals(2, found.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        UserDto found = userService.getById(user1.getId());
        assertNotNull(found);
        assertEquals(userDto1, found);
        verify(userRepository, times(1)).findById(anyLong());

    }

    @Test
    void create() {
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());
        UserDto added = userService.create(userDto1);
        assertEquals(userDto1.getId(), added.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        UserDto updatedUser = userService.update(userDto1.getId(), userForUpdate);
        assertEquals(userForUpdate, updatedUser);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        UserDto removed = userService.delete(user1.getId());
        assertEquals(userDto1, removed);
        verify(userRepository, times(1)).deleteById(user1.getId());
    }
}