package org.jpc.commons.prologbrowser.model;

import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.jpc.query.QuerySolution;
import org.jpc.term.Term;
import org.minitoolbox.fx.FXUtil;

public class QueryResultModel {

	private Term goal;
	private ObservableList<TableColumn<Map<String, Term>,?>> columns;
	private ObservableList<QuerySolution> queryResult;
	
	public QueryResultModel() {
		columns = FXCollections.observableArrayList();
		queryResult = FXCollections.observableArrayList();
	}
	
	public List<String> getNonAnonymousVariablesNames() {
		if(goal == null)
			return null;
		else
			return goal.getNonAnonymousVariablesNames();
	}
	
	public Term getGoal() {
		return goal;
	}
	
	public void setGoal(Term goal) {
		this.goal = goal;
		reset();
		configureColumns();
	}
	
	public void reset() {
		columns.clear();
		queryResult.clear();
	}
	
	public ObservableList<TableColumn<Map<String, Term>,?>> getColumns() {
		return columns;
	}
	
	public ObservableList<QuerySolution> getQueryResult() {
		return queryResult;
	}
	
	
	private void configureColumns() {
		getColumns().clear();
		List<String> nonAnonymousVariablesNames = getNonAnonymousVariablesNames();
		if(nonAnonymousVariablesNames != null && !nonAnonymousVariablesNames.isEmpty()) {
			for(String nonAnonymousVarName : nonAnonymousVariablesNames) {
				TableColumn<Map<String, Term>, Term> dataColumn = new TableColumn<>(nonAnonymousVarName);
				dataColumn.setCellValueFactory(new MapValueFactory(nonAnonymousVarName));
				dataColumn.setMinWidth(130);
				Callback<TableColumn<Map<String, Term>, Term>, TableCell<Map<String, Term>, Term>>
	            cellFactoryForMap = new Callback<TableColumn<Map<String, Term>, Term>, TableCell<Map<String, Term>, Term>>() {
	                    @Override
	                    public TableCell call(TableColumn p) {
	                        return new TextFieldTableCell(new StringConverter() {
	                            @Override
	                            public String toString(Object t) {
	                                return t.toString();
	                            }
	                            @Override
	                            public Object fromString(String string) {
	                                return null;
	                            }                                    
	                        });
	                    }
				};
				dataColumn.setCellFactory(cellFactoryForMap);
				getColumns().add(dataColumn);
			}
		}
	}

}
