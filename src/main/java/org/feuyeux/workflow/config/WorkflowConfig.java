package org.feuyeux.workflow.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "workflow")
@Data
@Configuration
public class WorkflowConfig {

  /**
   * 参与运算的组件配置
   */
  private List<ComponentConfig> components;
}