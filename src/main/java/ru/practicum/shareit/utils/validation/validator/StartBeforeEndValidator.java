package ru.practicum.shareit.utils.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.utils.validation.HasPeriod;
import ru.practicum.shareit.utils.validation.annotation.StartBeforeEnd;

import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, HasPeriod> {

	@Override
	public boolean isValid(HasPeriod period, ConstraintValidatorContext context) {
		if (period == null) {
			return true;
		}
		LocalDateTime start = period.getStart();
		LocalDateTime end = period.getEnd();
		if (start == null || end == null) {
			return true;
		}
		return end.isAfter(start);
	}
}
