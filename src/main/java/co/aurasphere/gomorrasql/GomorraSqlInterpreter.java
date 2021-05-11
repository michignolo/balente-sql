package it.linkalab.balentesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.BalenteSqlQueryResult;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.model.WhereCondition;
import it.linkalab.balentesql.states.AbstractState;
import it.linkalab.balentesql.states.InitialState;

/**
 * Interpreter for the BalenteSql.
 * 
 * @author Donato Rimenti
 *
 */
public class BalenteSqlInterpreter {

	private Connection connection;

	public BalenteSqlInterpreter(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Converts a BalenteSql query into a standard SQL query.
	 * 
	 * @param balenteQuery the query to convert
	 * @return an equivalent SQL query
	 */
	public static String toSqlQuery(String balenteQuery) {
		QueryInfo info = parseQuery(balenteQuery);
		return buildSqlQuery(info);
	}

	private static QueryInfo parseQuery(String query) {
		AbstractState currentState = new InitialState();
		// TODO: bug: whitespaces inside quotes should be ignored
		StringTokenizer tokenizer = new StringTokenizer(query, ", ", true);
		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken().trim();
			if (!nextToken.isEmpty()) {
				currentState = currentState.transitionToNextState(nextToken);
			}
		}

		if (!currentState.isFinalState()) {
			throw new CaggiaFaException("Unexpected end of query");
		}

		return currentState.getQueryInfo();
	}

	private static String buildSqlQuery(QueryInfo queryInfo) {
		switch (queryInfo.getType()) {
		case SELECT:
			return buildSelectQuery(queryInfo);
		case INSERT:
			return buildInsertQuery(queryInfo);
		case UPDATE:
			return buildUpdateQuery(queryInfo);
		case DELETE:
			return buildDeleteQuery(queryInfo);
		case BEGIN_TRANSACTION:
			return "START_TRANSACTION";
		case COMMIT:
			return "COMMIT";
		case ROLLBACK:
			return "ROLLBACK";
		}
		throw new CaggiaFaException("Unrecognised query");
	}

	private static String buildDeleteQuery(QueryInfo queryInfo) {
		return "DELETE FROM " + queryInfo.getTableName() + buildWhereClause(queryInfo);
	}

	private static String buildWhereClause(QueryInfo queryInfo) {
		List<WhereCondition> whereConditions = queryInfo.getWhereConditions();
		List<String> whereConditionsJoinOperators = queryInfo.getWhereConditionsJoinOperators();
		if (whereConditions.isEmpty()) {
			return "";
		}
		StringBuilder whereClause = new StringBuilder(" WHERE ");
		for (int i = 0; i < whereConditions.size(); i++) {
			WhereCondition whereCondition = whereConditions.get(i);
			whereClause.append(whereCondition.getField()).append(" ").append(whereCondition.getOperator()).append(" ")
					.append(whereCondition.getValue());
			if (whereConditionsJoinOperators.size() >= i + 1) {
				whereClause.append(" ").append(whereConditionsJoinOperators.get(i)).append(" ");
			}
		}
		return whereClause.toString();
	}

	private static String buildUpdateQuery(QueryInfo queryInfo) {
		StringBuilder query = new StringBuilder("UPDATE ").append(queryInfo.getTableName()).append(" SET ");
		for (int i = 0; i < queryInfo.getColumnNames().size(); i++) {
			query.append(queryInfo.getColumnNames().get(i)).append(" = ").append(queryInfo.getValues().get(i))
					.append(", ");
		}
		query.delete(query.length() - 2, query.length());
		return query.toString() + buildWhereClause(queryInfo);
	}

	private static String buildInsertQuery(QueryInfo queryInfo) {
		StringBuilder query = new StringBuilder("INSERT INTO ").append(queryInfo.getTableName());
		if (!queryInfo.getColumnNames().isEmpty()) {
			query.append(" ( ").append(queryInfo.getColumnNames().stream().collect(Collectors.joining(", ")))
					.append(" )");
		}
		query.append(" VALUES ( ").append(queryInfo.getValues().stream().collect(Collectors.joining(", ")))
				.append(" )");
		return query.toString();
	}

	private static String buildSelectQuery(QueryInfo queryInfo) {
		String query = "SELECT ";

		// Column names
		query += queryInfo.getColumnNames().stream().collect(Collectors.joining(", "));

		// Table name
		query += " FROM " + queryInfo.getTableName();

		// Joins
		List<String> joinedTables = queryInfo.getJoinedTables();
		if (!joinedTables.isEmpty()) {
			query += " INNER JOIN " + joinedTables.stream().collect(Collectors.joining(" INNER JOIN "));
		}
		// Where
		query += buildWhereClause(queryInfo);

		return query;
	}

	/**
	 * Executes the given BalenteSql query in the connected database.
	 * 
	 * @param balenteSqlQuery the query to execute
	 * @return the result of the query
	 */
	public BalenteSqlQueryResult execute(String balenteSqlQuery) {
		QueryInfo queryInfo = parseQuery(balenteSqlQuery);
		String sqlQuery = buildSqlQuery(queryInfo);
		BalenteSqlQueryResult result = new BalenteSqlQueryResult();

		// Executes the query
		try {
			Statement statement = connection.createStatement();
			switch (queryInfo.getType()) {
			case SELECT:
				ResultSet resultSet = statement.executeQuery(sqlQuery);
				result.setResultSet(resultSet);
				break;
			case BEGIN_TRANSACTION:
				connection.setAutoCommit(false);
				break;
			case COMMIT:
				connection.commit();
				connection.setAutoCommit(true);
				break;
			case DELETE:
			case UPDATE:
			case INSERT:
				int updatedRows = statement.executeUpdate(sqlQuery);
				result.setAffectedRows(updatedRows);
				break;
			case ROLLBACK:
				connection.rollback();
				connection.setAutoCommit(true);
				break;
			}
		} catch (SQLException e) {
			throw new CaggiaFaException(e);
		}
		return result;
	}

}