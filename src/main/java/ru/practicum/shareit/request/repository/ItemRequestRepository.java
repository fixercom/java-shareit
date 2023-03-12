package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(Long requesterId, Sort sort);

    @Query("select  ir from ItemRequest ir where ir.requester.id != ?1 order by ir.created desc")
    List<ItemRequest> findAllNotOwnItemRequests(Long requesterId, Pageable pageable);
}