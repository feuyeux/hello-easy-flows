package org.feuyeux.workflow.flows;

import org.feuyeux.workflow.works.PrintMessageWork;
import org.jeasy.flows.work.WorkReportPredicate;
import org.jeasy.flows.workflow.RepeatFlow;

public class RepeatFlowFactory {
    static WorkReportPredicate predicate = WorkReportPredicate.FAILED;

    public static RepeatFlow buildRepeatFlow(PrintMessageWork work) {
        return RepeatFlow.Builder.aNewRepeatFlow()
                .repeat(work)
                .until(predicate)
                .build();
    }

    public static RepeatFlow buildRepeatFlow(PrintMessageWork work, int times) {
        return RepeatFlow.Builder.aNewRepeatFlow()
                .repeat(work)
                .times(times)
                .build();
    }
}
