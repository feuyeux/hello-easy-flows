package org.feuyeux.workflow.dag;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.works.ZeroWork;

@Data
@Slf4j
public class TreeNode {

  public static final String Zzz = "Zzz";
  private ZeroWork zeroWork;
  private TreeNode parent;
  private int inDegree;
  private String union;
  private boolean end;
  private Set<TreeNode> children = new HashSet<>();

  public TreeNode(ZeroWork zeroWork) {
    this.zeroWork = zeroWork;
  }

  public void addEdge(ZeroWork... works) {
    for (ZeroWork work : works) {
      if (work == null) {
        log.error("work is null");
        continue;
      }
      TreeNode child = new TreeNode(work);
      child.setParent(this);
      child.addInDegree();
      children.add(child);
    }
  }

  public void addInDegree() {
    inDegree += 1;
  }

  public void addEdge(TreeNode... nodes) {
    for (TreeNode node : nodes) {
      node.setParent(this);
      node.addInDegree();
      children.add(node);
    }
  }

  @Override
  public String toString() {
    if (zeroWork == null) {
      return Zzz;
    }
    return zeroWork.toString();
  }

  @Override
  public int hashCode() {
    if (zeroWork == null) {
      return Zzz.hashCode();
    }
    return zeroWork.toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TreeNode node) {
      return zeroWork.getName().equals(node.getZeroWork().getName());
    }
    return false;
  }
}
