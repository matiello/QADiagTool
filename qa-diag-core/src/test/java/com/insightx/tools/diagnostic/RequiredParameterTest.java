package com.insightx.tools.diagnostic;

import junit.framework.TestCase;

import com.insightx.tools.diagnostic.parameters.RequiredParameter;
import com.insightx.tools.diagnostic.parameters.ParameterValidator;
import com.insightx.tools.diagnostic.parameters.ParameterAlowedListValidator;

import java.util.ArrayList;

public class RequiredParameterTest extends TestCase {

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
    int type = 1;
    String value = "value";
    
    ArrayList parameterList = new ArrayList();
    parameterList.add (value);
    ParameterValidator pv = new ParameterAlowedListValidator(parameterList);
    
    RequiredParameter op = new RequiredParameter(id, description, type, pv);
    op.setValue (value);
    
    assertEquals(id, op.getId());
    assertEquals(description, op.getDescription());
    assertEquals(type, op.getType());
    //assertEquals(value, op.getValue());    
  }
}
