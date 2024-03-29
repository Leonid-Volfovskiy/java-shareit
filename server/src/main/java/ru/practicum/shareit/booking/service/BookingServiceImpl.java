package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ModelNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.StatusType.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingRepository;
    private final UserService userService;
    private final UserDao userRepository;
    private final ItemDao itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ModelNotFoundException("Item not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new ModelNotFoundException("User can not book own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("User can book only available item");
        }
        Booking booking = BookingMapper.fromBookingShortDto(bookingRequestDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ModelNotFoundException("Booking not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ModelNotFoundException("Only owner can approve or reject");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Only WAITING can be approved or rejected");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByOwner(Long userId, StatusType state, int from, int size) {
        userService.getById(userId);
        final List<Booking> bookingDtoList;
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByItemOwnerId(userId, pageRequest);
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, WAITING, pageRequest);
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findAllByItemOwnerIdAndStatusEquals(userId, REJECTED, pageRequest);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUser(Long userId, StatusType state, int from, int size) {
        userService.getById(userId);
        final List<Booking> bookingDtoList;
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookingDtoList = bookingRepository.findAllByBookerId(userId, pageRequest);
                break;
            case CURRENT:
                bookingDtoList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookingDtoList = bookingRepository.findAllByBookerIdAndEndBefore(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookingDtoList = bookingRepository.findAllByBookerIdAndStartAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookingDtoList = bookingRepository.findAllByBookerIdAndStatusEquals(userId, WAITING, pageRequest);
                break;
            case REJECTED:
                bookingDtoList = bookingRepository.findAllByBookerIdAndStatusEquals(userId, REJECTED, pageRequest);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ModelNotFoundException("Booking not found"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ModelNotFoundException("Booking for specified user not found");
        }

        return BookingMapper.toBookingDto(booking);
    }
}
