package it.linkalab.balentesql;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.linkalab.balentesql.model.CaggiaFaException;

public class TestWrongSyntax {
	
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
	public void testWrongInitialKeyword() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("uella");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testSelectDoubleComma() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo a , ,");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testSelectWrongFrom() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo a chist");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testQueryEndExpected() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("sfaccimm wrong");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testWrongLongOperator() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo tutto proprio tutto");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testIncompleteQuery() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testWhereInvalidOperator() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo tutto chillo ch'era 'o nuostro mmiez 'a city ar� city_id !! 4");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testWrongSelectWhereJoinOperator() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo tutto chillo ch'era 'o nuostro mmiez 'a city ar� city_id <= 4 ma 5 > 1");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testWrongSelectWhereClause() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("ripigliammo tutto chillo ch'era 'o nuostro mmiez 'a city quando city_id <= 4 e 5 > 1");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testWrongUpdateSetClause() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("rifacimm city cambia city_id = 1");
	}
	
	@Test(expected = CaggiaFaException.class)
	public void testWrongUpdateWhereClause() throws SQLException {
		BalenteSqlInterpreter gsi = new BalenteSqlInterpreter(connection);
		gsi.execute("rifacimm city accunza city_id accuss� 1 e basta");
	}
}