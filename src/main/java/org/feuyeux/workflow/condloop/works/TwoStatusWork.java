package org.feuyeux.workflow.condloop.works;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.*;

@Slf4j
public class TwoStatusWork implements Work {
  private final String name;
  private final long costTime;
  private final Random random;

  public TwoStatusWork(String name, int costTime) {
    this.name = name;
    this.costTime = costTime;
    this.random = new Random(costTime ^ System.nanoTime());
  }

  @Override
  public WorkReport execute(WorkContext workContext) {
    log.info("{} will work {}ms...", this.name, this.costTime);
    try {
      TimeUnit.MILLISECONDS.sleep(costTime);
    } catch (InterruptedException e) {
      log.error("{}", e.getMessage());
    }
    WorkStatus status;
    if (random.nextBoolean()) {
      status = WorkStatus.COMPLETED;
    } else {
      status = WorkStatus.FAILED;
    }
    log.info("{} {}", this.name, status);
    return new DefaultWorkReport(status, workContext);
  }
}
