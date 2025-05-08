package com.readhub.bookservice.scheduler;

import com.readhub.bookservice.overdue.service.OverdueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OverdueScheduler {

    private final OverdueService overdueService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runAutoRegisterOverdue() {
        overdueService.autoRegisterOverdue();
    }
}
