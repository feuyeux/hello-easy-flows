package org.feuyeux.workflow.config;

import java.util.List;
import lombok.Data;

@Data
public class ComponentConfig {
  private String name;
  private String union;
  private boolean end;
  private List<String> dependency;
}
