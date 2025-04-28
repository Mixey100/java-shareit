package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryItemService implements ItemService {

    private final ItemStorage storage;

    @Override
    public List<ItemDto> getItems() {
        return storage.getItems()
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        return storage.getItems()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long id) {
        return storage.getItemById(id)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        return storage.getItems().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto createItem(Item item) {
        checkName(item);
        checkDescription(item);
        checkAvailable(item);
        log.info("Вещь {} добавлена", item.getName());
        return ItemMapper.mapToItemDto(storage.createItem(item));
    }

    @Override
    public ItemDto updateItem(Item item, Long id) {
        Item oldItem = storage.getItemById(id).orElseThrow(() -> new NotFoundException("Вещь с id= " + id + "не найдена"));
        if (item.getName() != null && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.mapToItemDto(storage.updateItem(oldItem));
    }

    @Override
    public void deleteItem(Long id) {
        getItemById(id);
        storage.deleteItem(id);
    }

    private void checkName(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.error("Не указано наименование вещи");
            throw new ValidationException("Должно быть указано наименование вещи");
        }
    }

    private void checkDescription(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error("Нет описания вещи");
            throw new ValidationException("Должно быть описание вещи");
        }
    }

    private void checkAvailable(Item item) {
        if (item.getAvailable() == null) {
            log.error("Не указана доступность вещи");
            throw new ValidationException("Должно быть указана доступность вещи");
        }
    }
}
