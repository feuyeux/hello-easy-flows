package org.feuyeux.workflow;

import static org.feuyeux.workflow.flows.ConditionalFlowFactory.buildConditionalFlow;
import static org.feuyeux.workflow.flows.ParallelFlowFactory.buildParallelFlow;
import static org.feuyeux.workflow.flows.RepeatFlowFactory.buildRepeatFlow;
import static org.feuyeux.workflow.flows.SequentialFlowFactory.buildSequentialFlow;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelloEasyFlows.class)
@Slf4j
public class TestFlows {

  @Autowired private Map<String, ZeroWork> workMap;
  private final WorkContext workContext = new WorkContext();

  @Test
  public void test() {
    ZeroWork aWork = workMap.get("A");
    ZeroWork bWork = workMap.get("B");
    ZeroWork cWork = workMap.get("C");

    ConditionalFlow conditionalFlow = buildConditionalFlow(aWork, bWork, cWork);
    SequentialFlow sequentialFlow = buildSequentialFlow(aWork, bWork, cWork);
    RepeatFlow repeatFlow1 = buildRepeatFlow(aWork);
    RepeatFlow repeatFlow2 = buildRepeatFlow(aWork, 3);

    ParallelFlow parallelFlow = buildParallelFlow(aWork, bWork, cWork);

    log.info("=== EXECUTE CONDITIONAL FLOW ===");
    for (int i = 0; i < 3; i++) {
      log.info("{}", i + 1);
      WorkReport workReport = conditionalFlow.execute(workContext);
      log.info("latest flow status:{}", workReport.getStatus());
    }
    log.info("");

    log.info("=== EXECUTE SEQUENTIAL FLOW ===");
    for (int i = 0; i < 3; i++) {
      log.info("{}", i + 1);
      WorkReport workReport = sequentialFlow.execute(workContext);
      log.info("latest flow status:{}", workReport.getStatus());
    }
    log.info("");

    log.info("=== REPEAT UNTIL FLOW ===");
    WorkReport workReport = repeatFlow1.execute(workContext);
    log.info("latest flow status:{}", workReport.getStatus());
    log.info("");

    log.info("=== REPEAT TIMES FLOW ===");
    workReport = repeatFlow2.execute(workContext);
    log.info("latest flow status:{}", workReport.getStatus());
    log.info("");

    log.info("PARALLEL FLOW ===");
    for (int i = 0; i < 3; i++) {
      log.info("{}", i + 1);
      ParallelFlowReport parallelFlowReport = parallelFlow.execute(workContext);
      List<WorkReport> reports = parallelFlowReport.getReports();
      for (WorkReport report : reports) {
        log.info(" status:{}", report.getStatus());
      }
      log.info("parallel flow status:{}", parallelFlowReport.getStatus());
    }
  }
}
