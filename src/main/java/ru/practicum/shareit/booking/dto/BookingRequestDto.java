package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.valid.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
@StartBeforeEndDateValid
public class BookingRequestDto {
    private Long id;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    private Long itemId;
}
