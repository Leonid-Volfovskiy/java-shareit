package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader(HEADER_USER_ID) Long ownerId) {
        return itemService.getUserItemsById(ownerId);
    }

    @GetMapping("{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemService.findItems(text);
        }
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_USER_ID) Long userId,
                          @Validated({Marker.OnCreate.class}) @NotNull @RequestBody (required = false) ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) Long userId,
                          @PathVariable Long itemId,
                          @Validated(Marker.OnUpdate.class) @NotNull @RequestBody (required = false) ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@RequestHeader(HEADER_USER_ID) Long userId,
                          @PathVariable Long itemId) {
        return itemService.deleteItem(itemId, userId);
    }
}
