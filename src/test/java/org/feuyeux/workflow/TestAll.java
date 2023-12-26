package org.feuyeux.workflow;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.config.ComponentConfig;
import org.feuyeux.workflow.config.WorkflowConfig;
import org.feuyeux.workflow.dag.DagBuilder;
import org.feuyeux.workflow.dag.FlowBuilder;
import org.feuyeux.workflow.dag.FlowDebugTools;
import org.feuyeux.workflow.dag.TreeNode;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.workflow.SequentialFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = HelloEasyFlows.class)
@Slf4j
public class TestAll {

  @Autowired private Map<String, ZeroWork> workMap;
  @Autowired private WorkflowConfig workflowConfig;

  private SequentialFlow sequentialFlow;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  @BeforeEach
  public void init() {
    List<ComponentConfig> components = workflowConfig.getComponents();
    TreeNode root = DagBuilder.buildTree(components, workMap);
    if (root != null) {
      sequentialFlow = FlowBuilder.buildFlow(root);
      FlowDebugTools.debug();
    }
  }

  @Test
  public void testLoop() {
    if (sequentialFlow != null) {
      IntStream.range(0, 5).forEach(i -> executor.submit(this::testOne));
    } else {
      log.error("sequentialFlow is null");
    }
  }

  @Test
  public void testOne() {
    WorkContext workContext = new WorkContext();
    workContext.put("ALWAYS_SUCCESS", "Y");
    workContext.put("request_id", UUID.randomUUID().toString());
    sequentialFlow.execute(workContext);
  }
}
