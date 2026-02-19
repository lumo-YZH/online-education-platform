package com.edu.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    
    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
    
    public PageResult(List<T> records, Long total) {
        this.records = records;
        this.total = total;
    }
}

