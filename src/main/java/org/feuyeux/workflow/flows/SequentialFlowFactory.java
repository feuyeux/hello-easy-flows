package org.feuyeux.workflow.flows;

import org.jeasy.flows.work.Work;
import org.jeasy.flows.workflow.SequentialFlow;

public class SequentialFlowFactory {

  static String flowName = "sequential flow";

  public static SequentialFlow buildSequentialFlow(Work... workMap) {
    SequentialFlow.Builder.ThenStep thenStep =
        SequentialFlow.Builder.aNewSequentialFlow().named(flowName).execute(workMap[0]);
    for (int i = 1; i < workMap.length; i++) {
      thenStep = thenStep.then(workMap[i]);
    }
    return thenStep.build();
  }
}
