package org.feuyeux.workflow.flows;

import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.workflow.ParallelFlow;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelFlowFactory {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(30);

    public static ParallelFlow buildParallelFlow(Work... works) {
        return ParallelFlow.Builder.aNewParallelFlow()
                .execute(works)
                .with(executorService)
                .build();
    }

    public static ParallelFlow buildParallelFlow(Set<ZeroWork> works0) {
        Work[] works = works0.toArray(new Work[0]);
        return ParallelFlow.Builder.aNewParallelFlow()
                .execute(works)
                .with(executorService)
                .build();
    }
}
