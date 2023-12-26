package org.feuyeux.workflow.works;

import org.feuyeux.workflow.dag.TreeNode;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;

public class Zzz extends ZeroWork {

  public String getName() {
    return TreeNode.Zzz;
  }

  @Override
  public WorkReport execute(WorkContext workContext) {
    return null;
  }
}
