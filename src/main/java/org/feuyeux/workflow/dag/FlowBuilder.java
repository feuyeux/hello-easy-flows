package org.feuyeux.workflow.dag;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.flows.ParallelFlowFactory;
import org.feuyeux.workflow.works.ZeroWork;
import org.jeasy.flows.workflow.ParallelFlow;
import org.jeasy.flows.workflow.SequentialFlow;

@Slf4j
public class FlowBuilder {

  public static SequentialFlow buildFlow(TreeNode node) {
    Map<Integer, Set<ZeroWork>> levelMap = buildLevelSet(node);
    if (levelMap.isEmpty()) {
      return null;
    }
    SequentialFlow.Builder.NameStep nameStep = SequentialFlow.Builder.aNewSequentialFlow();
    SequentialFlow.Builder.ExecuteStep executeStep = nameStep.named("execute dynamic workflow");
    SequentialFlow.Builder.ThenStep thenStep = null;
    for (Set<ZeroWork> zeroWorks : levelMap.values()) {
      if (thenStep == null) {
        ZeroWork zeroWork = node.getZeroWork();
        thenStep = executeStep.execute(zeroWork);
      } else {
        if (zeroWorks.size() == 1) {
          Optional<ZeroWork> optional = zeroWorks.stream().filter(Objects::nonNull).findFirst();
          if (optional.isPresent()) {
            thenStep.then(optional.get());
          }
        } else {
          ParallelFlow parallelFlow = ParallelFlowFactory.buildParallelFlow(zeroWorks);
          thenStep.then(parallelFlow);
        }
      }
    }
    return thenStep == null ? null : thenStep.build();
  }

  private static Map<Integer, Set<ZeroWork>> buildLevelSet(TreeNode node) {
    Deque<Set<ZeroWork>> levelDeque = DagTools.DFS(node);
    Map<Integer, Set<ZeroWork>> levelMap = new HashMap<>();
    int index = levelDeque.size() - 1;
    for (Set<ZeroWork> works : levelDeque) {
      Set<ZeroWork> zeroWorks = new HashSet<>();
      boolean flag = false;
      for (ZeroWork zeroWork : works) {
        // 如果当前节点已经存在于某个level中，则将当前level的所有节点加入到该level中
        Optional<Integer> existInLevel = levelMap.entrySet().stream()
            .filter(entry -> entry.getValue().contains(zeroWork)).map(
                Map.Entry::getKey).findFirst();
        if (existInLevel.isPresent()) {
          Integer key = existInLevel.get();
          flag = true;
          levelMap.get(key).addAll(works);
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
}
