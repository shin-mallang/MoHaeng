package com.mohaeng.common.util;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;

import java.util.List;

public class RepositoryUtil {

    public static Club saveClub(final ClubRepository clubRepository, final Club club) {
        return clubRepository.save(club);
    }

    public static Member saveMember(final MemberRepository memberRepository, final Member member) {
        return memberRepository.save(member);
    }

    public static ApplicationForm saveApplicationForm(final ApplicationFormRepository applicationFormRepository, final ApplicationForm applicationForm) {
        return applicationFormRepository.save(applicationForm);
    }

    public static Notification saveNotification(final NotificationRepository notificationRepository, final Notification notification) {
        return notificationRepository.save(notification);
    }

    public static List<Notification> saveNotifications(final NotificationRepository notificationRepository, final List<Notification> notifications) {
        return notificationRepository.saveAll(notifications);
    }
}
