package com.mohaeng.alarm.domain.model.value;

import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class Receiver {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    protected Receiver() {
    }

    public Receiver(final Member receiver) {
        this.receiver = receiver;
    }

    public static Receiver of(final Member member) {
        return new Receiver(member);
    }

    public Member receiver() {
        return receiver;
    }
}
