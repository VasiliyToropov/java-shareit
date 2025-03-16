package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.item.id =?1")
    List<Booking> findByItemId(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start ASC")
    List<Booking> findAllByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 AND b.end < ?2 ORDER BY b.start ASC")
    List<Booking> findAllByBookerWithCurrentState(Long userId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end > ?2 ORDER BY b.start ASC")
    List<Booking> findAllByBookerWithPastState(Long userId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start ASC")
    List<Booking> findAllByBookerWithFutureState(Long userId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = 'WAITING' ORDER BY b.start ASC")
    List<Booking> findAllByBookerWithWaitingState(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = 'REJECTED' ORDER BY b.start ASC")
    List<Booking> findAllByBookerWithRejectedState(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 ORDER BY b.start ASC")
    List<Booking> findAllByOwnerId(Set<Long> itemsId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.start > ?2 AND b.end < ?2 ORDER BY b.start ASC")
    List<Booking> findAllByOwnerWithCurrentState(Set<Long> itemsId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.end > ?2 ORDER BY b.start ASC")
    List<Booking> findAllByOwnerWithPastState(Set<Long> itemsId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.start > ?2 ORDER BY b.start ASC")
    List<Booking> findAllByOwnerWithFutureState(Set<Long> itemsId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = 'WAITING' ORDER BY b.start ASC")
    List<Booking> findAllByOwnerWithWaitingState(Set<Long> itemsId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = 'REJECTED' ORDER BY b.start ASC")
    List<Booking> findAllByOwnerWithRejectedState(Set<Long> itemsId);


}
