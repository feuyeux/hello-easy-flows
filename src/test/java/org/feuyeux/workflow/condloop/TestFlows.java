package org.feuyeux.workflow.condloop;

import static org.feuyeux.workflow.flows.ParallelFlowFactory.buildParallelFlow;
import static org.feuyeux.workflow.flows.RepeatFlowFactory.buildRepeatFlow;
import static org.jeasy.flows.workflow.RepeatFlow.Builder.aNewRepeatFlow;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.workflow.HelloEasyFlows;
import org.feuyeux.workflow.condloop.works.OneStatusWork;
import org.feuyeux.workflow.condloop.works.ThreeStatusWork;
import org.feuyeux.workflow.condloop.works.TimesWork;
import org.feuyeux.workflow.condloop.works.TwoStatusWork;
import org.jeasy.flows.work.Work;
import org.jeasy.flows.work.WorkContext;
import org.jeasy.flows.work.WorkReport;
import org.jeasy.flows.work.WorkReportPredicate;
import org.jeasy.flows.workflow.ConditionalFlow;
import org.jeasy.flows.workflow.RepeatFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HelloEasyFlows.class)
@DisplayName("easy-flows tests")
@Slf4j
public class TestFlows {

  private final WorkContext workContext = new WorkContext();

  OneStatusWork a1, b, c;
  TwoStatusWork a2;
  TimesWork a3;

  @BeforeEach
  public void init() {
    a1 = new OneStatusWork("A1", 100);
    a2 = new TwoStatusWork("A2", 1000);
    b = new OneStatusWork("B", 200);
    c = new OneStatusWork("C", 200);
    a3 = new TimesWork("A3", 10, 3);
  }

  @Test
  public void testConditional() {
    ConditionalFlow conditionalFlow =
        ConditionalFlow.Builder.aNewConditionalFlow()
            .named("conditional_flow")
            .execute(buildParallelFlow(a1, a2))
            .when(WorkReportPredicate.COMPLETED)
            .then(b)
            .otherwise(c)
            .build();
    for (int i = 0; i < 3; i++) {
      WorkReport workReport = conditionalFlow.execute(workContext);
      log.info("latest flow status:{}", workReport.getStatus());
      log.info("--------------------");
    }
  }

  @Test
  public void testRepeatTimes() {
    // given
    int n = 3;
    log.info("WorkerX will repeat always {} times, whether status is COMPLETED or FAILED.", n);
    Work work =
        Mockito.mock(
            TwoStatusWork.class,
            Mockito.withSettings()
                .useConstructor("WorkerX", 1000)
                .defaultAnswer(Mockito.CALLS_REAL_METHODS));
    RepeatFlow repeatFlow = buildRepeatFlow(work, n);
    // when
    repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.times(n)).execute(workContext);
  }

  @Test
  public void testRepeatUntil() {
    // given
    log.info("WorkerX will repeat until the status is FAILED.");
    Work work =
        Mockito.mock(
            TwoStatusWork.class,
            Mockito.withSettings()
                .useConstructor("WorkerX", 1000)
                .defaultAnswer(Mockito.CALLS_REAL_METHODS));
    RepeatFlow repeatFlow = buildRepeatFlow(work, WorkReportPredicate.COMPLETED);
    // when
    repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.atLeastOnce()).execute(workContext);
  }

  @Test
  public void testRepeatTimesUntil() {
    // given
    int n = 3;
    log.info("WorkerX will repeat at most {} times if status is COMPLETED.", n);

    Work work =
        Mockito.mock(
            ThreeStatusWork.class,
            Mockito.withSettings()
                .useConstructor("WorkerX", 1000, n)
                .defaultAnswer(Mockito.CALLS_REAL_METHODS));
    RepeatFlow repeatFlow =
        aNewRepeatFlow().repeat(work).until(WorkReportPredicate.COMPLETED).build();
    // when
    repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.atMost(n)).execute(workContext);
    Mockito.verify(work, Mockito.atLeastOnce()).execute(workContext);
  }
}
