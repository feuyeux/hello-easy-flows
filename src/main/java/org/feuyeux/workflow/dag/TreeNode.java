package org.feuyeux.workflow.dag;

import lombok.Data;
import org.feuyeux.workflow.works.ZeroWork;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    private ZeroWork zeroWork;
    private TreeNode parent;
    private List<TreeNode> children = new ArrayList<>();

    public TreeNode(ZeroWork zeroWork) {
        this.zeroWork = zeroWork;
    }

    public void addChildren(ZeroWork... works) {
        for (ZeroWork work : works) {
            TreeNode child = new TreeNode(work);
            child.setParent(this);
            children.add(child);
        }
    }

    public void addChildren(TreeNode... nodes) {
        for (TreeNode node : nodes) {
            node.setParent(this);
            children.add(node);
        }
    }

    @Override
    public String toString() {
        return zeroWork.getName();
    }

    @Override
    public int hashCode() {
        return zeroWork.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TreeNode) {
            TreeNode node = (TreeNode) obj;
            return zeroWork.getName().equals(node.getZeroWork().getName());
        }
        return false;
    }
}