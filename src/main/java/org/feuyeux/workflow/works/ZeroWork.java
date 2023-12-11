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
        return "hola".equals(workContext.get("hi")) || random.nextBoolean();
    }

    abstract public String getName();
}
