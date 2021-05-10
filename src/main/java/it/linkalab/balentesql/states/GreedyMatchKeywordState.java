package it.linkalab.balentesql.states;

import java.util.function.Function;

import it.linkalab.balentesql.model.AligaException;
import it.linkalab.balentesql.model.QueryInfo;

/**
 * State that proceeds to the next one if all the given keywords are matched,
 * otherwise throws an exception.
 * 
 * @author Damiano Casula
 *
 */
public class GreedyMatchKeywordState extends AbstractState {

	private int currentIndex = 1;
	private String[] keywords;
	private Function<QueryInfo, AbstractState> nextStateTransition;

	public GreedyMatchKeywordState(QueryInfo queryInfo, String[] keywords,
			Function<QueryInfo, AbstractState> nextStateTransition) {
		super(queryInfo);
		this.keywords = keywords;
		this.nextStateTransition = nextStateTransition;
	}

	public GreedyMatchKeywordState(QueryInfo queryInfo, String[] keywords,
			Function<QueryInfo, AbstractState> nextStateTransition, int currentIndex) {
		super(queryInfo);
		this.keywords = keywords;
		this.nextStateTransition = nextStateTransition;
		this.currentIndex = currentIndex;
	}

	@Override
	public AbstractState transitionToNextState(String token) throws AligaException {
		if (token.equalsIgnoreCase(keywords[currentIndex])) {
			currentIndex++;
			if (currentIndex == keywords.length) {
				return nextStateTransition.apply(queryInfo);
			}
			return this;
		}
		throw new AligaException(keywords[currentIndex], token);
	}
}