package org.feuyeux.workflow.flows;

import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.WorkReportPredicate;
import org.jeasy.flows.workflow.ConditionalFlow;

public class ConditionalFlowFactory {
    static String flowName = "conditional flow";

    public static ConditionalFlow buildConditionalFlow(Work work1, Work work2, Work work3) {
        return ConditionalFlow.Builder.aNewConditionalFlow()
                .named(flowName)
                .execute(work1)
                .when(WorkReportPredicate.COMPLETED)
                .then(work2)
                .otherwise(work3)
                .build();
    }
}
