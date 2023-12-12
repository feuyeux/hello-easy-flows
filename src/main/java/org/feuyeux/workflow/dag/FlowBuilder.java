package org.feuyeux.workflow.dag;

import org.feuyeux.workflow.flows.ParallelFlowFactory;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.workflow.ParallelFlow;
import org.jeasy.flows.workflow.SequentialFlow;

import java.util.*;

public class FlowBuilder {

    //TODO
    public static SequentialFlow buildFlow(TreeNode node) {
        SequentialFlow.Builder.NameStep nameStep = SequentialFlow.Builder.aNewSequentialFlow();
        SequentialFlow.Builder.ExecuteStep executeStep = nameStep.named("execute dynamic workflow");
        SequentialFlow.Builder.ThenStep thenStep = null;
        Deque<TreeNode> nodeDeque = new LinkedList<>();
        Map<Integer, ZeroWork> flowMap = new HashMap<>();
        nodeDeque.add(node);
        while (!nodeDeque.isEmpty()) {
            node = nodeDeque.pop();
            List<TreeNode> children = node.getChildren();
            if (thenStep == null) {
                ZeroWork zeroWork = node.getZeroWork();
                thenStep = executeStep.execute(zeroWork);
            }
            ZeroWork[] works = transforming(children);
            if (works.length == 1) {
                thenStep.then(works[0]);
            } else {
                ParallelFlow parallelFlow = ParallelFlowFactory.buildParallelFlow(works);
                thenStep.then(parallelFlow);
            }
            nodeDeque.addAll(children);
        }
        return thenStep.build();
    }

    private static ZeroWork[] transforming(List<TreeNode> treeNodes) {
        return treeNodes.stream().map(TreeNode::getZeroWork).toArray(ZeroWork[]::new);
    }
}
