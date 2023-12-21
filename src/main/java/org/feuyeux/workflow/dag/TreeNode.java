package org.feuyeux.workflow.dag;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.feuyeux.workflow.works.ZeroWork;

@Data
public class TreeNode {

  public static final String Zzz = "Zzz";
  private ZeroWork zeroWork;
  private TreeNode parent;
  private int inDegree;
  private List<TreeNode> children = new ArrayList<>();

  public TreeNode(ZeroWork zeroWork) {
    this.zeroWork = zeroWork;
  }

  public void addEdge(ZeroWork... works) {
    for (ZeroWork work : works) {
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
    if(zeroWork==null){
      return Zzz;
    }
    return zeroWork.getName();
  }

  @Override
  public int hashCode() {
    if(zeroWork==null){
      return Zzz.hashCode();
    }
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