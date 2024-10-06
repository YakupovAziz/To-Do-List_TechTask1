package com.todo.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

@Getter
@Setter
public class PageRequestDto {

    private Integer pageNo = 0;
    private Integer pageSize = 10;

    public Pageable getPageable(PageRequestDto pageRequestDto) {


        Integer page = Objects.nonNull(pageRequestDto.getPageNo()) ? pageRequestDto.getPageSize() : this.pageNo;
        Integer size = Objects.nonNull(pageRequestDto.getPageSize()) ? pageRequestDto.getPageSize() : this.pageSize;

        PageRequest pageRequest = PageRequest.of(page, size);
        return pageRequest;
    }

}
