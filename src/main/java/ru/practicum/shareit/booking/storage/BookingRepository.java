package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.booker.id = :bookerId "
			+ "ORDER BY b.start DESC")
	List<Booking> findAllByBooker(@Param("bookerId") long bookerId);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.booker.id = :bookerId AND b.start > :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findFutureByBooker(@Param("bookerId") long bookerId, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.booker.id = :bookerId AND b.start < :now AND b.end > :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findCurrentByBooker(@Param("bookerId") long bookerId, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.booker.id = :bookerId AND b.end < :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findPastByBooker(@Param("bookerId") long bookerId, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.booker.id = :bookerId AND b.status = :status "
			+ "ORDER BY b.start DESC")
	List<Booking> findByBookerAndStatus(@Param("bookerId") long bookerId, @Param("status") BookingStatus status);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.owner.id = :ownerId "
			+ "ORDER BY b.start DESC")
	List<Booking> findAllByOwner(@Param("ownerId") long ownerId);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.owner.id = :ownerId AND b.start > :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findFutureByOwner(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.owner.id = :ownerId AND b.start < :now AND b.end > :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findCurrentByOwner(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.owner.id = :ownerId AND b.end < :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findPastByOwner(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.owner.id = :ownerId AND b.status = :status "
			+ "ORDER BY b.start DESC")
	List<Booking> findByOwnerAndStatus(@Param("ownerId") long ownerId, @Param("status") BookingStatus status);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.id = :itemId AND b.status = :status AND b.start < :now "
			+ "ORDER BY b.start DESC")
	List<Booking> findLastBookings(@Param("itemId") long itemId,
								   @Param("status") BookingStatus status,
								   @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.item.id = :itemId AND b.status = :status AND b.start > :now "
			+ "ORDER BY b.start ASC")
	List<Booking> findNextBookings(@Param("itemId") long itemId,
								   @Param("status") BookingStatus status,
								   @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b "
			+ "WHERE b.booker.id = :bookerId AND b.item.id = :itemId "
			+ "AND b.status = :status AND b.end < :now")
	List<Booking> findCompletedBookings(@Param("bookerId") long bookerId,
										@Param("itemId") long itemId,
										@Param("status") BookingStatus status,
										@Param("now") LocalDateTime now);
}
