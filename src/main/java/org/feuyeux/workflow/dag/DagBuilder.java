package org.feuyeux.workflow.dag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.dag.config.ComponentConfigV1;
import org.feuyeux.workflow.dag.works.ZeroWork;

@Slf4j
public class DagBuilder {

  public static WorkFlowNode buildTree(
      List<ComponentConfigV1> configs, Map<String, ZeroWork> workMap) {
    Map<String, WorkFlowNode> nodeMap = new HashMap<>();
    WorkFlowNode root = null;
    for (ComponentConfigV1 config : configs) {
      String nodeName = config.getName();
      ZeroWork zeroWork = workMap.get(nodeName);
      WorkFlowNode node = new WorkFlowNode(zeroWork);
      nodeMap.put(nodeName, node);
      List<String> dependencies = config.getDependency();
      // 假设只有(唯一的)根节点没有依赖
      if (dependencies == null || dependencies.isEmpty()) {
        root = node;
      } else {
        log.debug("dependencies:{}", dependencies);
        for (String dependency : dependencies) {
          WorkFlowNode workFlowNode = nodeMap.get(dependency);
          workFlowNode.addEdge(node);
        }
      }
      node.setUnion(config.getUnion());
      node.setEnd(config.isEnd());
    }
    return root;
  }
}
