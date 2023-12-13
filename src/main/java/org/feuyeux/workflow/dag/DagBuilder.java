package org.feuyeux.workflow.dag;

import org.feuyeux.workflow.config.ComponentConfig;
import org.feuyeux.workflow.config.WorkflowConfig;
import org.feuyeux.workflow.works.ZeroWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DagBuilder {
    @Autowired
    private WorkflowConfig workflowConfig;
    @Autowired
    private Map<String, ZeroWork> works;

    public TreeNode buildTree() {
        Map<String, TreeNode> nodeMap = new HashMap<>();
        List<ComponentConfig> configs = workflowConfig.getComponents();
        TreeNode root = null;
        for (ComponentConfig config : configs) {
            String nodeName = config.getName();
            ZeroWork zeroWork = works.get(nodeName);
            TreeNode node = new TreeNode(zeroWork);
            nodeMap.put(nodeName, node);
            List<String> dependencies = config.getDependency();
            //假设只有(唯一的)根节点没有依赖
            if (dependencies == null || dependencies.isEmpty()) {
                root = node;
            }
            for (String dependency : dependencies) {
                TreeNode treeNode = nodeMap.get(dependency);
                treeNode.addChildren(node);
            }
        }
        return root;
    }
}
