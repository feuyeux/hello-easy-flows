package org.feuyeux.workflow.works;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service("D")
public class DWork extends ZeroWork {

    @PostConstruct
    public void init() {
        this.random = new Random();
    }

    public String getName() {
        return "DWork";
    }
}
