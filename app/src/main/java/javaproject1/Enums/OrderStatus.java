package javaproject1.Enums;

/**
 * Represents the possible states of an order using an Enum.
 * Enums are a special type of class in Java.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    READY_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;
}