package com.demo.matching.profile.scheduler.port;

import com.demo.matching.profile.domain.ProfileViewCountHistory;

import java.time.LocalDate;
import java.util.List;

public interface ProfileViewCountRecoveryService {
    List<ProfileViewCountHistory> recoverProfileViewCount(LocalDate localDate);
}
