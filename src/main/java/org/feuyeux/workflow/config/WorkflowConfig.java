package org.feuyeux.workflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "workflow")
@Data
@Configuration
public class WorkflowConfig {
    /**
     * 参与运算的组件配置
     */
    private List<ComponentConfig> components;
}