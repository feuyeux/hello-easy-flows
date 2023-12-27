package org.feuyeux.workflow.dag;

import static org.feuyeux.workflow.dag.NodeType.*;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.flows.ParallelFlowFactory;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.workflow.ParallelFlow;
import org.jeasy.flows.workflow.SequentialFlow;

@Slf4j
public class FlowBuilder {
  private static final List<WorkFlowNode> visits = new ArrayList<>();
  private static final Map<String, SequentialFlow.Builder.ThenStep> stepMap = new HashMap<>();

  public static SequentialFlow buildFlow(WorkFlowNode node) {
    visits.clear();
    stepMap.clear();
    Map<Integer, Set<WorkFlowNode>> levelMap = buildLevelSet(node);
    if (levelMap.isEmpty()) {
      return null;
    }
    SequentialFlow flow = buildFlow(node, node.getUnion(), levelMap, 0);
    if (log.isInfoEnabled()) {
      StringBuilder flows = new StringBuilder();
      for (WorkFlowNode visit : visits) {
        switch (visit.getType()) {
          case ROOT:
            flows.append("[<").append(visit).append("> ");
            break;
          case PARALLEL:
            if (visit.isEnd()) {
              flows.append("|").append(visit).append("|] ");
            } else {
              flows.append("|").append(visit).append("| ");
            }
            break;
          case SEQUENTIAL:
            if (visit.isEnd()) {
              flows.append("<").append(visit).append(">] ");
            } else {
              flows.append("<").append(visit).append("> ");
            }
            break;
          default:
            break;
        }
      }
      log.info("dag flow[total:{}]:\n{}", visits.size(), flows);
    }
    return flow;
  }

  private static SequentialFlow buildFlow(
      WorkFlowNode node, String unionContext, Map<Integer, Set<WorkFlowNode>> levelMap, int x0) {
    SequentialFlow.Builder.NameStep nameStep = SequentialFlow.Builder.aNewSequentialFlow();
    SequentialFlow.Builder.ExecuteStep executeStep = nameStep.named("sequential-flow-" + genId());
    SequentialFlow.Builder.ThenStep rootStep = getRootStep(unionContext);
    int x = levelMap.keySet().size();
    for (int i = x0; i < x; i++) {
      Set<WorkFlowNode> levelNodes = levelMap.get(i);
      if (rootStep == null) {
        if (isSame(node.getUnion(), unionContext)) {
          if (visits.contains(node)) {
            continue;
          }
          visits.add(node.setType(ROOT));
          ZeroWork zeroWork = node.getZeroWork();
          rootStep = executeStep.execute(zeroWork);
          setRootStep(unionContext, rootStep);
          if (node.isEnd()) {
            break;
          }
        }
      } else {
        if (levelNodes.size() == 1) {
          Optional<WorkFlowNode> optional =
              levelNodes.stream().filter(Objects::nonNull).findFirst();
          if (optional.isPresent()) {
            WorkFlowNode currentNode = optional.get();
            if (visits.contains(currentNode)) {
              continue;
            }
            if (isSame(currentNode.getUnion(), unionContext)) {
              String nodeUnion = currentNode.getUnion();
              if (nodeUnion != null && !nodeUnion.equals(unionContext)) {
                rootStep.then(buildFlow(currentNode, nodeUnion, levelMap, i));
              } else {
                visits.add(currentNode.setType(SEQUENTIAL));
                rootStep.then(currentNode.getZeroWork());
              }
              if (currentNode.isEnd()) {
                break;
              }
            }
          }
        } else {
          List<String> unionList = levelNodes.stream().map(WorkFlowNode::getUnion).toList();
          boolean noUnion = unionList.stream().allMatch(Objects::isNull);
          boolean oneUnion = unionList.stream().distinct().count() == 1;
          Set<ZeroWork> zeroWorks =
              levelNodes.stream().map(WorkFlowNode::getZeroWork).collect(Collectors.toSet());
          if (noUnion || oneUnion) {
            boolean hasVisited =
                levelNodes.stream()
                    .map(
                        n -> {
                          if (visits.contains(n)) {
                            return false;
                          }
                          visits.add(n.setType(PARALLEL));
                          return true;
                        })
                    .anyMatch(f -> !f);
            if (hasVisited) {
              continue;
            }
            ParallelFlow parallelFlow = ParallelFlowFactory.buildParallelFlow(zeroWorks);
            rootStep.then(parallelFlow);
            if (levelNodes.stream().anyMatch(WorkFlowNode::isEnd)) {
              break;
            }
          } else {
            ParallelFlow.Builder.NameStep pNameStep = ParallelFlow.Builder.aNewParallelFlow();
            ParallelFlow.Builder.ExecuteStep pExecuteStep =
                pNameStep.named("parallel-flow-" + genId());
            ParallelFlow.Builder.WithStep pWithStep = null;
            int y = levelNodes.size();
            List<WorkFlowNode> nodeList = levelNodes.stream().toList();
            long count = 0;
            for (int j = 0; j < y; j++) {
              WorkFlowNode levelNode = nodeList.get(j);
              if (isSame(levelNode.getUnion(), unionContext)) {
                if (visits.contains(levelNode)) {
                  continue;
                }
                String levelNodeUnion = levelNode.getUnion();
                if (levelNodeUnion != null && !levelNodeUnion.equals(unionContext)) {
                  SequentialFlow sequentialFlow = buildFlow(levelNode, levelNodeUnion, levelMap, i);
                  pWithStep = pExecuteStep.execute(sequentialFlow);
                } else {
                  visits.add(levelNode.setType(PARALLEL));
                  pWithStep = pExecuteStep.execute(levelNode.getZeroWork());
                }
                if (levelNode.isEnd()) {
                  count++;
                }
              }
            }
            if (pWithStep != null) {
              ParallelFlow parallelFlow =
                  pWithStep.with(ParallelFlowFactory.executorService).build();
              rootStep.then(parallelFlow);
            }
            if (count > 0) {
              break;
            }
          }
        }
      }
    }
    return rootStep == null ? null : rootStep.build();
  }

  private static SequentialFlow.Builder.ThenStep getRootStep(String unionContext) {
    if (unionContext == null) {
      unionContext = "DEFAULT";
    }
    return stepMap.get(unionContext);
  }

  private static void setRootStep(String unionContext, SequentialFlow.Builder.ThenStep rootStep) {
    if (unionContext == null) {
      unionContext = "DEFAULT";
    }
    stepMap.put(unionContext, rootStep);
  }

  private static boolean isSame(String nodeUnion, String contextUnion) {
    if (contextUnion == null) {
      return true;
    } else {
      if (nodeUnion == null) {
        return false;
      } else {
        return contextUnion.equals(nodeUnion);
      }
    }
  }

  private static Map<Integer, Set<WorkFlowNode>> buildLevelSet(WorkFlowNode node) {
    Map<Integer, Set<WorkFlowNode>> levelMap = new HashMap<>();
    Deque<Set<WorkFlowNode>> levelDeque = DagTools.DFS(node);
    int index = levelDeque.size() - 1;
    for (Set<WorkFlowNode> workNodes : levelDeque) {
      Set<WorkFlowNode> zeroWorks = new HashSet<>();
      boolean flag = false;
      for (WorkFlowNode zeroWork : workNodes) {
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
