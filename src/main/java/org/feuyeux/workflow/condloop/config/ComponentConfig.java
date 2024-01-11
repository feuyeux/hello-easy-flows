package org.feuyeux.workflow.condloop.config;

import lombok.Data;

import java.util.List;

@Data
public class ComponentConfig {
  private String name;
  private String union;
  private boolean end;
  private int times;
  private List<String> dependency;
}
