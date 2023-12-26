# Hello Easy Flows

> <https://github.com/j-easy/easy-flows>

**template yaml -> dag -> flow**

<img src="doc/easy-flows.png" alt="easy-flows" style="width:500px" />

## DAG

| YAML                                                                       | DAG                                                                       |
|:---------------------------------------------------------------------------|:--------------------------------------------------------------------------|
| [dag config](src/test/resources/application.yml)<br/>[dag config old](src/test/resources/application.yml.231225) | <img src="doc/dag.drawio.png" alt="dag.drawio.png" style="width:500px" /> |

```sh
$ mvn test -D test=org.feuyeux.workflow.TestDag#testDagAndFlow

18:15:23.594 queue[levels:9,total:13]:[[NWork(NWork)], [MWork(MWork)], [KWork(KWork), LWork(LWork)], [JWork(JWork)], [HWork(HWork), GWork(GWork)], [FWork(FWork)], [EWork(EWork), DWork(DWork)], [BWork(BWork), CWork(CWork)], [AWork(AWork)]]
18:15:23.601  AWork(AWork):COMPLETED
18:15:23.603  CWork(CWork):COMPLETED
18:15:23.603  BWork(BWork):COMPLETED
18:15:23.604  EWork(EWork):COMPLETED
18:15:23.604  DWork(DWork):COMPLETED
18:15:23.605  FWork(FWork):COMPLETED
18:15:23.605  HWork(HWork):COMPLETED
18:15:23.605  GWork(GWork):COMPLETED
18:15:23.606  JWork(JWork):COMPLETED
18:15:23.606  KWork(KWork):COMPLETED
18:15:23.606  LWork(LWork):COMPLETED
18:15:23.607  MWork(MWork):COMPLETED
18:15:23.607  NWork(NWork):COMPLETED
18:15:23.607 Latest status:COMPLETED
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.943 s -- in org.feuyeux.workflow.TestDag
```

## UT

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

```sh
$ mvn test -D test=org.feuyeux.workflow.TestAll#testOne

22:45:05.780 queue[levels:12,total:18]:[[RWork(R)], [QWork(Q)], [PWork(P)], [NWork(N)], [MWork(M)], [LWork(L), KWork(K)], [JWork(J)], [GWork(G), HWork(H)], [FWork(F)], [EWork(E), DWork(D), TWork(T)], [BWork(B), CWork(C), SWork(S)], [AWork(A)]]
22:45:05.789 91474f3c-93e3-4ab6-9897-b31fd387ff7b AWork(A):COMPLETED
22:45:05.791 91474f3c-93e3-4ab6-9897-b31fd387ff7b BWork(B):COMPLETED
22:45:05.791 91474f3c-93e3-4ab6-9897-b31fd387ff7b CWork(C):COMPLETED
22:45:05.791 91474f3c-93e3-4ab6-9897-b31fd387ff7b SWork(S):COMPLETED
22:45:05.791 91474f3c-93e3-4ab6-9897-b31fd387ff7b TWork(T):COMPLETED
22:45:05.792 91474f3c-93e3-4ab6-9897-b31fd387ff7b DWork(D):COMPLETED
22:45:05.792 91474f3c-93e3-4ab6-9897-b31fd387ff7b EWork(E):COMPLETED
22:45:05.792 91474f3c-93e3-4ab6-9897-b31fd387ff7b FWork(F):COMPLETED
22:45:05.793 91474f3c-93e3-4ab6-9897-b31fd387ff7b GWork(G):COMPLETED
22:45:05.793 91474f3c-93e3-4ab6-9897-b31fd387ff7b HWork(H):COMPLETED
22:45:05.793 91474f3c-93e3-4ab6-9897-b31fd387ff7b JWork(J):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b LWork(L):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b KWork(K):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b MWork(M):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b NWork(N):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b PWork(P):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b QWork(Q):COMPLETED
22:45:05.794 91474f3c-93e3-4ab6-9897-b31fd387ff7b RWork(R):COMPLETED
```
