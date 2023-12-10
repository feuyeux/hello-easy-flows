package org.feuyeux.workflow.flows;

import org.feuyeux.workflow.works.PrintMessageWork;
import org.jeasy.flows.workflow.ParallelFlow;

import java.util.concurrent.ExecutorService;

public class ParallelFlowFactory {

    public static ParallelFlow buildParallelFlow(PrintMessageWork work1, PrintMessageWork work2, PrintMessageWork work3, ExecutorService executorService) {
        return ParallelFlow.Builder.aNewParallelFlow()
                .execute(work1, work2, work3)
                .with(executorService)
                .build();
    }
}
