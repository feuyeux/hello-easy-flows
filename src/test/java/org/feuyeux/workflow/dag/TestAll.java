package org.feuyeux.workflow.dag;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.HelloEasyFlows;
import org.feuyeux.workflow.dag.config.ComponentConfigV1;
import org.feuyeux.workflow.dag.config.WorkflowConfigV1;
import org.feuyeux.workflow.dag.works.ZeroWork;
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
  @Autowired private WorkflowConfigV1 workflowConfigV1;

  private SequentialFlow sequentialFlow;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
  private final int times = 5;
  private final CountDownLatch doneSignal = new CountDownLatch(times);

  @BeforeEach
  public void init() {
    List<ComponentConfigV1> components = workflowConfigV1.getComponents();
    WorkFlowNode root = DagBuilder.buildTree(components, workMap);
    if (root != null) {
      sequentialFlow = FlowBuilder.buildFlow(root);
    }
  }

  @Test
  public void testLoop() throws InterruptedException {
    if (sequentialFlow != null) {
      IntStream.range(0, 5).forEach(i -> executor.submit(testOne0()));
    } else {
      log.error("sequentialFlow is null");
    }
    executor.shutdown();
    doneSignal.await();
  }

  private Runnable testOne0() {
    return () -> {
      try {
        testOne();
      } catch (Exception e) {
        log.error("", e);
      } finally {
        doneSignal.countDown();
      }
    };
  }

  @Test
  public void testOne() {
    WorkContext workContext = new WorkContext();
    workContext.put("ALWAYS_SUCCESS", "Y");
    workContext.put("request_id", UUID.randomUUID().toString());
    sequentialFlow.execute(workContext);
  }
}
