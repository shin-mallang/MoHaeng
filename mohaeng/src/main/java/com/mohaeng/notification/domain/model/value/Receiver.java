package com.mohaeng.notification.domain.model.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Receiver {

    @Column(name = "receiverId", nullable = false)
    private Long receiverId;

    protected Receiver() {
    }

    private Receiver(final Long receiverId) {
        this.receiverId = receiverId;
    }

    public static Receiver of(final Long receiverId) {
        return new Receiver(receiverId);
    }

    public Long receiverId() {
        return receiverId;
    }
}