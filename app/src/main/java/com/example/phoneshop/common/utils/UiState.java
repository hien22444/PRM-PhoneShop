package com.example.phoneshop.common.utils;

public class UiState<T> {
    public enum Status { IDLE, LOADING, SUCCESS, ERROR, EMPTY }
    public final Status status;
    public final T data;
    public final String error;

    private UiState(Status s, T d, String e){ status=s; data=d; error=e; }
    public static <T> UiState<T> idle(){ return new UiState<>(Status.IDLE,null,null); }
    public static <T> UiState<T> loading(){ return new UiState<>(Status.LOADING,null,null); }
    public static <T> UiState<T> success(T d){ return new UiState<>(Status.SUCCESS,d,null); }
    public static <T> UiState<T> error(String e){ return new UiState<>(Status.ERROR,null,e); }
    public static <T> UiState<T> empty(){ return new UiState<>(Status.EMPTY,null,null); }
}
