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

package org.ensembl.healthcheck.testcase;

import java.sql.*;

import org.ensembl.healthcheck.*;

import org.ensembl.healthcheck.util.*;

/**
 * Healthcheck for the meta_coord table.
 */

public class MetaCoordTestCase extends EnsTestCase {
  
  /**
   * Constructor.
   */
  public MetaCoordTestCase() {
    addToGroup("post_genebuild");
    setDescription("Check meta_coord table");
  }
  
  /**
   * Check the coord systems in each feature table.
   * @return Result.
   */
  public TestResult run() {
    
    boolean result = true;
    DatabaseConnectionIterator it = getDatabaseConnectionIterator();
    
    String[] featureTables = {"exon", "repeat_feature", "simple_feature", "dna_align_feature",
    "protein_align_feature", "marker_feature", "prediction_transcript", "prediction_exon",
    "gene", "qtl_feature", "transcript", "karyotype" };
    
    while (it.hasNext()) {
      
      Connection con = (Connection)it.next();
      
      for (int i = 0; i < featureTables.length; i++) {
        
        String featureTable = featureTables[i];
        logger.fine("Checking " +  featureTable);
        
        String sql = "SELECT DISTINCT cs.name FROM " + featureTable + "  f, seq_region sr, coord_system cs " +
        "WHERE f.seq_region_id=sr.seq_region_id AND sr.coord_system_id=cs.coord_system_id;";
        
        // warn if features in a feature table are stored in > 1 coordinate system
        String[] cs = getColumnValues(con, sql);
        
        if (cs.length == 0) {
          
          result = false;
          ReportManager.problem(this, con, featureTable + " does not appear to have any associated coordinate systems");
          
        } else if (cs.length > 1) {
          
          result = false;
          String problemCoordinateSystems = Utils.arrayToString(cs, ",");
          ReportManager.problem(this, con, featureTable + " has more than one associated coordinate system: " + problemCoordinateSystems);
          
        } else {
          
          ReportManager.correct(this, con, featureTable + " coordinates OK");
          
        }
        
      }
      
      // check that the tables listed in the meta_coord table actually exist
      // typos in the table names could lead to all sorts of confusion
      
      String[] tableNames = getColumnValues(con, "SELECT table_name FROM meta_coord");
      for (int j = 0; j < tableNames.length; j++) {
        if (checkTableExists(con, tableNames[j])) {
          ReportManager.correct(this, con, tableNames[j] + " listed in meta_coord exists");
        } else {
          ReportManager.problem(this, con, tableNames[j] + " listed in meta_coord does not exist!");
        }
      }
      
      // each species must have one co-ordinate system defined as "top_level" and one defined as
      // "sequence_level" in the attrib column of the coord_system table.
      String[] attribs = getColumnValues(con, "SELECT attrib FROM coord_system");
      int topLevelFound = 0;
      int seqLevelFound = 0;
      for (int j = 0; j < attribs.length; j++) {
        String[] bits = attribs[j].split(",");
        for (int k = 0; k < bits.length; k++) {
          if (bits[k].equalsIgnoreCase("top_level")) {
            topLevelFound++;
          } else if (bits[k].equalsIgnoreCase("sequence_level")) {
            seqLevelFound++;
          }
        }
      }
      if (topLevelFound == 0) {
        ReportManager.problem(this, con, "coord_system table does not have a top_level attribute defined");
      } else if (topLevelFound == 1) {
        ReportManager.correct(this, con, "coord_system table has a top_level attribute defined");
      } else if (topLevelFound > 1) {
        ReportManager.problem(this, con, "coord_system table has " + topLevelFound + " co-ordinate systems defined as top_level!");        
      }
      if (seqLevelFound == 0) {
        ReportManager.problem(this, con, "coord_system table does not have a sequence_level attribute defined");
      } else if (seqLevelFound == 1) {
        ReportManager.correct(this, con, "coord_system table has a seq_level attribute defined");
      } else if (seqLevelFound > 1) {
        ReportManager.problem(this, con, "coord_system table has " + seqLevelFound + " co-ordinate systems defined as sequence_level!");        
      }
      
      
      
    }
    
    return new TestResult(getShortTestName(), result);
    
  }
  
}