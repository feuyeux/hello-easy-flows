package org.feuyeux.workflow.works;

import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.WorkContext;

import java.util.Random;

public abstract class ZeroWork implements Work {
    protected Random random;

    protected String getHello(WorkContext workContext) {
        String hello = (String) workContext.get("hello");
        hello += "-" + getName();
        workContext.put("hello", hello);
        return hello;
    }

    protected boolean isSuccess(WorkContext workContext) {
        return "Y".equals(workContext.get("ALWAYS_SUCCESS")) || random.nextBoolean();
    }

    abstract public String getName();

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZeroWork) {
            ZeroWork work = (ZeroWork) obj;
            return getName().equals(work.getName());
        }
        return false;
    }
}
