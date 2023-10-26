package org.feuyeux.workflow;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.flows.work.*;
import org.jeasy.flows.workflow.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HelloEasyFlows {

    public static void main(String[] args) {
        log.info("{}", String.join(" ", args));

        WorkContext workContext = new WorkContext();

        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");

        ConditionalFlow conditionalFlow = ConditionalFlow.Builder.aNewConditionalFlow()
                .named("conditional flow")
                .execute(work1)
                .when(WorkReportPredicate.COMPLETED)
                .then(work2)
                .otherwise(work3)
                .build();
        for (int i = 0; i < 3; i++) {
            log.info("EXECUTE CONDITIONAL FLOW[{}]:", i);
            WorkReport workReport = conditionalFlow.execute(workContext);
            log.info("latest flow status:{}", workReport.getStatus());
        }
        log.info("====");

        SequentialFlow sequentialFlow = SequentialFlow.Builder.aNewSequentialFlow()
                .named("sequential flow")
                .execute(work1)
                .then(work2)
                .then(work3)
                .build();
        log.info("EXECUTE SEQUENTIAL FLOW:");
        WorkReport workReport = sequentialFlow.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
        log.info("====");

        WorkReportPredicate predicate = WorkReportPredicate.FAILED;
        RepeatFlow repeatFlow1 = RepeatFlow.Builder.aNewRepeatFlow()
                .repeat(work1)
                .until(predicate)
                .build();
        log.info("REPEAT UNTIL FLOW:");
        workReport = repeatFlow1.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
        log.info("====");

        RepeatFlow repeatFlow2 = RepeatFlow.Builder.aNewRepeatFlow()
                .repeat(work1)
                .times(3)
                .build();
        log.info("REPEAT TIMES FLOW:");
        workReport = repeatFlow2.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
        log.info("====");

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        ParallelFlow parallelFlow = ParallelFlow.Builder.aNewParallelFlow()
                .execute(work1, work2, work3)
                .with(executorService)
                .build();
        log.info("PARALLEL FLOW:");
        ParallelFlowReport parallelFlowReport = parallelFlow.execute(workContext);
        List<WorkReport> reports = parallelFlowReport.getReports();
        for (WorkReport report : reports) {
            log.info(" status:{}", report.getStatus());
        }
        log.info("parallel flow status:{}", parallelFlowReport.getStatus());
        executorService.shutdown();
    }

    static class PrintMessageWork implements Work {
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
}
