package it.linkalab.balentesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.linkalab.balentesql.model.BalenteSqlQueryResult;

public class TestDelete {

	private Connection connection;
	
	@Before
	public void setup() throws SQLException {
		this.connection = Config.getDbConnection();
	}
	
	@After
	public void teardown() throws SQLException {
		this.connection.close();
	}
	
	
	@Test
	public void testDeleteAll() throws SQLException {
		BalenteSqlInterpreter bsi = new BalenteSqlInterpreter(connection);
		BalenteSqlQueryResult result = gsi.execute("facimm na' strage mmiez 'a city");
		Assert.assertEquals((Integer) 4, result.getAffectedRows());
		
		result = gsi.execute("ripigliammo tutto chillo ch'era 'o nuostro mmiez 'a city");
		ResultSet resultSet = result.getResultSet();
		int counter = 0;
		while (resultSet.next()) {
			counter++;
		}
		Assert.assertEquals(0, counter);
	}
	
	@Test
	public void testDeleteWithWhere() throws SQLException {
		BalenteSqlInterpreter bsi = new BalenteSqlInterpreter(connection);
		BalenteSqlQueryResult result = gsi.execute("facimm na' strage mmiez 'a city ar� city_id >= 2");
		Assert.assertEquals((Integer) 3, result.getAffectedRows());
		
		result = gsi.execute("ripigliammo tutto chillo ch'era 'o nuostro mmiez 'a city");
		ResultSet resultSet = result.getResultSet();
		int counter = 0;
		while (resultSet.next()) {
			int id = resultSet.getInt(1);
			String name = resultSet.getString(2);
			Assert.assertEquals(1, id);
			Assert.assertEquals("MILANO", name);
			counter++;
		}
		Assert.assertEquals(1, counter);
	}

}