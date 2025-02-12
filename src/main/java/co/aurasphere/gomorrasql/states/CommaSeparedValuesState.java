package it.linkalab.balentesql.states;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.QueryInfo;

/**
 * State that parses a list of values, separed by a comma. It stops when there's
 * no more comma and checks if the token after is the expected one (set in the
 * constructor). It can be configured to be a final state.
 * 
 * @author Donato Rimenti
 *
 */
public class CommaSeparedValuesState extends AbstractState {

	private boolean lastWasComma = false;
	private List<String> collector;
	private String nextToken;
	private Function<QueryInfo, AbstractState> transitionFunction;
	private String expectedToken;
	private boolean canBeFinalState = false;
	private boolean optionalValues = false;

	public CommaSeparedValuesState(QueryInfo queryInfo, List<String> collector, String nextToken, String expectedToken,
			Function<QueryInfo, AbstractState> transitionFunction) {
		super(queryInfo);
		this.collector = collector;
		this.nextToken = nextToken;
		this.transitionFunction = transitionFunction;
		this.expectedToken = expectedToken;
	}

	public CommaSeparedValuesState(QueryInfo queryInfo, List<String> collector, String nextToken, String expectedToken,
			boolean lastWasComma, boolean canBeFinalState, Function<QueryInfo, AbstractState> transitionFunction) {
		this(queryInfo, collector, nextToken, expectedToken, transitionFunction);

		// Used when the first token is not consumed by the previous state.
		this.lastWasComma = lastWasComma;
		this.canBeFinalState = canBeFinalState;
		this.optionalValues = true;
	}

	@Override
	public AbstractState transitionToNextState(String token) throws CaggiaFaException {
		if (token.equals(",")) {
			if (lastWasComma) {
				// Case ", ,"
				throw new CaggiaFaException(expectedToken, token);
			} else {
				// Case "%expectedToken% ,"
				lastWasComma = true;
				return this;
			}
		}

		// Case ", %expectedToken%"
		if (lastWasComma) {
			if (optionalValues && token.equalsIgnoreCase(nextToken)) {
				return transitionFunction.apply(queryInfo);
			} else {
				optionalValues = false;
			}
			collector.add(token);
			lastWasComma = false;
			return this;
		}

		// Case "%expectedToken% %nextToken%"
		if (token.equalsIgnoreCase(nextToken)) {
			return transitionFunction.apply(queryInfo);
		}

		// Case "%expectedToken% %WRONG_TOKEN%"
		throw new CaggiaFaException(Arrays.asList(",", nextToken), token);
	}

	@Override
	public boolean isFinalState() {
		return canBeFinalState;
	}
}