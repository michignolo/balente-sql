package it.linkalab.balentesql.states.where;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.AligaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.model.WhereCondition;
import it.linkalab.balentesql.states.AbstractState;
import it.linkalab.balentesql.states.GreedyMatchKeywordState;

/**
 * State for continuing the last WHERE subclause in the format "field operator
 * value" by inserting the operator.
 * 
 * @author Damiano Casula
 *
 */
public class WhereOperatorState extends AbstractState {

	private WhereCondition condition;

	public WhereOperatorState(QueryInfo queryInfo, WhereCondition condition) {
		super(queryInfo);
		this.condition = condition;
	}

	@Override
	public AbstractState transitionToNextState(String token) throws AligaException {
		if (Keywords.WHERE_OPERATORS.contains(token) || token.equalsIgnoreCase(Keywords.IS_KEYWORD)) {
			if (token.equalsIgnoreCase(Keywords.IS_NOT_KEYWORDS[0])) {
				condition.setOperator("IS NOT");
				return new GreedyMatchKeywordState(queryInfo, Keywords.IS_NOT_KEYWORDS, q -> new WhereValueState(q, condition));
			}
			if (token.equalsIgnoreCase(Keywords.IS_KEYWORD)) {
				condition.setOperator("IS");
			} else {
				condition.setOperator(token);
			}
			return new WhereValueState(queryInfo, condition);
		}
		throw new AligaException(Keywords.WHERE_OPERATORS, token);
	}

}