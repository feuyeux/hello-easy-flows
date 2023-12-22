package org.feuyeux.workflow.dag;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.works.ZeroWork;
import org.feuyeux.workflow.works.Zzz;

import java.util.*;

@Slf4j
public class DagTools {

  public static Deque<Set<ZeroWork>> DFS(TreeNode node) {
    Deque<Set<ZeroWork>> levelDeque = new LinkedList<>();
    levelDeque.push(new HashSet<>());
    Deque<TreeNode> walkingDeque = new LinkedList<>();
    List<TreeNode> visited = new ArrayList<>();
    List<TreeNode> levelNodes = new ArrayList<>();
    Map<TreeNode, Integer> degreeMap = new HashMap<>();

    // 有个假设，就是DAG的根节点是唯一的
    walkingDeque.add(node);

    while (!walkingDeque.isEmpty()) {
      node = walkingDeque.pop();
      ZeroWork work = node.getZeroWork();
      if ("Zzz".equals(work.getName())) {
        levelNodes.clear();
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
      if (nodeIns != null && nodeIns > 0) {
        log.debug("ignore:{} {}", node, nodeIns);
      } else {
        levelNodes.add(node);
        visited.add(node);
        //当前元素添加到这一层的数组中
        levelDeque.peek().add(work);
      }
      if (!walkingDeque.isEmpty()) {
        //同层还有元素未处理 先处理同层元素 自己的子节点元素后续处理
        continue;
      }

      //这一层的全部子节点都过一遍
      for (TreeNode n : levelNodes) {
        for (TreeNode child : n.getChildren()) {
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

      walkingDeque.push(new TreeNode(new Zzz()));

      // 这一层的数组中如果没有元素就丢弃
      if (levelDeque.peek().isEmpty()) {
        levelDeque.pop();
      }
    }
    if (levelDeque.peek().isEmpty()) {
      levelDeque.pop();
    }

    Optional<Integer> optional = levelDeque.stream().map(Set::size).reduce(Integer::sum);
    log.info("queue[levels:{},total:{}]:{}", levelDeque.size(), optional.isPresent() ? optional.get() : "?", levelDeque);
    return levelDeque;
  }
}
