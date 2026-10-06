package ru.practicum.shareit.utils.validation;

import java.time.LocalDateTime;

/**
 * Shared contract for types that describe a time period with a start and an end.
 * It lets the reusable {@code @StartBeforeEnd} constraint validate any such type
 * without the validation utilities depending on a concrete feature package.
 */
public interface HasPeriod {

	LocalDateTime getStart();

	LocalDateTime getEnd();
}
