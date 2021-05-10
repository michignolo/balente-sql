package it.linkalab.balentesql.states;

import it.linkalab.balentesql.model.AligaException;
import it.linkalab.balentesql.model.QueryInfo;

/**
 * State that represents the end of a query.
 * 
 * @author Damiano Casula
 *
 */
public class FinalState extends AbstractState {

	public FinalState(QueryInfo queryInfo) {
		super(queryInfo);
	}

	@Override
	public AbstractState transitionToNextState(String token) throws AligaException {
		throw new AligaException("%END_OF_QUERY%", token);
	}

	@Override
	public boolean isFinalState() {
		return true;
	}

}