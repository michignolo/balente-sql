package it.linkalab.balentesql.states;

import java.util.function.Function;

import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.QueryInfo;

/**
 * States that proceeds if a specific token is matched exactly.
 * 
 * @author Donato Rimenti
 *
 */
public class SingleTokenMatchState extends AbstractState {

	private String expectedToken;
	private Function<QueryInfo, AbstractState> transitionFunction;

	public SingleTokenMatchState(QueryInfo queryInfo, String expectedToken,
			Function<QueryInfo, AbstractState> transitionFunction) {
		super(queryInfo);
		this.expectedToken = expectedToken;
		this.transitionFunction = transitionFunction;
	}

	@Override
	public AbstractState transitionToNextState(String token) throws CaggiaFaException {
		if (token.equalsIgnoreCase(expectedToken)) {
			return transitionFunction.apply(queryInfo);
		}
		throw new CaggiaFaException(expectedToken, token);
	}

}