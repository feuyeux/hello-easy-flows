package org.feuyeux.workflow.dag;

import org.feuyeux.workflow.flows.ParallelFlowFactory;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.workflow.ParallelFlow;
import org.jeasy.flows.workflow.SequentialFlow;

import java.util.*;

public class FlowBuilder {
    public static SequentialFlow buildFlow(TreeNode node) {
        SequentialFlow.Builder.NameStep nameStep = SequentialFlow.Builder.aNewSequentialFlow();
        SequentialFlow.Builder.ExecuteStep executeStep = nameStep.named("execute dynamic workflow");
        SequentialFlow.Builder.ThenStep thenStep = null;
        Deque<TreeNode> nodeDeque = new LinkedList<>();
        Map<Integer, ParallelFlow> uniqueFlowMap = new HashMap<>();
        nodeDeque.add(node);
        while (!nodeDeque.isEmpty()) {
            node = nodeDeque.pop();

            if (thenStep == null) {
                ZeroWork zeroWork = node.getZeroWork();
                thenStep = executeStep.execute(zeroWork);
            }

            List<TreeNode> children = node.getChildren();
            ZeroWork[] works = transforming(children);
            int key = buildKey(works);
            ParallelFlow parallelFlow = uniqueFlowMap.get(key);
            if(parallelFlow==null){
                parallelFlow = ParallelFlowFactory.buildParallelFlow(works);
                uniqueFlowMap.put(key,parallelFlow);
            }
            thenStep.then(parallelFlow);
            nodeDeque.addAll(children);
        }
        return thenStep.build();
    }

    private static ZeroWork[] transforming(List<TreeNode> treeNodes) {
        return treeNodes.stream().map(TreeNode::getZeroWork).toArray(ZeroWork[]::new);
    }

    private static int buildKey(ZeroWork... works) {
        int key = 0;
        for (ZeroWork work : works) {
            key += work.getName().hashCode();
        }
        return key;
    }
}
