package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private static final LocalDateTime NOW_OFFSET = LocalDateTime.now().minusSeconds(3);

    @Override
    @Transactional
    public CommentDto addComment(CommentDtoRequest commentDtoRequest, Long itemId, Long userId) {
        log.info("Начало добавления комментария");
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + userId + " не найдена"));
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(userId, itemId,
                LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь " + userId + " не брал в аренду вещь " + itemId);
        }
        Comment comment = commentRepository.save(CommentMapper.mapToComment(commentDtoRequest, item, owner));
        log.info("Комментарий с id {} добавлен к вещи {}", comment.getId(), item.getName());
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        ItemRequest request = null;
        if (itemDtoRequest.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDtoRequest.getRequestId()).orElse(null);
        }
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDtoRequest, owner, request));
        log.info("Вещь c id {} создана пользователем {}", item.getId(), owner.getId());
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDtoRequest itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + "не найдена"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь с id = " + id + " не принадлежит " +
                    "указанному пользователю с id = " + userId);
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
        item = itemRepository.save(item);
        log.info("Вещь {} обновлена", item.getName());
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.getReferenceById(id);
        itemRepository.delete(item);
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findByNameContainingIgnoreCase(text);
        items.addAll(itemRepository.findByDescriptionContainingIgnoreCase(text));
        List<Item> result = items.stream()
                .filter(Item::getAvailable)
                .toList();
        return ItemMapper.mapToItemDto(result);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не найдена"));
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndStatusAndEndBeforeOrderByEndDesc(id,
                Status.APPROVED, NOW_OFFSET);
        if (!lastBookings.isEmpty()) {
            lastBooking = BookingMapper.mapToBookingDto(lastBookings.getFirst());
        }
        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(id, LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            nextBooking = BookingMapper.mapToBookingDto(nextBookings.getFirst());
        }
        List<CommentDto> commentDtos = CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(id));
        return ItemMapper.mapToItemDto(item, lastBooking, nextBooking, commentDtos);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Map<Long, Booking> lastBookings = bookingRepository
                .findByItemIdInAndStatusAndEndBeforeOrderByEndDesc(itemIds, Status.APPROVED, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity()
                ));
        Map<Long, Booking> nextBookings = bookingRepository
                .findByItemIdInAndStartAfterOrderByStartAsc(itemIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity()
                ));
        Map<Long, List<Comment>> commentsByItem = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId()
                ));
        return items.stream()
                .map(item -> {
                    BookingDto lastBookingDto = Optional.ofNullable(lastBookings.get(item.getId()))
                            .map(BookingMapper::mapToBookingDto)
                            .orElse(null);
                    BookingDto nextBookingDto = Optional.ofNullable(nextBookings.get(item.getId()))
                            .map(BookingMapper::mapToBookingDto)
                            .orElse(null);
                    List<CommentDto> commentDtos = Optional.ofNullable(commentsByItem.get(item.getId()))
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .collect(Collectors.toList());
                    return ItemMapper.mapToItemDto(item, lastBookingDto, nextBookingDto, commentDtos);
                })
                .collect(Collectors.toList());
    }
}
