package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long ownerId) {
        User owner = getUserById(ownerId);
        ItemRequest itemRequest = getItemRequestById(itemDtoRequest.getRequestId());
        Item item = itemMapper.toItem(itemDtoRequest, owner, itemRequest);
        Item savedItem = itemRepository.save(item);
        log.debug("Item saved in the database with id={}: {}", savedItem.getId(), item);
        return itemMapper.toItemDtoResponse(savedItem);
    }

    @Override
    public ItemDtoResponseWithDate getItemById(Long id, Long userId) {
        Item item = getItemByIdWithoutCheckAccess(id);
        List<Booking> bookings = bookingRepository.findAllSortedByStartApprovedBookingsByItemId(id);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        ItemDtoResponseWithDate dto = buildItemDtoResponseWithDate(userId, item, bookings, comments);
        log.debug("Item with id={} was obtained from the database: {}", id, item);
        return dto;
    }

    @Override
    public List<ItemDtoResponseWithDate> getAllItemsByOwnerId(Long id) {
        List<Item> items = itemRepository.findAllByOwnerId(id);
        List<Booking> bookings = bookingRepository.findAllSortedByStartApprovedBookingsByItemOwnerId(id);
        List<Comment> comments = commentRepository.findAllByItemOwnerId(id);
        List<ItemDtoResponseWithDate> dtoList = buildItemDtoResponseWithDateList(items, bookings, comments);
        log.debug("All items for owner with id={} were obtained from the database: {}", id, items);
        return dtoList;
    }

    @Override
    @Transactional
    public ItemDtoResponse updateItem(Long id, ItemDtoRequest itemDtoRequest, Long userId) {
        Item item = getItemByIdWithoutCheckAccess(id);
        checkItemOwner(item, userId);
        Item oldItemWithUpdate = itemMapper.updateItemFromDto(itemDtoRequest, item);
        Item updatedItem = itemRepository.save(oldItemWithUpdate);
        log.debug("Item with id={} successfully updated in the database: {}", id, updatedItem);
        return itemMapper.toItemDtoResponse(updatedItem);
    }

    @Override
    public List<ItemDtoResponse> getAvailableItemsByText(String text) {
        List<Item> items = text.isEmpty() ? Collections.emptyList() :
                itemRepository.findAvailableItemsByText(text);
        log.debug("Items containing the text={} are received from the database: {}", text, items);
        return itemMapper.toItemDtoResponseList(items);
    }

    @Override
    @Transactional
    public CommentDtoResponse createComment(Long itemId, CommentDtoRequest commentDtoRequest, Long userId) {
        checkUserHasBookedItemInThePast(userId, itemId);
        Item item = getItemByIdWithoutCheckAccess(itemId);
        User author = getUserById(userId);
        Comment comment = commentMapper.toComment(commentDtoRequest, item, author, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment saved in the database with id={}: {}", savedComment.getId(), item);
        return commentMapper.toCommentDtoResponse(savedComment);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private Item getItemByIdWithoutCheckAccess(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    private void checkUserHasBookedItemInThePast(Long userId, Long itemId) {
        List<Booking> bookings =
                bookingRepository.findAllRealItemBookingsForUserAtTheMoment(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new UserDidNotBookingItemException(userId, itemId);
        }
    }

    public void checkItemOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotOwnerItemException(item.getId(), userId);
        }
    }

    private ItemDtoResponseWithDate buildItemDtoResponseWithDate(Long userId, Item item, List<Booking> bookings,
                                                                 List<Comment> comments) {
        ItemDtoResponseWithDate itemDtoResponseWithDate;
        if (userId.equals(item.getOwner().getId())) {
            itemDtoResponseWithDate = buildDtoWithBookingDates(item, bookings, comments);
        } else {
            itemDtoResponseWithDate = buildDtoWithoutBookingDates(item, comments);
        }
        return itemDtoResponseWithDate;
    }

    private ItemDtoResponseWithDate buildDtoWithBookingDates(Item item, List<Booking> bookings, List<Comment> comments) {
        Booking lastBooking = calculateLastBookingFromSortedList(bookings);
        Booking nextBooking = calculateNextBookingFromSortedList(bookings);
        BookingDtoForItem lastBookingDto = bookingMapper.toBookingDtoForItem(lastBooking);
        BookingDtoForItem nextBookingDto = bookingMapper.toBookingDtoForItem(nextBooking);
        List<CommentDtoResponse> commentDtoList = commentMapper.toCommentDtoResponseList(comments);
        return itemMapper.toItemDtoResponseWithDate(item, lastBookingDto, nextBookingDto, commentDtoList);
    }

    private ItemDtoResponseWithDate buildDtoWithoutBookingDates(Item item, List<Comment> comments) {
        List<CommentDtoResponse> commentDtoList = commentMapper.toCommentDtoResponseList(comments);
        return itemMapper.toItemDtoResponseWithDate(item, null, null, commentDtoList);
    }

    private List<ItemDtoResponseWithDate> buildItemDtoResponseWithDateList(List<Item> items, List<Booking> bookings,
                                                                           List<Comment> comments) {
        Map<Item, List<Booking>> groupedBookings = groupBookingsByItemKey(bookings);
        Map<Item, List<Comment>> groupedComments = groupCommentsByItemKey(comments);
        List<ItemDtoResponseWithDate> responseList = new ArrayList<>();
        for (Item item : items) {
            List<Booking> currentBookings = groupedBookings.get(item);
            List<Comment> currentComments = groupedComments.get(item);
            if (currentBookings == null) {
                responseList.add(buildDtoWithoutBookingDates(item, currentComments));
            } else {
                responseList.add(buildDtoWithBookingDates(item, currentBookings, currentComments));
            }
        }
        return responseList;
    }

    private Map<Item, List<Booking>> groupBookingsByItemKey(List<Booking> bookings) {
        Map<Item, List<Booking>> bookingsMap = new HashMap<>();
        for (Booking booking : bookings) {
            Item item = booking.getItem();
            List<Booking> bookingsForItem = bookingsMap.getOrDefault(item, new ArrayList<>());
            bookingsForItem.add(booking);
            bookingsMap.put(item, bookingsForItem);
        }
        return bookingsMap;
    }

    private Map<Item, List<Comment>> groupCommentsByItemKey(List<Comment> comments) {
        Map<Item, List<Comment>> commentsMap = new HashMap<>();
        for (Comment comment : comments) {
            Item item = comment.getItem();
            List<Comment> commentsForItem = commentsMap.getOrDefault(item, new ArrayList<>());
            commentsForItem.add(comment);
            commentsMap.put(item, commentsForItem);
        }
        return commentsMap;
    }

    private Booking calculateLastBookingFromSortedList(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .reduce((booking, booking2) -> booking2)
                .orElse(null);
    }

    private Booking calculateNextBookingFromSortedList(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .findFirst()
                .orElse(null);
    }

    private ItemRequest getItemRequestById(Long id) {
        return (id == null) ? null : itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));
    }
}