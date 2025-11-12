package com.example.phoneshop.data.model;

import java.util.List;

/**
 * Simple wrapper used by UI/ViewModel code.
 * Fields are public because many places in your project wrote to them directly.
 */
public class PagedResponse<T> {
    public List<T> content;
    public int page;
    public int size;
    public int totalPages;
    public long totalElements; // many UI parts used int for this
    public PagedResponse() {}
}
