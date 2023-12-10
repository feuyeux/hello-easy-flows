package org.feuyeux.workflow.flows;

import org.feuyeux.workflow.works.PrintMessageWork;
import org.jeasy.flows.work.WorkReportPredicate;
import org.jeasy.flows.workflow.ConditionalFlow;

public class ConditionalFlowFactory {
    static String flowName = "conditional flow";

    public static ConditionalFlow buildConditionalFlow(PrintMessageWork work1, PrintMessageWork work2, PrintMessageWork work3) {
        return ConditionalFlow.Builder.aNewConditionalFlow()
                .named(flowName)
                .execute(work1)
                .when(WorkReportPredicate.COMPLETED)
                .then(work2)
                .otherwise(work3)
                .build();
    }
}
