package it.linkalab.balentesql;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.linkalab.balentesql.model.CaggiaFaException;

public class TestBalenteSqlInterpreter {

	private Connection connection;

	@Before
	public void setup() throws SQLException {
		this.connection = Config.getDbConnection();
	}

	@After
	public void teardown() throws SQLException {
		this.connection.close();
	}

	@Test(expected = CaggiaFaException.class)
	public void testWrongDataTypeInsert() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("nzipp 'ngoppa city chist 'RHO', 7");
	}

	@Test
	public void testSqlConversion() throws SQLException {
		String balenteQuery = "nzipp 'ngoppa city chist 6, 8";
		String sqlQuery = BalenteSqlInterpreter.toSqlQuery(balenteQuery);
		Assert.assertEquals("INSERT INTO city VALUES ( 6, 8 )", sqlQuery);
	}

}
