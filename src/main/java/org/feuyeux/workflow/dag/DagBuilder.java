package org.feuyeux.workflow.dag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.config.ComponentConfig;
import org.feuyeux.workflow.works.ZeroWork;

@Slf4j
public class DagBuilder {

  public static TreeNode buildTree(List<ComponentConfig> configs, Map<String, ZeroWork> workMap) {
    Map<String, TreeNode> nodeMap = new HashMap<>();
    TreeNode root = null;
    for (ComponentConfig config : configs) {
      String nodeName = config.getName();
      ZeroWork zeroWork = workMap.get(nodeName);
      TreeNode node = new TreeNode(zeroWork);
      nodeMap.put(nodeName, node);
      List<String> dependencies = config.getDependency();
      //假设只有(唯一的)根节点没有依赖
      if (dependencies == null || dependencies.isEmpty()) {
        root = node;
      }
      for (String dependency : dependencies) {
        TreeNode treeNode = nodeMap.get(dependency);
        treeNode.addEdge(node);
      }
    }
    return root;
  }
}

