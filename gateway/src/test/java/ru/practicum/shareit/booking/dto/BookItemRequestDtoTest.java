package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookItemRequestDtoTest {

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
	void shouldAcceptValidBooking() {
		BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
		assertTrue(validator.validate(dto).isEmpty());
	}

	@Test
	void shouldRejectEndBeforeStart() {
		BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));
		assertFalse(validator.validate(dto).isEmpty());
	}

	@Test
	void shouldRejectPastStart() {
		BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
		assertFalse(validator.validate(dto).isEmpty());
	}

	@Test
	void shouldRejectPastEnd() {
		BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1));
		assertFalse(validator.validate(dto).isEmpty());
	}

	@Test
	void shouldRejectMissingItemId() {
		BookItemRequestDto dto = new BookItemRequestDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
		assertFalse(validator.validate(dto).isEmpty());
	}
}
