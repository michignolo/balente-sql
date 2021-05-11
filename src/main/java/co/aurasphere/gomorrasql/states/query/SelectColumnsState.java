package it.linkalab.balentesql.states.query;

import it.linkalab.balentesql.Keywords;
import it.linkalab.balentesql.model.CaggiaFaException;
import it.linkalab.balentesql.model.QueryInfo;
import it.linkalab.balentesql.states.AbstractState;
import it.linkalab.balentesql.states.AnyTokenConsumerState;
import it.linkalab.balentesql.states.CommaSeparedValuesState;
import it.linkalab.balentesql.states.GreedyMatchKeywordState;

/**
 * State that allows a select to switch between the * operator and the column
 * names to rietrieve.
 * 
 * @author Donato Rimenti
 *
 */
public class SelectColumnsState extends AbstractState {

	public SelectColumnsState(QueryInfo queryInfo) {
		super(queryInfo);
	}

	@Override
	public AbstractState transitionToNextState(String token) throws CaggiaFaException {
		if (token.equalsIgnoreCase(Keywords.ASTERISK_KEYWORDS[0])) {
			// Token is "*" (all columns). We proceed to from keyword
			queryInfo.addColumnName("*");
			return new GreedyMatchKeywordState(queryInfo, Keywords.ASTERISK_KEYWORDS,
					q -> new GreedyMatchKeywordState(q, Keywords.FROM_KEYWORDS,
							q2 -> new AnyTokenConsumerState(q2, q2::setTableName, OptionalWhereState::new), 0));
		} else {
			// Token is a column name, we continue until there are none
			queryInfo.addColumnName(token);
			return new CommaSeparedValuesState(queryInfo, queryInfo.getColumnNames(), Keywords.FROM_KEYWORDS[0],
					"%COLUMN_NAME%", q -> new GreedyMatchKeywordState(queryInfo, Keywords.FROM_KEYWORDS,
							q2 -> new AnyTokenConsumerState(q2, q2::setTableName, OptionalWhereState::new)));
		}
	}
}