package org.feuyeux.workflow.dag;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.works.ZeroWork;

@Data
@Slf4j
public class WorkFlowNode {

  public static final String Zzz = "Zzz";
  private ZeroWork zeroWork;
  private WorkFlowNode parent;
  private int inDegree;
  private String union;
  private boolean end;
  private Set<WorkFlowNode> children = new HashSet<>();
  @Getter private NodeType type;

  public WorkFlowNode(ZeroWork zeroWork) {
    this.zeroWork = zeroWork;
  }

  public void addEdge(ZeroWork... works) {
    for (ZeroWork work : works) {
      if (work == null) {
        log.error("work is null");
        continue;
      }
      WorkFlowNode child = new WorkFlowNode(work);
      child.setParent(this);
      child.addInDegree();
      children.add(child);
    }
  }

  public void addInDegree() {
    inDegree += 1;
  }

  public void addEdge(WorkFlowNode... nodes) {
    for (WorkFlowNode node : nodes) {
      node.setParent(this);
      node.addInDegree();
      children.add(node);
    }
  }

  public WorkFlowNode setType(NodeType type) {
    this.type = type;
    return this;
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
    if (obj instanceof WorkFlowNode node) {
      return zeroWork.getName().equals(node.getZeroWork().getName());
    }
    return false;
  }
}
