package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT r FROM ItemRequest r WHERE r.requester.id != ?1 ORDER BY r.created ASC")
    List<ItemRequest> getAllItemRequests(Long userId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requester.id = ?1 ORDER BY r.created ASC")
    List<ItemRequest> findAllByRequesterId(Long requesterId);
}
