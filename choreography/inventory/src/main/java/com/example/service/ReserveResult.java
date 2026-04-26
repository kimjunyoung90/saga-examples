package com.example.service;

import com.example.entity.Inventory;

public record ReserveResult(Status status, Inventory inventory) {

    public enum Status {
        SUCCESS,
        INSUFFICIENT
    }

    public static ReserveResult success(Inventory inventory) {
        return new ReserveResult(Status.SUCCESS, inventory);
    }

    public static ReserveResult insufficient() {
        return new ReserveResult(Status.INSUFFICIENT, null);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
