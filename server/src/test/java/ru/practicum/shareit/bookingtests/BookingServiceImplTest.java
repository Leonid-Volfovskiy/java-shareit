package ru.practicum.shareit.bookingtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
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
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingDao bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserDao userRepository;
    @Mock
    private ItemDao itemRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private Item item1;
    private Booking booking1;
    private Booking booking;
    private BookingRequestDto bookingRequestDto1;
    private BookingDto bookingDto;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private UserDto userDto1;

    @BeforeEach
    void init() {
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

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("item request description")
                .requestor(user1)
                .created(LocalDateTime.of(2023, 10, 24, 0, 0))
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .owner(user2)
                .request(itemRequest1)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 25, 11, 0))
                .end(LocalDateTime.of(2023, 10, 26, 11, 0))
                .item(item1)
                .booker(user1)
                .status(StatusType.FUTURE)
                .build();

        booking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 10, 25, 11, 0))
                .end(LocalDateTime.of(2023, 10, 26, 11, 0))
                .item(item1)
                .booker(user1)
                .status(StatusType.WAITING)
                .build();

        bookingRequestDto1 = BookingRequestDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .itemId(item1.getId())
                .build();


        bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(StatusType.WAITING)
                .booker(BookingDto.Booker.builder().id(1L).name("name1").build())
                .item(BookingDto.BookedItem.builder().id(1L).name("item_name").build())
                .bookerId(1L)
                .build();

        bookingDto = BookingDto.builder()
                .id(2L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(StatusType.REJECTED)
                .booker(BookingDto.Booker.builder().id(1L).name("name1").build())
                .item(BookingDto.BookedItem.builder().id(1L).name("item_name").build())
                .bookerId(1L)
                .build();

        bookingDto2 = BookingDto.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .booker(BookingDto.Booker.builder().id(1L).name("name1").build())
                .item(BookingDto.BookedItem.builder().id(1L).name("item_name").build())
                .bookerId(1L)
                .build();


        userDto1 = UserDto.builder()
                .id(1L)
                .name("name1")
                .email("user1@email.com")
                .build();

    }


    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        BookingDto created = bookingService.create(bookingRequestDto1, user1.getId());
        assertNotNull(created);
        assertEquals(created, bookingDto1);

    }

    @Test
    void approve() {
        when(userService.getById(anyLong())).thenReturn(userDto1);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        BookingDto approvedDto = bookingService.approve(booking.getId(), user2.getId(), any(Boolean.TYPE));
        assertNotNull(approvedDto);
        assertEquals(approvedDto, bookingDto);
    }

    @ParameterizedTest
    @EnumSource(StatusType.class)
    void getAllByOwner(StatusType state) {
        int from = 1;
        int size = 10;

        List<Booking> bookingDtoList = Stream.of(booking1, booking).filter(Objects::nonNull).collect(Collectors.toList());

        Executor finder = () -> {
            List<BookingDto> found = bookingService.getAllByOwner(1L, state, from, size);
            assertNotNull(found);
            assertEquals(2, found.size());
        };

        switch (state) {
            case ALL:
                when(bookingRepository.findAllByItemOwnerId(anyLong(), any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByItemOwnerId(anyLong(), any(PageRequest.class));
                break;
            case CURRENT:
                when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                                any(LocalDateTime.class), any(PageRequest.class));
                break;
            case PAST:
                when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class));
                break;

            case FUTURE:
                when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(PageRequest.class));
                break;

            case WAITING:

            case REJECTED:
                when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(anyLong(), any(StatusType.class),
                        any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusEquals(anyLong(),
                        any(StatusType.class), any(PageRequest.class));
                break;

        }
    }

    @ParameterizedTest
    @EnumSource(StatusType.class)
    void getAllByUser(StatusType state) {
        int from = 1;
        int size = 10;

        List<Booking> bookingDtoList = Stream.of(booking1, booking).filter(Objects::nonNull).collect(Collectors.toList());

        Executor finder = () -> {
            List<BookingDto> found = bookingService.getAllByUser(1L, state, from, size);
            assertNotNull(found);
            assertEquals(2, found.size());
        };

        switch (state) {
            case ALL:
                when(bookingRepository.findAllByBookerId(anyLong(), any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByBookerId(anyLong(), any(PageRequest.class));
                break;
            case CURRENT:
                when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                                any(LocalDateTime.class), any(PageRequest.class));
                break;
            case PAST:
                when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class));
                break;

            case FUTURE:
                when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                        any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1))
                        .findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(PageRequest.class));
                break;

            case WAITING:

            case REJECTED:
                when(bookingRepository.findAllByBookerIdAndStatusEquals(anyLong(), any(StatusType.class),
                        any(PageRequest.class))).thenReturn(bookingDtoList);
                finder.execute();
                verify(bookingRepository, times(1)).findAllByBookerIdAndStatusEquals(anyLong(),
                        any(StatusType.class), any(PageRequest.class));
                break;

        }

    }

    @Test
    void getById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking1));
        BookingDto bookingGot = bookingService.getById(booking1.getId(), user1.getId());

        assertNotNull(bookingGot);
        assertEquals(bookingDto2, bookingGot);

    }

    @FunctionalInterface
    interface Executor {
        void execute();
    }
}