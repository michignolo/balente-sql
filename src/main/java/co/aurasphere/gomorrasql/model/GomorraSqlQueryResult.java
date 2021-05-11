package it.linkalab.balentesql.model;

import java.sql.ResultSet;

/**
 * Wrapper around JDBC query results, used by BalenteSql.
 * 
 * @author Donato Rimenti
 *
 */
public class BalenteSqlQueryResult {
	
	private Integer affectedRows;
	
	private ResultSet resultSet;

	public Integer getAffectedRows() {
		return affectedRows;
	}

	public void setAffectedRows(Integer affectedRows) {
		this.affectedRows = affectedRows;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

}