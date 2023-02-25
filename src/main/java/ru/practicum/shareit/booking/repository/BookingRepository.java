package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.booker.id = ?1")
    List<Booking> findAllUserBookings(Long userId, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2")
    List<Booking> findAllPastUserBookings(Long userId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 AND b.end > ?2")
    List<Booking> findAllCurrentUserBookings(Long userId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2")
    List<Booking> findAllFutureUserBookings(Long userId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findAllItemOwnerBookings(Long userId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findAllPastItemOwnerBookings(Long userId, LocalDateTime currentTime, Sort sort);

    @Query("SELECT b FROM Booking b WHERE  b.item.owner.id = ?1 AND b.start < ?2 AND b.end > ?2")
    List<Booking> findAllCurrentItemOwnerBookings(Long userId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findAllFutureItemOwnerBookings(Long userId, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long itemOwnerId, BookingStatus bookingStatus, Sort sort);

    @Query("select b from Booking  b where b.item.id = ?1 and b.status = 'APPROVED' order by b.start")
    List<Booking> findAllSortedByStartApprovedBookingsByItemId(Long itemId);

    @Query("select b from Booking  b where b.item.owner.id = ?1 and b.status = 'APPROVED' order by b.start")
    List<Booking> findAllSortedByStartApprovedBookingsByItemOwnerId(Long id);

    @Query("select b from Booking  b where b.item.id = ?1 and b.booker.id = ?2" +
            " and ((b.status = 'APPROVED' and b.start < ?3) or (b.status = 'CANCELED'))")
    List<Booking> findAllRealItemBookingsForUserAtTheMoment(Long itemId, Long userId, LocalDateTime currentTime);
}
