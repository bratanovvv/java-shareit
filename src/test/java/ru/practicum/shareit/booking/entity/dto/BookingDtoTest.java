package ru.practicum.shareit.booking.entity.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utils.validation.group.Create;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingDtoTest {

	private static final String PERIOD_MESSAGE = "Booking end must be after start";

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void setUp() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		validatorFactory.close();
	}

	@Test
	void shouldRejectEndBeforeStart() {
		BookingDto dto = new BookingDto(null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1),
				1L, null, null, null);
		assertFalse(validator.validate(dto, Create.class).isEmpty());
	}

	@Test
	void shouldRejectEndEqualToStart() {
		LocalDateTime moment = LocalDateTime.now().plusDays(1);
		BookingDto dto = new BookingDto(null, moment, moment, 1L, null, null, null);
		assertFalse(validator.validate(dto, Create.class).isEmpty());
	}

	@Test
	void shouldAcceptValidPeriod() {
		BookingDto dto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
				1L, null, null, null);
		assertTrue(validator.validate(dto, Create.class).isEmpty());
	}

	@Test
	void shouldSkipPeriodCheckWhenDatesMissing() {
		BookingDto dto = new BookingDto(null, null, null, 1L, null, null, null);
		assertTrue(validator.validate(dto, Create.class).stream()
				.noneMatch(violation -> PERIOD_MESSAGE.equals(violation.getMessage())));
	}
}
