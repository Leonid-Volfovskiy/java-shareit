package ru.practicum.shareit.itemtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;
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

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private CommentDao commentRepository;
    @Mock
    private ItemRequestDao itemRequestRepository;
    @Mock
    private ItemDao itemRepository;
    @Mock
    private BookingDao bookingRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private UserDto userDto1;
    private User user1;
    private ItemDto.OwnerTiny ownerTiny;
    private ItemInDto itemInDto1;
    private ItemInDto itemInDto1Updated;
    private ItemDto itemDto1;
    private ItemDto itemDtoUpdated1;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private Item item1;

    private Item item2;
    private Comment comment1;
    private CommentDto commentDto1;
    private Comment comment2;
    private CommentDto commentDto2;
    private Booking lastBooking;
    private ItemDto.BookingTiny lastBookingTiny;
    private Booking nextBooking;
    private ItemDto.BookingTiny nextBookingTiny;

    private LastNextBookingDto lastNextBookingDto;
    private String searchedText;


    @BeforeEach
    void init() {
        userDto1 = UserDto.builder()
                .id(1L)
                .name("user_name")
                .email("user1@email.com")
                .build();

        user1 = User.builder()
                .id(1L)
                .name("user_name")
                .email("user1@email.com")
                .build();

        ownerTiny = ItemDto.OwnerTiny.builder()
                .id(1L)
                .name("user_name")
                .build();

        itemInDto1 = ItemInDto.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .requestId(1L)
                .build();

        itemInDto1Updated = ItemInDto.builder()
                .id(1L)
                .name("item_name_updated")
                .description("item_description_updated")
                .available(true)
                .requestId(1L)
                .build();

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("item request description")
                .requestor(user1)
                .created(LocalDateTime.of(2023, 10, 24, 0, 0))
                .build();

        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("item request description 2")
                .requestor(user1)
                .created(LocalDateTime.of(2023, 10, 26, 0, 0))
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
                .id(1L)
                .name("item_name_2")
                .description("item_2_description")
                .available(true)
                .owner(user1)
                .request(itemRequest2)
                .build();

        comment1 = Comment.builder()
                .id(1L)
                .text("comment1")
                .item(item1)
                .author(user1)
                .created(LocalDateTime.of(2023, 10, 24, 0, 0))
                .build();

        commentDto1 = CommentMapper.toCommentDto(comment1);

        comment2 = Comment.builder()
                .id(2L)
                .text("comment2")
                .item(item1)
                .author(user1)
                .created(LocalDateTime.of(2023, 10, 25, 0, 0))
                .build();

        commentDto2 = CommentMapper.toCommentDto(comment2);

        lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 20, 11, 0))
                .end(LocalDateTime.of(2023, 10, 21, 11, 0))
                .item(item1)
                .booker(user1)
                .status(StatusType.APPROVED)
                .build();

        lastBookingTiny = ItemDto.BookingTiny.builder()
                .id(1L)
                .bookerId(user1.getId())
                .build();

        nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 10, 22, 11, 0))
                .end(LocalDateTime.of(2023, 10, 23, 11, 0))
                .item(item1)
                .booker(user1)
                .status(StatusType.APPROVED)
                .build();

        nextBookingTiny = ItemDto.BookingTiny.builder()
                .id(2L)
                .bookerId(user1.getId())
                .build();

        lastNextBookingDto = LastNextBookingDto.builder()
                .itemId(1L)
                .lastBookingId(1L)
                .lastBookingBookerId(1L)
                .nextBookingId(2L)
                .nextBookingBookerId(2L)
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

        itemDtoUpdated1 = ItemDto.builder()
                .id(1L)
                .name("item_name_updated")
                .description("item_description_updated")
                .available(true)
                .requestId(1L)
                .owner(ownerTiny)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();

        searchedText = "item_name";
    }


    @Test
    void create() {
        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest1));
        when(itemRepository.save(any(Item.class))).thenAnswer(returnsFirstArg());
        ItemDto created = itemService.create(itemInDto1, user1.getId());
        assertNotNull(created);
        assertEquals(created, itemDto1);
    }

    @Test
    void update() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item1));
        ItemDto updated = itemService.update(itemInDto1Updated, item1.getId(), user1.getId());
        assertNotNull(updated);
        assertEquals(itemDtoUpdated1, updated);
    }

    @Test
    void getById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository
                .findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(anyLong(), any(LocalDateTime.class),
                        anyList())).thenReturn(Optional.ofNullable(lastBooking));
        when(bookingRepository
                .findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(anyLong(),
                        any(LocalDateTime.class), anyList())).thenReturn(Optional.ofNullable(nextBooking));

        ItemDto itemDto = itemService.getById(item1.getId(), user1.getId());
        itemDto1.setLastBooking(lastBookingTiny);
        itemDto1.setNextBooking(nextBookingTiny);

        assertNotNull(itemDto);
        assertEquals(itemDto1, itemDto);

    }

    @Test
    void createComment() {
        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(), anyLong(),
                any(StatusType.class), any(LocalDateTime.class))).thenReturn(Stream.of(lastBooking, nextBooking)
                .filter(Objects::nonNull).collect(Collectors.toList()));

        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());
        CommentDto created = itemService.createComment(item1.getId(), user1.getId(), commentDto1);

        assertNotNull(created);
        assertEquals(created.getId(), commentDto1.getId());
        assertEquals(created.getText(), commentDto1.getText());
        assertEquals(created.getAuthorName(), commentDto1.getAuthorName());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }


    @Test
    void findAllByRequestId() {
        List<Item> items = Stream.of(item1, item2).filter(Objects::nonNull).collect(toList());
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        List<ItemDto> itemsList = itemService.findAllByRequestId(itemRequest1.getId());
        assertNotNull(itemsList);
        assertEquals(2, itemsList.size());
        verify(itemRepository, times(1)).findAllByRequestId(anyLong());
    }

    @Test
    void getByOwner() {
        List<Item> items = Stream.of(item1, item2).filter(Objects::nonNull).collect(toList());
        List<LastNextBookingDto> findLastNextBooking = Stream.of(lastNextBookingDto)
                .filter(Objects::nonNull).collect(toList());

        when(itemRepository.findAllByOwnerId(anyLong(), any(PageRequest.class))).thenReturn(items);
        when(bookingRepository.findLastNextBooking(anyList())).thenReturn(findLastNextBooking);
        when(commentRepository.findAllByItemIn(anyList(), any(Sort.class))).thenReturn(Collections.emptyList());
        List<ItemDto> itemsList = itemService.getByOwner(1L, 1, 5);
        assertNotNull(itemsList);
        assertEquals(2, itemsList.size());

    }

    @Test
    void search() {
        when(itemRepository.findAllByCriteria(anyString(), any(PageRequest.class)))
                .thenReturn(Stream.of(item1).filter(Objects::nonNull).collect(Collectors.toList()));

        List<ItemDto> searched = itemService.search(searchedText, 1, 1);
        assertNotNull(searched);
        verify(itemRepository, times(1)).findAllByCriteria(anyString(), any(PageRequest.class));

    }
}

