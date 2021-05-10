package it.linkalab.balentesql.states.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.AligaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.model.QueryInfo.QueryType;
import it.linkalab.balentesql.states.AbstractState;
import it.linkalab.balentesql.states.AnyTokenConsumerState;
import it.linkalab.balentesql.states.GreedyMatchKeywordState;
import it.linkalab.balentesql.states.where.WhereFieldState;

/**
 * State that allows for an optional WHERE clause, JOIN clause (only when the
 * query is a select) or end of the query.
 * 
 * @author Damiano Casula
 *
 */
public class OptionalWhereState extends AbstractState {

	public OptionalWhereState(QueryInfo queryInfo) {
		super(queryInfo);
	}

	@Override
	public AbstractState transitionToNextState(String token) throws AligaException {
		List<String> expectedKeywords = new ArrayList<>(Arrays.asList(Keywords.WHERE_KEYWORD));
		if (token.equalsIgnoreCase(Keywords.WHERE_KEYWORD)) {
			return new WhereFieldState(queryInfo);
		}
		if (queryInfo.getType().equals(QueryType.SELECT)) {
			expectedKeywords.add(Keywords.JOIN_KEYWORDS[0]);
			if (token.equalsIgnoreCase(Keywords.JOIN_KEYWORDS[0])) {
				return new GreedyMatchKeywordState(queryInfo, Keywords.JOIN_KEYWORDS,
						q -> new AnyTokenConsumerState(q, q::addJoinedTable, OptionalWhereState::new));
			}
		}
		throw new AligaException(expectedKeywords, token);
	}

	@Override
	public boolean isFinalState() {
		return true;
	}

}