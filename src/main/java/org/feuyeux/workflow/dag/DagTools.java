package org.feuyeux.workflow.dag;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.works.ZeroWork;
import org.feuyeux.workflow.works.Zzz;

@Slf4j
public class DagTools {

  public static Deque<Set<ZeroWork>> DFS(TreeNode node) {
    Deque<Set<ZeroWork>> levelDeque = new LinkedList<>();
    levelDeque.push(new HashSet<>());
    Deque<TreeNode> walkingDeque = new LinkedList<>();
    List<TreeNode> visited = new ArrayList<>();
    Map<TreeNode, Integer> degreeMap = new HashMap<>();

    // 有个假设，就是DAG的根节点是唯一的
    walkingDeque.add(node);

    while (!walkingDeque.isEmpty()) {
      node = walkingDeque.pop();
      ZeroWork zeroWork = node.getZeroWork();
      if ("Zzz".equals(zeroWork.getName())) {
        //为新层级创建数组
        if (!levelDeque.peek().isEmpty()) {
          levelDeque.push(new HashSet<>());
        }
        continue;
      }
      if (visited.contains(node)) {
        continue;
      }

      Integer nodeIns = degreeMap.get(node);
      if (nodeIns != null && nodeIns > 1) {
        log.warn("ignore:{} {}", node, nodeIns);
      } else {
        visited.add(node);
        //当前元素添加到这一层的数组中
        levelDeque.peek().add(zeroWork);
      }
      if (!walkingDeque.isEmpty()) {
        //同层还有元素未处理
        continue;
      }
      for (TreeNode child : node.getChildren()) {
        walkingDeque.push(child);
        Integer ins = null;
        try {
          ins = degreeMap.get(child);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        if (ins == null) {
          int inDegree = child.getInDegree();
          degreeMap.put(child, inDegree - 1);
        } else {
          degreeMap.put(node, ins - 1);
        }
      }
      walkingDeque.push(new TreeNode(new Zzz()));
      // 这一层的数组中如果没有元素就丢弃
      if (levelDeque.peek().isEmpty()) {
        levelDeque.pop();
      }
    }
    if (levelDeque.peek().isEmpty()) {
      levelDeque.pop();
    }
    log.info("{}", levelDeque);
    return levelDeque;
  }
}
