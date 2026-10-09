package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.entity.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

	@Query("SELECT r FROM ItemRequest r "
			+ "WHERE r.requestor.id = :requestorId "
			+ "ORDER BY r.created DESC")
	List<ItemRequest> findAllByRequestorId(@Param("requestorId") long requestorId);

	@Query("SELECT r FROM ItemRequest r "
			+ "WHERE r.requestor.id <> :requestorId "
			+ "ORDER BY r.created DESC")
	List<ItemRequest> findAllByOtherRequestors(@Param("requestorId") long requestorId);
}
