package it.linkalab.balentesql.states.where;

import java.util.Arrays;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.states.AbstractState;

/**
 * State for joining two WHERE subclause using an AND or OR operator, or
 * finishing the WHERE clause.
 * 
 * @author Donato Rimenti
 *
 */
public class WhereJoinState extends AbstractState {

	public WhereJoinState(QueryInfo queryInfo) {
		super(queryInfo);
	}

	@Override
	public AbstractState transitionToNextState(String token) throws CaggiaFaException {
		if (token.equalsIgnoreCase(Keywords.AND_KEYWORD)) {
			queryInfo.addWhereConditionsJoinOperator("AND");
			return new WhereFieldState(queryInfo);
		}
		if (token.equalsIgnoreCase(Keywords.OR_KEYWORD)) {
			queryInfo.addWhereConditionsJoinOperator("OR");
			return new WhereFieldState(queryInfo);
		}
		throw new CaggiaFaException(Arrays.asList(Keywords.AND_KEYWORD, Keywords.OR_KEYWORD), token);
	}

	@Override
	public boolean isFinalState() {
		return true;
	}

}