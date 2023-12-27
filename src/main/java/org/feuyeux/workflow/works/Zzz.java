package org.feuyeux.workflow.works;

import org.feuyeux.workflow.dag.WorkFlowNode;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;

public class Zzz extends ZeroWork {

  public String getName() {
    return WorkFlowNode.Zzz;
  }

  @Override
  public WorkReport execute(WorkContext workContext) {
    return null;
  }
}
