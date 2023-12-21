package org.feuyeux.workflow;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.config.ComponentConfig;
import org.feuyeux.workflow.config.WorkflowConfig;
import org.feuyeux.workflow.dag.DagBuilder;
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
public class TestAll {

  @Autowired
  private Map<String, ZeroWork> workMap;
  @Autowired
  private WorkflowConfig workflowConfig;

  private SequentialFlow sequentialFlow;

  private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  @BeforeEach
  public void init() {
    List<ComponentConfig> components = workflowConfig.getComponents();
    TreeNode root = DagBuilder.buildTree(components, workMap);
    if (root != null) {
      sequentialFlow = FlowBuilder.buildFlow(root);
    }
  }

  @Test
  public void test() {
    if (sequentialFlow != null) {
      for (int i = 0; i < 5; i++) {
        executor.submit(() -> {
          WorkContext workContext = new WorkContext();
          workContext.put("ALWAYS_SUCCESS", "Y");
          workContext.put("request_id", UUID.randomUUID().toString());
          WorkReport workReport = sequentialFlow.execute(workContext);
          log.info("Latest status:{}", workReport.getStatus());
        });
      }
    } else {
      log.error("sequentialFlow is null");
    }
  }
}
