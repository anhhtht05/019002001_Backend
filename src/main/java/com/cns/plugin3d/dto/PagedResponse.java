package com.cns.plugin3d.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> data;
    private Pagination pagination;

    @Data
    @Builder
    public static class Pagination {
        private int page;
        private int limit;
        private long total;
    }
}

