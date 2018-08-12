package bkko.simplecqrs.domain;

public enum EventType {
    ORDER_REQUESTED,
    PAYMENT_REQUESTED,
    PAYMENT_COMPLETED,
    ORDER_COMPLETED,
    MAIL_REQUESTED,
    MAIL_COMPLETED;
}
