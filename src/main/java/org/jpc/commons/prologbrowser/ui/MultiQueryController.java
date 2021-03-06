package org.jpc.commons.prologbrowser.ui;

import org.jpc.commons.prologbrowser.model.MultiQueryModel;
import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineInvalidatedListener;
import org.jpc.commons.prologbrowser.model.PrologEngineModel;

public class MultiQueryController implements PrologEngineInvalidatedListener {

	private PrologEngineChoiceModel engineChoiceModel;
	private MultiQueryPane multiQueryPane;
	
	public MultiQueryController(PrologEngineChoiceModel engineChoiceModel, MultiQueryPane multiQueryPane) {
		this.engineChoiceModel = engineChoiceModel;
		this.multiQueryPane = multiQueryPane;
		engineChoiceModel.addEngineSelectionListener(this);
	}

	@Override
	public void onPrologEngineInvalidated() {
		PrologEngineModel prologEngineModel = engineChoiceModel.getPrologEngine();
		if(prologEngineModel != null) {
			MultiQueryModel multiQueryModel = prologEngineModel.getMultiQueryModel();
			if(multiQueryModel.getQueries().isEmpty())
				multiQueryModel.createSingleQueryModel();
			multiQueryPane.setMultiQueryModel(multiQueryModel);
		} else {
			multiQueryPane.reset();
		}
	}
	
}
