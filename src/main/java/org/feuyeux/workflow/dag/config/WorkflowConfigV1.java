package org.feuyeux.workflow.dag.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "workflow")
@Data
@Configuration
public class WorkflowConfigV1 {

  /** 参与运算的组件配置 */
  private List<ComponentConfigV1> components;
}
