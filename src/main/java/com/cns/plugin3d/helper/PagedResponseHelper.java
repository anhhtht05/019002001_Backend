package com.cns.plugin3d.helper;

import com.cns.plugin3d.dto.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PagedResponseHelper {

    public static <E, D> PagedResponse<D> build(Page<E> pageData, Function<E, D> mapper) {
        List<D> dtoList = pageData.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PagedResponse.<D>builder()
                .data(dtoList)
                .pagination(PagedResponse.Pagination.builder()
                        .page(pageData.getNumber() + 1)
                        .limit(pageData.getSize())
                        .total(pageData.getTotalElements())
                        .build())
                .build();
    }
}
