package com.example.phoneshop.data.model;

import java.util.List;

/**
 * ProductResponse: both public fields and getters/setters are provided
 * so existing code that used either style will compile.
 */
public class ProductResponse {
    // Public fields (some code accessed fields directly)
    public List<Product> content;
    public int page;
    public int size;
    public int totalPages;
    // server sometimes sends totalElements as number larger than int; keep as long here
    public long totalElements;

    public ProductResponse() {}

    // getters / setters (some code used these)
    public List<Product> getContent() { return content; }
    public void setContent(List<Product> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
}
