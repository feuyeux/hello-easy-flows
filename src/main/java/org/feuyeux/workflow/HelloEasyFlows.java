package org.feuyeux.workflow;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.works.PrintMessageWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.feuyeux.workflow.flows.ConditionalFlowFactory.buildConditionalFlow;
import static org.feuyeux.workflow.flows.ParallelFlowFactory.buildParallelFlow;
import static org.feuyeux.workflow.flows.RepeatFlowFactory.buildRepeatFlow;
import static org.feuyeux.workflow.flows.SequentialFlowFactory.buildSequentialFlow;

@Slf4j
public class HelloEasyFlows {

    public static void main(String[] args) {
        log.info("{}", String.join(" ", args));
        WorkContext workContext = new WorkContext();
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");

        ConditionalFlow conditionalFlow = buildConditionalFlow(work1, work2, work3);
        SequentialFlow sequentialFlow = buildSequentialFlow(work1, work2, work3);
        RepeatFlow repeatFlow1 = buildRepeatFlow(work1);
        RepeatFlow repeatFlow2 = buildRepeatFlow(work1, 3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        ParallelFlow parallelFlow = buildParallelFlow(work1, work2, work3, executorService);
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
        executorService.shutdown();
    }

}
