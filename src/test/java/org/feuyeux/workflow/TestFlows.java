package org.feuyeux.workflow;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.dag.TreeNode;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.workflow.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.feuyeux.workflow.dag.FlowBuilder.buildFlow;
import static org.feuyeux.workflow.flows.ConditionalFlowFactory.buildConditionalFlow;
import static org.feuyeux.workflow.flows.ParallelFlowFactory.buildParallelFlow;
import static org.feuyeux.workflow.flows.RepeatFlowFactory.buildRepeatFlow;
import static org.feuyeux.workflow.flows.SequentialFlowFactory.buildSequentialFlow;

@SpringBootTest
@Slf4j
public class TestFlows {

    @Autowired
    Map<String, ZeroWork> works;
    WorkContext workContext = new WorkContext();

    @Test
    public void testDAG() {
        TreeNode aNode = new TreeNode(works.get("AWork"));
        TreeNode bNode = new TreeNode(works.get("BWork"));
        TreeNode cNode = new TreeNode(works.get("CWork"));
        TreeNode dNode = new TreeNode(works.get("DWork"));
        TreeNode eNode = new TreeNode(works.get("EWork"));
        TreeNode fNode = new TreeNode(works.get("FWork"));
        TreeNode gNode = new TreeNode(works.get("GWork"));
        TreeNode hNode = new TreeNode(works.get("HWork"));
        TreeNode iNode = new TreeNode( works.get("IWork"));
        workContext.put("ALWAYS_SUCCESS", "Y");

        gNode.addChildren(iNode);
        hNode.addChildren(iNode);
        //
        eNode.addChildren(gNode);
        fNode.addChildren(gNode);
        eNode.addChildren(hNode);
        //
        bNode.addChildren(eNode);
        bNode.addChildren(fNode);
        //
        cNode.addChildren(gNode);
        //
        aNode.addChildren(cNode);
        aNode.addChildren(bNode);

        SequentialFlow sequentialFlow = buildFlow(aNode);
        WorkReport workReport = sequentialFlow.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
    }

    @Test
    public void testDAG0() {
        ZeroWork aWork = works.get("AWork");
        ZeroWork bWork = works.get("BWork");
        ZeroWork cWork = works.get("CWork");
        ZeroWork dWork = works.get("DWork");
        ZeroWork eWork = works.get("EWork");
        ZeroWork fWork = works.get("FWork");
        ZeroWork gWork = works.get("GWork");
        ZeroWork hWork = works.get("HWork");
        ZeroWork iWork = works.get("IWork");

        workContext.put("ALWAYS_SUCCESS", "Y");
        workContext.put("hello", "");
        SequentialFlow sequentialFlow = buildSequentialFlow(
                aWork,
                buildParallelFlow(
                        buildSequentialFlow(
                                bWork,
                                buildParallelFlow(
                                        buildSequentialFlow(
                                                eWork,
                                                buildParallelFlow(
                                                        hWork,
                                                        gWork
                                                )
                                        ),
                                        fWork
                                )
                        ),
                        cWork),
                iWork
        );
        WorkReport workReport = sequentialFlow.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
    }

    @Test
    public void test() {

        ZeroWork aWork = works.get("AWork");
        ZeroWork bWork = works.get("BWork");
        ZeroWork cWork = works.get("CWork");

        ConditionalFlow conditionalFlow = buildConditionalFlow(aWork, bWork, cWork);
        SequentialFlow sequentialFlow = buildSequentialFlow(aWork, bWork, cWork);
        RepeatFlow repeatFlow1 = buildRepeatFlow(aWork);
        RepeatFlow repeatFlow2 = buildRepeatFlow(aWork, 3);

        ParallelFlow parallelFlow = buildParallelFlow(aWork, bWork, cWork);

        log.info("=== EXECUTE CONDITIONAL FLOW ===");
        for (int i = 0; i < 3; i++) {
            log.info("{}", i + 1);
            WorkReport workReport = conditionalFlow.execute(workContext);
            log.info("latest flow status:{}", workReport.getStatus());
        }
        log.info("");

        log.info("=== EXECUTE SEQUENTIAL FLOW ===");
        for (int i = 0; i < 3; i++) {
            log.info("{}", i + 1);
            WorkReport workReport = sequentialFlow.execute(workContext);
            log.info("latest flow status:{}", workReport.getStatus());
        }
        log.info("");

        log.info("=== REPEAT UNTIL FLOW ===");
        WorkReport workReport = repeatFlow1.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
        log.info("");

        log.info("=== REPEAT TIMES FLOW ===");
        workReport = repeatFlow2.execute(workContext);
        log.info("latest flow status:{}", workReport.getStatus());
        log.info("");

        log.info("PARALLEL FLOW ===");
        for (int i = 0; i < 3; i++) {
            log.info("{}", i + 1);
            ParallelFlowReport parallelFlowReport = parallelFlow.execute(workContext);
            List<WorkReport> reports = parallelFlowReport.getReports();
            for (WorkReport report : reports) {
                log.info(" status:{}", report.getStatus());
            }
            log.info("parallel flow status:{}", parallelFlowReport.getStatus());
        }
    }
}
