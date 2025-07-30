package com.github.blarc.model;

import java.util.List;

public record PagedResultDto<T>(
        List<T> content,
        int totalPages
) {
}
