package org.feuyeux.workflow;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.dag.DagTools;
import org.feuyeux.workflow.dag.FlowBuilder;
import org.feuyeux.workflow.dag.TreeNode;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.SequentialFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class TestDag {

  @Autowired
  private Map<String, ZeroWork> workMap;

  private WorkContext workContext = new WorkContext();
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
    SequentialFlow sequentialFlow= FlowBuilder.buildFlow(buildTestDag());
    WorkReport workReport = sequentialFlow.execute(workContext);
    log.info("Latest status:{}", workReport.getStatus());
  }

  private TreeNode buildTestDag() {
    TreeNode a = new TreeNode(workMap.get("A"));
    TreeNode b = new TreeNode(workMap.get("B"));
    TreeNode c = new TreeNode(workMap.get("C"));
    TreeNode d = new TreeNode(workMap.get("D"));
    TreeNode e = new TreeNode(workMap.get("E"));
    TreeNode f = new TreeNode(workMap.get("F"));
    TreeNode g = new TreeNode(workMap.get("G"));
    TreeNode h = new TreeNode(workMap.get("H"));
    TreeNode j = new TreeNode(workMap.get("J"));
    TreeNode k = new TreeNode(workMap.get("K"));
    TreeNode l = new TreeNode(workMap.get("L"));
    TreeNode m = new TreeNode(workMap.get("M"));
    TreeNode n = new TreeNode(workMap.get("N"));

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
