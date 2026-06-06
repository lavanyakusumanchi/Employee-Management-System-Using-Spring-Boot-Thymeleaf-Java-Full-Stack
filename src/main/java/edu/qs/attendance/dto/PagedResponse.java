package edu.qs.attendance.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

// Ticket LF-203: wrapper with metadata instead of returning a raw List.
public record PagedResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage
) {
    public static <E, T> PagedResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PagedResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber()
        );
    }
}
