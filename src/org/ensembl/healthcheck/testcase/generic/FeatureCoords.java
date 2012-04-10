/*
 Copyright (C) 2004 EBI, GRL
 
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

package org.ensembl.healthcheck.testcase.generic;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.Team;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;
import org.ensembl.healthcheck.util.DBUtils;

/**
 * Check that feature co-ords make sense.
 */
public class FeatureCoords extends SingleDatabaseTestCase {

	/**
	 * Creates a new instance of CheckFeatureCoordsTestCase
	 */
	public FeatureCoords() {

		addToGroup("post_genebuild");
		addToGroup("release");
		addToGroup("pre-compara-handover");
		addToGroup("post-compara-handover");

		setDescription("Check that feature co-ords make sense.");
		setHintLongRunning(true);
		setTeamResponsible(Team.GENEBUILD);
	}

	/**
	 * Iterate over each affected database and perform various checks.
	 * 
	 * @param dbre
	 *            The database to check.
	 * @return True if the test passed.
	 */
	public boolean run(DatabaseRegistryEntry dbre) {

		boolean result = true;

		String[] featureTables = getCoreFeatureTables();

		for (int tableIndex = 0; tableIndex < featureTables.length; tableIndex++) {

			String tableName = featureTables[tableIndex];

			Connection con = dbre.getConnection();

			// three separate queries to avoid using an OR

			// ------------------------

			logger.fine("Checking " + tableName + " for start < 1");
			String sql = "SELECT COUNT(*) FROM " + tableName
					+ " WHERE seq_region_start < 1";
			int rows = DBUtils.getRowCount(con, sql);
			if (rows > 0) {
				ReportManager.problem(this, con, rows + " rows in " + tableName
						+ " have seq_region_start < 1");
				result = false;
			} else {
				ReportManager.correct(this, con, "All rows in " + tableName
						+ " have seq_region_start >= 1");
			}

			if (!checkStartEnd(tableName, con)) {
				result = false;
			}

			// ------------------------

			/**
			 * This section removed as it takes an inordinately long time for
			 * not much useful result - can't find any combination of indices
			 * that avoid full table scan // skip large tables as this test
			 * takes an inordinately long time if
			 * (tableName.equals("protein_align_feature") ||
			 * tableName.equals("dna_align_feature") ||
			 * tableName.equals("repeat_feature")) { continue; }
			 * logger.info("Checking " + tableName + " for end > length"); sql =
			 * "SELECT COUNT(*) FROM " + tableName + " f, seq_region s WHERE
			 * f.seq_region_id = s.seq_region_id AND f.seq_region_end >
			 * s.length"; rows = DBUtils.getRowCount(con, sql); if (rows > 0) {
			 * ReportManager.problem(this, con, rows + " rows in " + tableName +
			 * " have seq_region_end > length in seq_region_table"); result =
			 * false; } else { ReportManager.correct(this, con, "All rows in " +
			 * tableName + " have sensible lengths"); }
			 */

		} // foreach table

		return result;

	} // run

	/**
	 * Subroutine to carry out a check on whether the start is after the end.
	 * This is to allow EG to skip this check for circular molecules
	 * 
	 * @param tableName
	 * @param con
	 * @return
	 */
	protected boolean checkStartEnd(String tableName, Connection con) {
		String sql;
		int rows;
		boolean result = true;
		// ------------------------
		logger.fine("Checking " + tableName + " for start > end");
		sql = "SELECT COUNT(*) FROM " + tableName
				+ " WHERE seq_region_start > seq_region_end";
		rows = DBUtils.getRowCount(con, sql);
		if (rows > 0) {
			ReportManager.problem(this, con, rows + " rows in " + tableName
					+ " have seq_region_start > seq_region_end");
			result = false;
		} else {
			ReportManager.correct(this, con, "All rows in " + tableName
					+ " have seq_region_start < seq_region_end");
		}
		return result;
	}

} // FeatureCoords
