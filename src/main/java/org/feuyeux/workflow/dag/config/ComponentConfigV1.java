package org.feuyeux.workflow.dag.config;

import java.util.List;
import lombok.Data;

@Data
public class ComponentConfigV1 {
  private String name;
  private String union;
  private boolean end;
  private List<String> dependency;
}
