package it.linkalab.balentesql.states.where;

import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.model.WhereCondition;
import it.linkalab.balentesql.states.AbstractState;

/**
 * State for creating a new WHERE subclause in the format "field operator
 * value" by inserting the field name.
 * 
 * @author Donato Rimenti
 *
 */
public class WhereFieldState extends AbstractState {

	public WhereFieldState(QueryInfo queryInfo) {
		super(queryInfo);
	}

	@Override
	public AbstractState transitionToNextState(String token) throws CaggiaFaException {
		return new WhereOperatorState(queryInfo, new WhereCondition(token));
	}

}