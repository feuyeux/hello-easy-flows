# Hello Easy Flows

> https://github.com/j-easy/easy-flows

![easy-flows](easy-flows.png)

```sh
mvn compile exec:java -Dexec.mainClass="org.feuyeux.workflow.HelloEasyFlows" -Dexec.args="Hello EasyFlows"
```

```sh
09:55:11.844 Hello EasyFlows
09:55:11.853 EXECUTE CONDITIONAL FLOW[0]:
09:55:11.853 work1:FAILED
09:55:11.854 work3:FAILED
09:55:11.855 latest flow status:FAILED
09:55:11.855 EXECUTE CONDITIONAL FLOW[1]:
09:55:11.855 work1:FAILED
09:55:11.855 work3:COMPLETED
09:55:11.855 latest flow status:COMPLETED
09:55:11.855 EXECUTE CONDITIONAL FLOW[2]:
09:55:11.855 work1:COMPLETED
09:55:11.855 work2:COMPLETED
09:55:11.856 latest flow status:COMPLETED
09:55:11.856 ====
09:55:11.859 EXECUTE SEQUENTIAL FLOW:
09:55:11.859 work1:COMPLETED
09:55:11.859 work2:FAILED
09:55:11.859 Work unit ''print message work'' has failed, skipping subsequent work units
09:55:11.859 latest flow status:FAILED
09:55:11.859 ====
09:55:11.861 REPEAT UNTIL FLOW:
09:55:11.861 work1:FAILED
09:55:11.861 work1:COMPLETED
09:55:11.861 latest flow status:COMPLETED
09:55:11.862 ====
09:55:11.862 REPEAT TIMES FLOW:
09:55:11.862 work1:COMPLETED
09:55:11.863 work1:FAILED
09:55:11.863 work1:FAILED
09:55:11.863 latest flow status:FAILED
09:55:11.863 ====
09:55:11.865 PARALLEL FLOW:
09:55:11.868 work1:FAILED
09:55:11.868 work2:FAILED
09:55:11.868 work3:FAILED
09:55:11.869  status:FAILED
09:55:11.869  status:FAILED
09:55:11.869  status:FAILED
09:55:11.869 parallel flow status:FAILED
```