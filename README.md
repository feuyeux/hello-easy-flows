# Hello Easy Flows

## 1 TEMPLATE YAML -> DAG -> FLOW

### TEMPLATE YAML

```yaml
workflow:
  components:
    - name: A
      dependency:
    - name: S
      dependency: A
      union: S
    - name: T
      dependency: S
      union: S
      end: true
    - name: B
      dependency: A
    - name: C
      dependency: A
    - name: E
      dependency: B
    - name: D
      dependency: B
    - name: F
      dependency: D,E
    - name: G
      dependency: F
    - name: H
      dependency: F
      union: H
    - name: J
      dependency: H,C
      union: H
    - name: L
      dependency: J,D
      union: H
      end: true
    - name: K
      dependency: J
      union: H
      end: true
    - name: M
      dependency: L,K,T
      union: M
    - name: N
      dependency: M
      union: M
    - name: P
      dependency: N
      union: M
    - name: Q
      dependency: P
      union: M
      end: true
    - name: R
      dependency: Q,G
```

### DAG

```sh
dag queue[levels:12,total:18]:
[[RWork(R)], [QWork(Q)], [PWork(P)], [NWork(N)], [MWork(M)], [LWork(L), KWork(K)], [JWork(J)], [GWork(G), HWork(H)], [FWork(F)], [EWork(E), DWork(D), TWork(T)], [BWork(B), CWork(C), SWork(S)], [AWork(A)]]
```

![](doc/dag.drawio.png)

### FLOW

```sh
dag flow[total:18]:
[<AWork(A)> |BWork(B)| |CWork(C)| [<SWork(S)> |TWork(T)|] |EWork(E)| |DWork(D)| <FWork(F)> |GWork(G)| [<HWork(H)> <JWork(J)> |LWork(L)|] |KWork(K)|] [<MWork(M)> <NWork(N)> <PWork(P)> <QWork(Q)>] <RWork(R)>
```

### UT

```sh
$ mvn test -D test=org.feuyeux.workflow.dag.TestAll#testOne

11:25:11.977 dag queue[levels:12,total:18]:
[[RWork(R)], [QWork(Q)], [PWork(P)], [NWork(N)], [MWork(M)], [LWork(L), KWork(K)], [JWork(J)], [GWork(G), HWork(H)], [FWork(F)], [EWork(E), DWork(D), TWork(T)], [BWork(B), CWork(C), SWork(S)], [AWork(A)]]
11:25:11.984 dag flow[total:18]:
[<AWork(A)> |BWork(B)| |CWork(C)| [<SWork(S)> |TWork(T)|] |EWork(E)| |DWork(D)| <FWork(F)> |GWork(G)| [<HWork(H)> <JWork(J)> |LWork(L)|] |KWork(K)|] [<MWork(M)> <NWork(N)> <PWork(P)> <QWork(Q)>] <RWork(R)> 
11:25:11.987 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d AWork(A):COMPLETED
11:25:11.988 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d BWork(B):COMPLETED
11:25:11.989 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d CWork(C):COMPLETED
11:25:11.989 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d SWork(S):COMPLETED
11:25:11.989 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d TWork(T):COMPLETED
11:25:11.990 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d EWork(E):COMPLETED
11:25:11.990 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d DWork(D):COMPLETED
11:25:11.990 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d FWork(F):COMPLETED
11:25:11.991 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d GWork(G):COMPLETED
11:25:11.991 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d HWork(H):COMPLETED
11:25:11.991 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d JWork(J):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d KWork(K):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d LWork(L):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d MWork(M):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d NWork(N):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d PWork(P):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d QWork(Q):COMPLETED
11:25:11.992 a8c0a8e3-1b48-4880-b06f-e1ba4216de3d RWork(R):COMPLETED
```

## 2 FLOWS & CONFIG

### CASE 1 Conditional Flow

```mermaid
flowchart TD
A1{{A1}}  & A2(A2)  --> R{Status}
R --> |COMPLETED| B{{B}}
R --> |FAILED| C{{C}}
```

><https://mermaid.js.org/config/theming.html#theme-variables>

```java
ConditionalFlow conditionalFlow = ConditionalFlow.Builder.aNewConditionalFlow()
                .named("conditional_flow")
                .execute(buildParallelFlow(a1,a2))
                .when(WorkReportPredicate.COMPLETED)
                .then(b)
                .otherwise(c)
                .build();
```

```sh
20:18:21.595 A1 will work 100ms...
20:18:21.595 A2 will work 1000ms...
20:18:21.698 A1 COMPLETED
20:18:22.599 A2 COMPLETED
20:18:22.600 B will work 200ms...
20:18:22.805 B COMPLETED
20:18:22.805 latest flow status:COMPLETED
```

```sh
20:18:22.806 A1 will work 100ms...
20:18:22.807 A2 will work 1000ms...
20:18:22.911 A1 COMPLETED
20:18:23.812 A2 FAILED
20:18:23.813 C will work 200ms...
20:18:24.018 C COMPLETED
20:18:24.018 latest flow status:COMPLETED
```

### CASE 2 Loop Times Flow

```mermaid
---
title: WorkerX will repeat always 3 times, whether status is COMPLETED or FAILED.
---
flowchart LR
A{{A}} --> R{times<=3}
R --> |Yes| A
R --> |No| End
```

```java
@Test
public void testRepeatUntil() {
    // given
    log.info("WorkerX will repeat until the status is FAILED.");
    Work work = Mockito.mock(TwoStatusWork.class,
            Mockito.withSettings()
                    .useConstructor("WorkerX", 1000)
                    .defaultAnswer(Mockito.CALLS_REAL_METHODS)
    );
    RepeatFlow repeatFlow = buildRepeatFlow(work, WorkReportPredicate.COMPLETED);
    // when
    WorkReport report = repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.atLeastOnce()).execute(workContext);
}

public void testRepeatTimes() {
    // given
    int n = 3;
    log.info("WorkerX will repeat always {} times, whether status is COMPLETED or FAILED.", n);
    Work work = Mockito.mock(TwoStatusWork.class,
            Mockito.withSettings()
                    .useConstructor("WorkerX", 1000)
                    .defaultAnswer(Mockito.CALLS_REAL_METHODS)
    );
    RepeatFlow repeatFlow = buildRepeatFlow(work, n);
    // when
    repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.times(n)).execute(workContext);
}
```

```sh
11:05:46.306 WorkerX will repeat until the status is FAILED.
11:05:46.523 WorkerX will work 1000ms...
11:05:47.528 WorkerX COMPLETED
11:05:47.530 WorkerX will work 1000ms...
11:05:48.534 WorkerX COMPLETED
11:05:48.535 WorkerX will work 1000ms...
11:05:49.539 WorkerX COMPLETED
11:05:49.540 WorkerX will work 1000ms...
11:05:50.545 WorkerX FAILED
```

### CASE 3 Loop Until Failed Flow

```mermaid
---
title: WorkerX will repeat until the status is FAILED.
---
flowchart LR
A{{A}} --> R{Status}
R --> |COMPLETED| A
R --> |FAILED| End
```

```java
public void testRepeatUntil() {
    // given
    log.info("WorkerX will repeat until the status is FAILED.");
    Work work = Mockito.mock(TwoStatusWork.class,
            Mockito.withSettings()
                    .useConstructor("WorkerX", 1000)
                    .defaultAnswer(Mockito.CALLS_REAL_METHODS)
    );
    RepeatFlow repeatFlow = buildRepeatFlow(work, WorkReportPredicate.COMPLETED);
    // when
   repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.atLeastOnce()).execute(workContext);
}

public WorkReport execute(WorkContext workContext) {
    log.info("{} will work {}ms...", this.name, this.costTime);
    try {
        TimeUnit.MILLISECONDS.sleep(costTime);
    } catch (InterruptedException e) {
        log.error("{}", e.getMessage());
    }
    WorkStatus status;
    if (random.nextBoolean()) {
        status = WorkStatus.COMPLETED;
    } else {
        status = WorkStatus.FAILED;
    }
    log.info("{} {}", this.name, status);
    return new DefaultWorkReport(status, workContext);
}
```

```sh
10:48:25.485 WorkerX will repeat until the status is FAILED.
10:48:25.685 WorkerX will work 1000ms...
10:48:26.691 WorkerX[1] COMPLETED
10:48:26.695 WorkerX will work 1000ms...
10:48:27.699 WorkerX[2] COMPLETED
10:48:27.700 WorkerX will work 1000ms...
10:48:28.703 WorkerX[3] COMPLETED
10:48:28.704 WorkerX will work 1000ms...
10:48:29.709 WorkerX[4] COMPLETED
10:48:29.710 WorkerX will work 1000ms...
10:48:30.713 WorkerX[5] FAILED
```

### CASE 4 Loop Until Failed or Times reached Flow

```mermaid
---
title: WorkerX repeat at most 3 times if status is COMPLETED.
---
flowchart LR
A{{A}} --> R1{Status}
A --> R2{times<=3}

R1--> |COMPLETED| A
R2 --> |Yes| A

R1 --> |FAILED| End
R2 --> |No| End

```

```java
public void testRepeatTimesUntil() {
    // given
    int n = 3;
    log.info("WorkerX will repeat at most {} times if status is COMPLETED.", n);

    Work work = Mockito.mock(ThreeStatusWork.class,
            Mockito.withSettings()
                    .useConstructor("WorkerX", 1000, n)
                    .defaultAnswer(Mockito.CALLS_REAL_METHODS)
    );
    RepeatFlow repeatFlow = aNewRepeatFlow()
            .repeat(work)
            .until(WorkReportPredicate.COMPLETED)
            .build();
    // when
    repeatFlow.execute(workContext);
    // then
    Mockito.verify(work, Mockito.atMost(n)).execute(workContext);
    Mockito.verify(work,Mockito.atLeastOnce()).execute(workContext);
}

public WorkReport execute(WorkContext workContext) {
    log.info("{} will work {}ms...", this.name, this.costTime);
    try {
        TimeUnit.MILLISECONDS.sleep(costTime);
    } catch (InterruptedException e) {
        log.error("{}", e.getMessage());
    }
    WorkStatus status;
    Object ott = workContext.get("times");
    int tt;
    if (ott == null) {
        tt = 1;
    } else {
        tt = (int) ott + 1;
    }
    workContext.put("times", tt);
    if (tt >= times) {
        status = WorkStatus.FAILED;
    } else {
        if (random.nextBoolean()) {
            status = WorkStatus.COMPLETED;
        } else {
            status = WorkStatus.FAILED;
        }
    }
    log.info("{}[{}] {}", this.name, tt, status);
    return new DefaultWorkReport(status, workContext);
}
```

```sh
11:15:13.151 WorkerX will repeat at most 3 times if status is COMPLETED.
11:15:13.354 WorkerX will work 1000ms...
11:15:14.361 WorkerX[1] COMPLETED
11:15:14.364 WorkerX will work 1000ms...
11:15:15.369 WorkerX[2] COMPLETED
11:15:15.370 WorkerX will work 1000ms...
11:15:16.374 WorkerX[3] FAILED
```

```sh
11:15:42.190 WorkerX will repeat at most 3 times if status is COMPLETED.
11:15:42.379 WorkerX will work 1000ms...
11:15:43.385 WorkerX[1] COMPLETED
11:15:43.388 WorkerX will work 1000ms...
11:15:44.393 WorkerX[2] FAILED
```

## Dependence

[j-easy easy-flows](https://github.com/j-easy/easy-flows)

<img src="doc/easy-flows.png" alt="easy-flows" style="width:500px" />
