package org.feuyeux.workflow.dag;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.HelloEasyFlows;
import org.feuyeux.workflow.dag.works.ZeroWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.SequentialFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelloEasyFlows.class)
@Slf4j
public class TestDag {

  @Autowired private Map<String, ZeroWork> workMap;

  private final WorkContext workContext = new WorkContext();

  @BeforeEach
  public void init() {
    workContext.put("ALWAYS_SUCCESS", "Y");
  }

  @Test
  public void testDAG() {
    DagTools.DFS(buildTestDag());
  }

  @Test
  public void testDagAndFlow() {
    SequentialFlow sequentialFlow = FlowBuilder.buildFlow(buildTestDag());
    if (sequentialFlow != null) {
      WorkReport workReport = sequentialFlow.execute(workContext);
      log.info("Latest status:{}", workReport.getStatus());
    }
  }

  private WorkFlowNode buildTestDag() {
    WorkFlowNode a = new WorkFlowNode(workMap.get("A"));
    WorkFlowNode b = new WorkFlowNode(workMap.get("B"));
    WorkFlowNode c = new WorkFlowNode(workMap.get("C"));
    WorkFlowNode d = new WorkFlowNode(workMap.get("D"));
    WorkFlowNode e = new WorkFlowNode(workMap.get("E"));
    WorkFlowNode f = new WorkFlowNode(workMap.get("F"));
    WorkFlowNode g = new WorkFlowNode(workMap.get("G"));
    WorkFlowNode h = new WorkFlowNode(workMap.get("H"));
    WorkFlowNode j = new WorkFlowNode(workMap.get("J"));
    WorkFlowNode k = new WorkFlowNode(workMap.get("K"));
    WorkFlowNode l = new WorkFlowNode(workMap.get("L"));
    WorkFlowNode m = new WorkFlowNode(workMap.get("M"));
    WorkFlowNode n = new WorkFlowNode(workMap.get("N"));

    a.addEdge(b);
    a.addEdge(c);
    b.addEdge(d);
    b.addEdge(e);
    d.addEdge(f);
    e.addEdge(f);
    f.addEdge(g);
    f.addEdge(h);
    g.addEdge(j);
    c.addEdge(j);
    j.addEdge(k);
    e.addEdge(l);
    j.addEdge(l);
    k.addEdge(m);
    l.addEdge(m);
    m.addEdge(n);
    h.addEdge(n);
    return a;
  }
}
