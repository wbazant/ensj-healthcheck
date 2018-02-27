package org.ensembl.healthcheck.testcase.eg_core;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.DatabaseType;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;
import org.ensembl.healthcheck.util.DBUtils;

public class ProviderDetails extends SingleDatabaseTestCase {

	public ProviderDetails() {
		setDescription("Check meta table has provider details");
	}

	public boolean run(DatabaseRegistryEntry dbre) {

		Connection con = dbre.getConnection();

		logger.fine("Checking there's provider details in the meta table");                 
		if ( 0 == DBUtils.getRowCount(con, "select count(*) from meta where meta_key like '%provider%'")){
			ReportManager.problem(this, con, "meta table missing provider details: provider.url/provider.name");
			return false;
		} else {
			return true;
		}
	}
}
