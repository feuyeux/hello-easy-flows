package org.feuyeux.workflow.works;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.*;

import java.util.Random;

@Slf4j
public class PrintMessageWork implements Work {
    private final Random random;
    private final String message;

    public PrintMessageWork(String message) {
        this.message = message;
        this.random = new Random();
    }

    public String getName() {
        return "print message work";
    }

    public WorkReport execute(WorkContext workContext) {
        WorkStatus status;
        if (random.nextBoolean()) {
            status = WorkStatus.COMPLETED;
        } else {
            status = WorkStatus.FAILED;
        }
        log.info("{}:{}", message, status);
        return new DefaultWorkReport(status, workContext);
    }
}
