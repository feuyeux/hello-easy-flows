package org.feuyeux.workflow.flows;

import org.feuyeux.workflow.works.PrintMessageWork;
import org.jeasy.flows.workflow.SequentialFlow;

public class SequentialFlowFactory {
    static String flowName = "sequential flow";

    public static SequentialFlow buildSequentialFlow(PrintMessageWork work1, PrintMessageWork work2, PrintMessageWork work3) {

        return SequentialFlow.Builder.aNewSequentialFlow()
                .named(flowName)
                .execute(work1)
                .then(work2)
                .then(work3)
                .build();
    }
}
