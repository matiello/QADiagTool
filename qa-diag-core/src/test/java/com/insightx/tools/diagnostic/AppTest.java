package com.insightx.tools.diagnostic;

import junit.framework.TestCase;

import com.insightx.tools.diagnostic.parameters.OptionalParameter;

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
    
    OptionalParameter op = new OptionalParameter("id", "description", "shellKey", 1, "default value");
    
    assertEquals(1, 1);
  }
}
