package org.feuyeux.workflow.dag;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.flows.ParallelFlowFactory;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.workflow.ParallelFlow;
import org.jeasy.flows.workflow.SequentialFlow;

@Slf4j
public class FlowBuilder {
  private static final List<String> builtList = new ArrayList<>();

  public static SequentialFlow buildFlow(TreeNode node) {
    builtList.clear();
    Map<Integer, Set<TreeNode>> levelMap = buildLevelSet(node);
    if (levelMap.isEmpty()) {
      return null;
    }
    return buildFlow(node, node.getUnion(), levelMap, 0, 0);
  }

  private static SequentialFlow buildFlow(
      TreeNode node, String unionContext, Map<Integer, Set<TreeNode>> levelMap, int x0, int y0) {
    SequentialFlow.Builder.NameStep nameStep = SequentialFlow.Builder.aNewSequentialFlow();
    SequentialFlow.Builder.ExecuteStep executeStep = nameStep.named("sequential-flow-" + genId());
    SequentialFlow.Builder.ThenStep rootStep = null;
    int x = levelMap.keySet().size();
    for (int i = x0; i < x; i++) {
      Set<TreeNode> levelNodes = levelMap.get(i);
      if (rootStep == null) {
        ZeroWork zeroWork = node.getZeroWork();
        rootStep = executeStep.execute(zeroWork);
        if (node.isEnd()) {
          break;
        }
      } else {
        if (levelNodes.size() == 1) {
          Optional<TreeNode> optional = levelNodes.stream().filter(Objects::nonNull).findFirst();
          if (optional.isPresent()) {
            if (checkAndMark(i, 0)) {
              continue;
            }
            TreeNode currentNode = optional.get();
            String nodeUnion = currentNode.getUnion();
            if (nodeUnion != null && !nodeUnion.equals(unionContext)) {
              rootStep.then(buildFlow(currentNode, nodeUnion, levelMap, i + 1, 0));
            } else {
              rootStep.then(currentNode.getZeroWork());
            }
            if (currentNode.isEnd()) {
              break;
            }
          }
        } else {
          List<String> unionList = levelNodes.stream().map(TreeNode::getUnion).toList();
          boolean noUnion = unionList.stream().allMatch(Objects::isNull);
          boolean oneUnion = unionList.stream().distinct().count() == 1;
          Set<ZeroWork> zeroWorks =
              levelNodes.stream().map(TreeNode::getZeroWork).collect(Collectors.toSet());
          if (noUnion || oneUnion) {
            if (checkAndMark(i, 0)) {
              continue;
            }
            ParallelFlow parallelFlow = ParallelFlowFactory.buildParallelFlow(zeroWorks);
            rootStep.then(parallelFlow);
          } else {
            ParallelFlow.Builder.NameStep pNameStep = ParallelFlow.Builder.aNewParallelFlow();
            ParallelFlow.Builder.ExecuteStep pExecuteStep =
                pNameStep.named("parallel-flow-" + genId());
            ParallelFlow.Builder.WithStep pWithStep = null;
            int y = levelNodes.size();
            List<TreeNode> nodeList = levelNodes.stream().toList();
            for (int j = y0; j < y; j++) {
              TreeNode levelNode = nodeList.get(j);
              if (checkAndMark(i, j)) {
                continue;
              }
              String levelNodeUnion = levelNode.getUnion();
              if (levelNodeUnion != null && !levelNodeUnion.equals(unionContext)) {
                SequentialFlow sequentialFlow =
                    buildFlow(levelNode, levelNodeUnion, levelMap, i, j + 1);
                pWithStep = pExecuteStep.execute(sequentialFlow);
              } else {
                pWithStep = pExecuteStep.execute(levelNode.getZeroWork());
              }
            }
            if (pWithStep != null) {
              ParallelFlow parallelFlow =
                  pWithStep.with(ParallelFlowFactory.executorService).build();
              rootStep.then(parallelFlow);
            }
            if (nodeList.get(y - 1).isEnd()) {
              break;
            }
          }
        }
      }
    }
    return rootStep == null ? null : rootStep.build();
  }

  private static boolean checkAndMark(int i, int j) {
    String key = i + "," + j;
    if (builtList.contains(key)) {
      return true;
    }
    builtList.add(key);
    return false;
  }

  private static Map<Integer, Set<TreeNode>> buildLevelSet(TreeNode node) {
    Map<Integer, Set<TreeNode>> levelMap = new HashMap<>();
    Deque<Set<TreeNode>> levelDeque = DagTools.DFS(node);
    int index = levelDeque.size() - 1;
    for (Set<TreeNode> workNodes : levelDeque) {
      Set<TreeNode> zeroWorks = new HashSet<>();
      boolean flag = false;
      for (TreeNode zeroWork : workNodes) {
        // 如果当前节点已经存在于某个level中，则将当前level的所有节点加入到该level中
        Optional<Integer> existInLevel =
            levelMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(zeroWork))
                .map(Map.Entry::getKey)
                .findFirst();
        if (existInLevel.isPresent()) {
          Integer key = existInLevel.get();
          flag = true;
          levelMap.get(key).addAll(workNodes);
          break;
        } else {
          zeroWorks.add(zeroWork);
        }
      }
      if (flag) {
        continue;
      }
      levelMap.put(index--, zeroWorks);
    }
    return levelMap;
  }

  private static String genId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
