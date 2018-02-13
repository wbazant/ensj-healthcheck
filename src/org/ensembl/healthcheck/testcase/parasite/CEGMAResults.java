package org.ensembl.healthcheck.testcase.eg_core;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.DatabaseType;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;
import org.ensembl.healthcheck.util.DBUtils;

public class CEGMAResults extends SingleDatabaseTestCase {

	public CEGMAResults() {
		setDescription("Check meta table has CEGMA results");
	}

	public boolean run(DatabaseRegistryEntry dbre) {

		Connection con = dbre.getConnection();

		logger.fine("Checking there's CEGMA results in the meta table");                 
		if ( 0 == DBUtils.getRowCount(con, "select count(*) from meta where meta_key like '%cegma%'")){
			ReportManager.problem(this, con, "meta table missing CEGMA results");
			return false;
		} else {
			return true;
		}
	}
}
