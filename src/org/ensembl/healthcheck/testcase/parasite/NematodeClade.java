package org.ensembl.healthcheck.testcase.eg_core;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.DatabaseType;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;
import org.ensembl.healthcheck.util.DBUtils;

public class NematodeClade extends SingleDatabaseTestCase {

	public NematodeClade() {
		setDescription("Check nematodes have their nematode clade provided");
	}

	public boolean run(DatabaseRegistryEntry dbre) {

		Connection con = dbre.getConnection();

		if ( 0 < DBUtils.getRowCount(con, "select count(*) from meta where meta_value=\"Nematoda\"")){
			if (0 == DBUtils.getRowCount(con, "select count(*) from meta where meta_key=\"species.nematode_clade\"")) {
				ReportManager.problem(this, con, "meta table missing nematode clade - update meta set meta_value=\"<I,III,IV,V> where meta_key=\"species.nematode_clade\"");
				return false;
			} else {
				return true;
			}	
		} else {
			return true;
		}
	}
}
