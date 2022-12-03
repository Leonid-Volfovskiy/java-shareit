package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.findItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @Validated({ValidationMarker.Create.class}) @NotNull @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @Validated(ValidationMarker.Update.class) @NotNull @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId) {
        return itemService.deleteItem(itemId, userId);
    }
}
