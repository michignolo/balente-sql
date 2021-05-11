package it.linkalab.balentesql.states;

import java.util.Arrays;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.AligaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.model.QueryInfo.QueryType;
import it.linkalab.balentesql.states.query.OptionalWhereState;
import it.linkalab.balentesql.states.query.SelectColumnsState;
import it.linkalab.balentesql.states.query.UpdateSetState;

/**
 * First state when parsing a query. It switches through the various possible
 * queries according to the first token.
 * 
 * @author Damiano Casula
 *
 */
public class InitialState extends AbstractState {

	public InitialState() {
		super(new QueryInfo());
	}

	@Override
	public AbstractState transitionToNextState(String token) throws AligaException {
		if (token.equalsIgnoreCase(Keywords.SELECT_KEYWORD)) {
			queryInfo.setType(QueryType.SELECT);
			return new SelectColumnsState(queryInfo);
		}
		if (token.equalsIgnoreCase(Keywords.UPDATE_KEYWORD)) {
			queryInfo.setType(QueryType.UPDATE);
			return new AnyTokenConsumerState(queryInfo, queryInfo::setTableName,
					q -> new SingleTokenMatchState(q, Keywords.SET_KEYWORD,
							q2 -> new AnyTokenConsumerState(q2, q2::addColumnName,
									q3 -> new SingleTokenMatchState(q3, Keywords.SET_EQUAL_KEYWORD,
											q4 -> new AnyTokenConsumerState(q4, q4::addValue, UpdateSetState::new)))));
		}
		if (token.equalsIgnoreCase(Keywords.DELETE_KEYWORDS[0])) {
			queryInfo.setType(QueryType.DELETE);
			return new GreedyMatchKeywordState(queryInfo, Keywords.DELETE_KEYWORDS,
					q -> new GreedyMatchKeywordState(q, Keywords.FROM_KEYWORDS,
							q2 -> new AnyTokenConsumerState(q2, q2::setTableName, OptionalWhereState::new), 0));
		}
		if (token.equalsIgnoreCase(Keywords.INSERT_KEYWORDS[0])) {
			queryInfo.setType(QueryType.INSERT);
			return new GreedyMatchKeywordState(queryInfo, Keywords.INSERT_KEYWORDS,
					q -> new AnyTokenConsumerState(q, q::setTableName,
							q2 -> new CommaSeparedValuesState(q2, q2.getColumnNames(), Keywords.VALUES_KEYWORD,
									"%COLUMN_NAME%", true, false, q3 -> new CommaSeparedValuesState(q3, q3.getValues(),
											null, "%VALUE%", true, true, FinalState::new))));
		}
		if (token.equalsIgnoreCase(Keywords.COMMIT_KEYWORDS[0])) {
			queryInfo.setType(QueryType.COMMIT);
			return new GreedyMatchKeywordState(queryInfo, Keywords.COMMIT_KEYWORDS, FinalState::new);
		}
		if (token.equalsIgnoreCase(Keywords.ROLLBACK_KEYWORD)) {
			queryInfo.setType(QueryType.ROLLBACK);
			return new FinalState(queryInfo);
		}
		if (token.equalsIgnoreCase(Keywords.BEGIN_TRANSACTION_KEYWORDS[0])) {
			queryInfo.setType(QueryType.BEGIN_TRANSACTION);
			return new GreedyMatchKeywordState(queryInfo, Keywords.BEGIN_TRANSACTION_KEYWORDS, FinalState::new);
		}
		throw new AligaException(Arrays.asList(Keywords.SELECT_KEYWORD, Keywords.UPDATE_KEYWORD,
				Keywords.INSERT_KEYWORDS[0], Keywords.DELETE_KEYWORDS[0], Keywords.BEGIN_TRANSACTION_KEYWORDS[0],
				Keywords.COMMIT_KEYWORDS[0], Keywords.ROLLBACK_KEYWORD), token);
	}

}