package org.feuyeux.workflow;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.dag.DagBuilder;
import org.feuyeux.workflow.dag.TreeNode;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.SequentialFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.feuyeux.workflow.dag.FlowBuilder.buildFlow;

@SpringBootTest
@Slf4j
public class TestDag {
    @Autowired
    private Map<String, ZeroWork> works;
    private WorkContext workContext;
    @Autowired
    private DagBuilder dagBuilder;

    private SequentialFlow sequentialFlow;

    @BeforeEach
    public void init() {
        workContext = new WorkContext();
        workContext.put("ALWAYS_SUCCESS", "Y");
        TreeNode root = dagBuilder.buildTree();
        if (root != null) {
            sequentialFlow = buildFlow(root);
        }
    }

    @Test
    public void testDAG() {
        if (sequentialFlow != null) {
            WorkReport workReport = sequentialFlow.execute(workContext);
            log.info("latest flow status:{}", workReport.getStatus());
        } else {
            log.error("sequentialFlow is null");
        }
    }
}
