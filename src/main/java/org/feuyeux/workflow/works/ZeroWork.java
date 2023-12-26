package org.feuyeux.workflow.works;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.*;

@Slf4j
public abstract class ZeroWork implements Work {

  protected Random random;

  public WorkReport execute(WorkContext workContext) {
    WorkStatus status;
    if (isSuccess(workContext)) {
      status = WorkStatus.COMPLETED;
    } else {
      status = WorkStatus.FAILED;
    }
    Object id = workContext.get("request_id");
    log.info("{} {}:{}", id == null ? "" : id, this, status);
    return new DefaultWorkReport(status, workContext);
  }

  protected String getHello(WorkContext workContext) {
    String hello = (String) workContext.get("hello");
    hello += "-" + getName();
    workContext.put("hello", hello);
    return hello;
  }

  protected boolean isSuccess(WorkContext workContext) {
    return "Y".equals(workContext.get("ALWAYS_SUCCESS")) || random.nextBoolean();
  }

  public abstract String getName();

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + getName() + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ZeroWork work) {
      return getName().equals(work.getName());
    }
    return false;
  }
}
