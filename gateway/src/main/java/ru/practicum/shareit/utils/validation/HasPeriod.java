package ru.practicum.shareit.utils.validation;

import java.time.LocalDateTime;

public interface HasPeriod {

	LocalDateTime getStart();

	LocalDateTime getEnd();
}
