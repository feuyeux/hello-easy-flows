package org.feuyeux.workflow.flows;

import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.WorkReportPredicate;
import org.jeasy.flows.workflow.RepeatFlow;

public class RepeatFlowFactory {

  static WorkReportPredicate predicate = WorkReportPredicate.FAILED;

  public static RepeatFlow buildRepeatFlow(Work work) {
    return RepeatFlow.Builder.aNewRepeatFlow().repeat(work).until(predicate).build();
  }

  public static RepeatFlow buildRepeatFlow(Work work, int times) {
    return RepeatFlow.Builder.aNewRepeatFlow().repeat(work).times(times).build();
  }
}
