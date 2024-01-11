package org.feuyeux.workflow.condloop.works;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.*;

@Slf4j
public class ThreeStatusWork implements Work {
  private final String name;
  private final long costTime;
  private final int times;
  private final Random random;

  public ThreeStatusWork(String name, int costTime, int times) {
    this.name = name;
    this.costTime = costTime;
    this.times = times;
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
    Object ott = workContext.get("times");
    int tt;
    if (ott == null) {
      tt = 1;
    } else {
      tt = (int) ott + 1;
    }
    workContext.put("times", tt);
    if (tt >= times) {
      status = WorkStatus.FAILED;
    } else {
      if (random.nextBoolean()) {
        status = WorkStatus.COMPLETED;
      } else {
        status = WorkStatus.FAILED;
      }
    }
    log.info("{}[{}] {}", this.name, tt, status);
    return new DefaultWorkReport(status, workContext);
  }
}
