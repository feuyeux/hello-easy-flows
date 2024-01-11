package org.feuyeux.workflow.condloop.works;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class OneStatusWork implements Work {
    private final String name;
    private final long costTime;

    public OneStatusWork(String name, int costTime) {
        this.name = name;
        this.costTime = costTime;
    }

    @Override
public WorkReport execute(WorkContext workContext) {
    log.info("{} will work {}ms...", this.name, this.costTime);
    try {
        TimeUnit.MILLISECONDS.sleep(costTime);
    } catch (InterruptedException e) {
        log.error("{}", e.getMessage());
    }
    WorkStatus  status = WorkStatus.COMPLETED;
    log.info("{} {}", this.name, status);
    return new DefaultWorkReport(status, workContext);
}

}
