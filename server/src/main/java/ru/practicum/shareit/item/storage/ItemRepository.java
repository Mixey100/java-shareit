package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long id);

    List<Item> findByNameContainingIgnoreCase(String text);

    List<Item> findByDescriptionContainingIgnoreCase(String text);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    List<Item> findAllByRequestId(Long requestId);
}
