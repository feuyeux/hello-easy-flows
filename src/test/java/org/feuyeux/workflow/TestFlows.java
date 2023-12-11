package org.feuyeux.workflow;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.feuyeux.workflow.flows.ConditionalFlowFactory.buildConditionalFlow;
import static org.feuyeux.workflow.flows.ParallelFlowFactory.buildParallelFlow;
import static org.feuyeux.workflow.flows.RepeatFlowFactory.buildRepeatFlow;
import static org.feuyeux.workflow.flows.SequentialFlowFactory.buildSequentialFlow;

@SpringBootTest
@Slf4j
public class TestFlows {

    @Autowired
    Map<String, Work> works;
    WorkContext workContext = new WorkContext();

    @Test
    public void testDAG() {
        Work aWork = works.get("AWork");
        Work bWork = works.get("BWork");
        Work cWork = works.get("CWork");
        Work dWork = works.get("DWork");
        Work eWork = works.get("EWork");
        Work fWork = works.get("FWork");
        Work gWork = works.get("GWork");
        Work hWork = works.get("HWork");
        workContext.put("hi", "hola");
        workContext.put("hello", "");
        SequentialFlow sequentialFlow = buildSequentialFlow(
                aWork,
                buildParallelFlow(
                        bWork,
                        buildSequentialFlow(
                                cWork,
                                buildParallelFlow(
                                        buildSequentialFlow(bWork, eWork),
                                        eWork,
                                        fWork),
                                gWork),
                        dWork),
                hWork);
        WorkReport workReport = sequentialFlow.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
    }

    @Test
    public void test() {

        Work aWork = works.get("AWork");
        Work bWork = works.get("BWork");
        Work cWork = works.get("CWork");

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
