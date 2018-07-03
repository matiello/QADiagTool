package com.insightx.tools.diagnostic;

import junit.framework.TestCase;

public class AppTest extends TestCase {
  private java.util.List emptyList;

  /**
   * Sets up the test fixture. 
   * (Called before every test case method.)
   */
  public void setUp() {
  }

  /**
   * Tears down the test fixture. 
   * (Called after every test case method.)
   */
  public void tearDown() {
  }
  
  public void testSomeBehavior() {
    AppCLI.main("AppCLI", "-h");
    assertEquals(1, 1);
  }
}
