package it.linkalab.balentesql.states.where;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.model.WhereCondition;
import it.linkalab.balentesql.states.AbstractState;

/**
 * State for completing the last WHERE subclause in the format "field operator
 * value" by inserting the value.
 * 
 * @author Donato Rimenti
 *
 */
public class WhereValueState extends AbstractState {

	private WhereCondition condition;

	public WhereValueState(QueryInfo queryInfo, WhereCondition condition) {
		super(queryInfo);
		this.condition = condition;
	}

	@Override
	public AbstractState transitionToNextState(String token) throws CaggiaFaException {
		if(token.equalsIgnoreCase(Keywords.NULL_KEYWORD)) {
			condition.setValue("NULL");
		} else {
			condition.setValue(token);
		}
		queryInfo.addWhereCondition(condition);
		return new WhereJoinState(queryInfo);
	}
	
}