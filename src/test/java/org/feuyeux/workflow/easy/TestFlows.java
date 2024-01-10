package org.feuyeux.workflow.easy;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.HelloEasyFlows;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.work.WorkReportPredicate;
import org.jeasy.flows.workflow.ConditionalFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.feuyeux.workflow.flows.ParallelFlowFactory.buildParallelFlow;
import static org.feuyeux.workflow.flows.RepeatFlowFactory.buildRepeatFlow;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelloEasyFlows.class)
@Slf4j
public class TestFlows {

    private final WorkContext workContext = new WorkContext();

    OneStatusWork a1, b, c;
    TwoStatusWork a2;
    TimesWork a3;

    @BeforeEach
    public void init() {
        a1 = new OneStatusWork("A1", 100);
        a2 = new TwoStatusWork("A2", 1000);
        b = new OneStatusWork("B", 200);
        c = new OneStatusWork("C", 200);
        a3 = new TimesWork("A3", 10, 3);
    }

    @Test
    public void testConditional() {
        ConditionalFlow conditionalFlow = ConditionalFlow.Builder.aNewConditionalFlow()
                .named("conditional_flow")
                .execute(buildParallelFlow(a1, a2))
                .when(WorkReportPredicate.COMPLETED)
                .then(b)
                .otherwise(c)
                .build();
        for (int i = 0; i < 3; i++) {
            WorkReport workReport = conditionalFlow.execute(workContext);
            log.info("latest flow status:{}", workReport.getStatus());
            log.info("--------------------");
        }
    }

    @Test
    public void testRepeat() {
        /*log.info("Run 3 times:");
        WorkReport workReport = buildRepeatFlow(a2, 3).execute(workContext);
        log.info("always true flow status:{}", workReport.getStatus());*/
        log.info("Run until failed:");
        WorkReport untilReport = buildRepeatFlow(a3, WorkReportPredicate.COMPLETED).execute(workContext);
        log.info("maybe false flow status:{}", untilReport.getStatus());
    }
}
