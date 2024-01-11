package org.feuyeux.workflow.flows;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.workflow.ParallelFlow;

public class ParallelFlowFactory {

  public static final ExecutorService executorService = Executors.newFixedThreadPool(30);

  public static ParallelFlow buildParallelFlow(Work... workMap) {
    return ParallelFlow.Builder.aNewParallelFlow().execute(workMap).with(executorService).build();
  }

  public static ParallelFlow buildParallelFlow(Set<Work> works0) {
    Work[] workMap = works0.toArray(new Work[0]);
    return ParallelFlow.Builder.aNewParallelFlow().execute(workMap).with(executorService).build();
  }
}
