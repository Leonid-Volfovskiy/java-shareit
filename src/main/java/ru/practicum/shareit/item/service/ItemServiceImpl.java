package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.OwnerItemException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getUserItemsById(Long ownerId) {
        return itemRepository.getByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = checkItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findItems(String query) {
        return itemRepository.search(query).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = UserMapper.toUser(userService.getUserById(userId));
        Item item = ItemMapper.toItem(itemDto, owner, null);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = checkItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new OwnerItemException("Only owner can edit the Item.");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto deleteItem(Long itemId, Long userId) {
        ItemDto itemDto = getItemById(itemId);
        if (!itemDto.getOwner().getId().equals(userId)) {
            throw new OwnerItemException("Only owner can edit the Item.");
        }
        itemRepository.delete(itemId);

        return itemDto;
    }

    private Item checkItem(Long itemId) {
        return itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));
    }
}
