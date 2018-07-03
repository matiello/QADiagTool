package com.insightx.tools.diagnostic;

import junit.framework.TestCase;

import com.insightx.tools.diagnostic.parameters.OptionalParameter;
import com.insightx.tools.diagnostic.AppCLI;

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
    
    String id = "id";
    String description = "description";
    String shellKey = "shellKey";
    int type = 1;
    String defaultValue = "defaultValue";
    String value = "value";
    
    OptionalParameter op = new OptionalParameter(id, description, shellKey, type, defaultValue);
    op.setValue (value);
    
    assertEquals(id, op.getId());
    assertEquals(description, op.getDescription());
    assertEquals(shellKey, op.getShellKey());
    assertEquals(type, op.getType());
    assertEquals(value, op.getValue());    
    assertEquals(defaultValue, op.getDefaultValue());
    
    AppCLI app = new AppCLI();
    assertEquals(1,1);
  }
}
