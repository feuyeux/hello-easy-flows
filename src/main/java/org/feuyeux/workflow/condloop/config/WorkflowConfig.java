package org.feuyeux.workflow.condloop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "yaml")
@PropertySource(value = "classpath:hello-flow.yaml", factory = YamlPropertySourceFactory.class)
public class WorkflowConfig {

    /**
     * 参与运算的组件配置
     */
    private List<ComponentConfig> components;
}
