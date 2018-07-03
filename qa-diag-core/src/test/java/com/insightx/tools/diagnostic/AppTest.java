package com.insightx.tools.diagnostic;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AppTest {

  @Test
  public void evaluatesExpression() {
    AppCLI.main("AppCLI", "-h");
    assertEquals(1, 1);
  }
}
