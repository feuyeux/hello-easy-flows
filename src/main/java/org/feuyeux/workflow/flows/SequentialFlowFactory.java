package org.feuyeux.workflow.flows;

import org.jeasy.flows.work.Work;
import org.jeasy.flows.workflow.SequentialFlow;

public class SequentialFlowFactory {
    static String flowName = "sequential flow";

    public static SequentialFlow buildSequentialFlow(Work... works) {
        SequentialFlow.Builder.ThenStep thenStep = SequentialFlow.Builder.aNewSequentialFlow()
                .named(flowName)
                .execute(works[0]);
        for (int i = 1; i < works.length; i++) {
            thenStep = thenStep.then(works[i]);
        }
        return thenStep.build();
    }
}
