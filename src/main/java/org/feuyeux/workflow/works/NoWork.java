package org.feuyeux.workflow.works;

import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;

public class NoWork extends ZeroWork{
    public String getName() {
        return "NoWork";
    }

    @Override
    public WorkReport execute(WorkContext workContext) {
        return null;
    }
}
