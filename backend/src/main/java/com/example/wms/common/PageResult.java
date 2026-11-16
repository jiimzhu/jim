package com.example.wms.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 分页结果，页码从 1 开始对前端更友好。
 */
@Data
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;

    public static <E, T> PageResult<T> of(Page<E> page, Function<E, T> mapper) {
        List<T> list = page.getContent().stream().map(mapper).toList();
        return new PageResult<>(list, page.getTotalElements(), page.getNumber() + 1, page.getSize());
    }
}
