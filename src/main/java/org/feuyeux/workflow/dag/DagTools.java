package org.feuyeux.workflow.dag;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.dag.works.ZeroWork;
import org.feuyeux.workflow.dag.works.Zzz;

@Slf4j
public class DagTools {

  public static Deque<Set<WorkFlowNode>> dfs(WorkFlowNode node) {
    Deque<Set<WorkFlowNode>> levelDeque = new LinkedList<>();
    levelDeque.push(new HashSet<>());
    Deque<WorkFlowNode> walkingDeque = new LinkedList<>();
    List<WorkFlowNode> visited = new ArrayList<>();
    List<WorkFlowNode> levelNodes = new ArrayList<>();
    Map<WorkFlowNode, Integer> degreeMap = new HashMap<>();

    // 有个假设，就是DAG的根节点是唯一的
    walkingDeque.add(node);

    while (!walkingDeque.isEmpty()) {
      node = walkingDeque.pop();
      ZeroWork work = node.getZeroWork();
      if (WorkFlowNode.Zzz.equals(work.getName())) {
        levelNodes.clear();
        // 为新层级创建数组
        if (levelDeque.peek() != null && !levelDeque.peek().isEmpty()) {
          levelDeque.push(new HashSet<>());
        }
        continue;
      }
      if (visited.contains(node)) {
        continue;
      }
      Integer nodeIns = degreeMap.get(node);
      if (nodeIns != null && nodeIns > 0) {
        log.debug("ignore:{} {}", node, nodeIns);
      } else {
        levelNodes.add(node);
        visited.add(node);
        // 当前元素添加到这一层的数组中
        if (levelDeque.peek() != null) {
          levelDeque.peek().add(node);
        }
      }
      if (!walkingDeque.isEmpty()) {
        // 同层还有元素未处理 先处理同层元素 自己的子节点元素后续处理
        continue;
      }

      // 这一层的全部子节点都过一遍
      for (WorkFlowNode n : levelNodes) {
        for (WorkFlowNode child : n.getChildren()) {
          if (!walkingDeque.contains(child)) {
            walkingDeque.push(child);
          }
          Integer ins;
          try {
            ins = degreeMap.get(child);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
          if (ins == null) {
            int inDegree = child.getInDegree();
            degreeMap.put(child, inDegree - 1);
          } else {
            degreeMap.put(child, ins - 1);
          }
        }
      }

      walkingDeque.push(new WorkFlowNode(new Zzz()));

      // 这一层的数组中如果没有元素就丢弃
      if (levelDeque.peek() != null && levelDeque.peek().isEmpty()) {
        levelDeque.pop();
      }
    }
    if (levelDeque.peek() != null && levelDeque.peek().isEmpty()) {
      levelDeque.pop();
    }
    if (log.isInfoEnabled()) {
      Optional<Integer> optional = levelDeque.stream().map(Set::size).reduce(Integer::sum);
      log.info(
          "dag queue[levels:{},total:{}]:\n{}",
          levelDeque.size(),
          optional.isPresent() ? optional.get() : "?",
          levelDeque);
    }
    return levelDeque;
  }
}
