package org.example.clothing_be.dto.general.res;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T>{
    private List<T> content;      // Danh sách dữ liệu (ItemRes)
    private int pageNo;           // Trang hiện tại
    private int pageSize;         // Số lượng phần tử mỗi trang
    private long totalElements;   // Tổng số phần tử trong DB
    private int totalPages;       // Tổng số trang
    private boolean last;         // Có phải trang cuối cùng không
}
