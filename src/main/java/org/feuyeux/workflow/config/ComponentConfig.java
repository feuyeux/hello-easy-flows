package org.feuyeux.workflow.config;

import lombok.Data;

import java.util.List;

@Data
public class ComponentConfig {
    private String name;
    private List<String> dependency;
}
