package ru.practicum.shareit.itemrequesttests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemDao itemRepository;
    @Mock
    private ItemRequestDao itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private UserDto userDto1;
    private User user1;
    private ItemRequestDto itemRequestDto;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemDto itemDto1;
    private ItemDto.OwnerTiny ownerTiny;


    @BeforeEach
    void SetUp() {
        user1 = User.builder()
                .id(1L)
                .name("user_name")
                .email("user1@email.com")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("item request description")
                .created(LocalDateTime.of(2023, 2, 24, 0, 0))
                .items(Stream.of(itemDto1).filter(Objects::nonNull).collect(Collectors.toList()))
                .build();

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("item request description")
                .requestor(user1)
                .created(LocalDateTime.of(2023, 2, 24, 0, 0))
                .build();

        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("item request description 2")
                .requestor(user1)
                .created(LocalDateTime.of(2023, 10, 26, 0, 0))
                .build();

        userDto1 = UserDto.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .owner(user1)
                .request(itemRequest1)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("item_name_2")
                .description("item_2_description")
                .available(true)
                .owner(user1)
                .request(itemRequest2)
                .build();

        ownerTiny = ItemDto.OwnerTiny.builder()
                .id(1L)
                .name("user_name")
                .build();

        itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .requestId(1L)
                .owner(ownerTiny)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();

    }

    @Test
    void create() { //Long userId, ItemRequestDto itemRequestDto
        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest1);
        ItemRequestDto created = itemRequestService.create(user1.getId(), itemRequestDto);

        assertNotNull(created);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getAllByUser() {
        List<ItemRequest> itemRequestList = Stream.of(itemRequest1, itemRequest2)
                .filter(Objects::nonNull).collect(Collectors.toList());
        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(anyLong())).thenReturn(itemRequestList);

        List<ItemRequestDto> listItemRequests = itemRequestService.getAllByUser(user1.getId());
        assertNotNull(listItemRequests);
        assertEquals(2, listItemRequests.size());
        verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreatedAsc(anyLong());
    }


    @Test
    void getAll() {
        List<ItemRequest> itemRequestList = Stream.of(itemRequest1, itemRequest2)
                .filter(Objects::nonNull).collect(Collectors.toList());
        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(itemRequestRepository.findAllByRequestorIdNotLikeOrderByCreatedAsc(anyLong(), any(PageRequest.class)))
                .thenReturn(itemRequestList);

        List<ItemRequestDto> allItemRequests = itemRequestService.getAll(1, 10, user1.getId());

        assertNotNull(allItemRequests);
        assertEquals(2, allItemRequests.size());
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNotLikeOrderByCreatedAsc(anyLong(), any(PageRequest.class));
    }

    @Test
    void getById() {
        List<ItemDto> list = List.of(itemDto1);

        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest1));
        when(itemService.findAllByRequestId(anyLong())).thenReturn(list);

        ItemRequestDto requestDto = itemRequestService.getById(itemRequest1.getId(), user1.getId());

        assertNotNull(requestDto);
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }
}

