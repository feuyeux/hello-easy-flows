package org.feuyeux.workflow.dag.works;

import jakarta.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.DefaultWorkReport;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.work.WorkStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service("T")
public class TWork extends ZeroWork {

  @PostConstruct
  public void init() {
    this.random = new Random();
  }

  @Override
  public String getName() {
      return "T";
  }

    @Override
    public WorkReport execute(WorkContext workContext) {
        WorkStatus status;
        if (isSuccess(workContext)) {
            status = WorkStatus.COMPLETED;
        } else {
            status = WorkStatus.FAILED;
        }
        Object id = workContext.get("request_id");
        try {
            //int timeout = random.nextInt(5);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            log.error("",e);
        }
        log.info("{} {}:{}", id == null ? "" : id, this, status);
        return new DefaultWorkReport(status, workContext);
    }
}
