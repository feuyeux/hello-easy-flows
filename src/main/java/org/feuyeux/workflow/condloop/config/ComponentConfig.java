package org.feuyeux.workflow.condloop.config;

import java.util.List;
import lombok.Data;

@Data
public class ComponentConfig {
  private String name;
  private String union;
  private boolean end;
  private int times;
  private List<String> dependency;
}
