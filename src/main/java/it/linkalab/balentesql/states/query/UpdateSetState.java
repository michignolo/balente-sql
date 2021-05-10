package it.linkalab.balentesql.states.query;

import java.util.Arrays;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.AligaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.states.AbstractState;
import it.linkalab.balentesql.states.AnyTokenConsumerState;
import it.linkalab.balentesql.states.SingleTokenMatchState;
import it.linkalab.balentesql.states.where.WhereFieldState;

/**
 * State for an update when the first value is set. Allows to continue setting
 * values or moving on to an optional where clause.
 * 
 * @author Damiano Casula
 *
 */
public class UpdateSetState extends AbstractState {

	public UpdateSetState(QueryInfo queryInfo) {
		super(queryInfo);
	}

	@Override
	public AbstractState transitionToNextState(String token) throws AligaException {
		// Adds another variable
		if (token.equalsIgnoreCase(",")) {
			return new AnyTokenConsumerState(queryInfo, queryInfo::addColumnName,
					q -> new SingleTokenMatchState(q, Keywords.SET_EQUAL_KEYWORD,
							q2 -> new AnyTokenConsumerState(q2, q2::addValue, UpdateSetState::new)));
		}
		// Moves to a where clause
		if (token.equalsIgnoreCase(Keywords.WHERE_KEYWORD)) {
			return new WhereFieldState(queryInfo);
		}
		throw new AligaException(Arrays.asList(",", Keywords.WHERE_KEYWORD, "%END_OF_QUERY%"), token);
	}

	@Override
	public boolean isFinalState() {
		return true;
	}

}