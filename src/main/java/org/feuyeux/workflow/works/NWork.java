package org.feuyeux.workflow.works;

import jakarta.annotation.PostConstruct;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("N")
public class NWork extends ZeroWork {

  @PostConstruct
  public void init() {
    this.random = new Random();
  }

  public String getName() {
    return "NWork";
  }
}
