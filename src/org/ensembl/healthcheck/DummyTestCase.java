/*
  Copyright (C) 2003 EBI, GRL
 
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.
 
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.ensembl.healthcheck;

import java.sql.*;

import org.ensembl.healthcheck.util.*;

/**
 * <p>Title: DummyTestCase.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation: EMBL</p>
 * <p>Created on March 11, 2003, 2:44 PM</p>
 * @author Glenn Proctor <glenn@ebi.ac.uk>
 * @version
 */

public class DummyTestCase extends EnsTestCase {
   
  public DummyTestCase() {
    super();
    databaseRegexp = "homo_sapiens_.*";
    addToGroup("group1");
    addToGroup("group2");
  }
 
  TestResult run() {
    
    System.out.println("In DummyTestCase; databaseRegexp=" + databaseRegexp);
    super.printAffectedDatabases(databaseRegexp);
    
    System.out.println(getShortTestName() + " is a member of test groups: " + getCommaSeparatedGroups());
    
    return new TestResult(getShortTestName(), true, "blank message");
    
  } // run
  
} // DummyTestCase
