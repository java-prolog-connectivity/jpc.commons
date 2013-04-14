package org.jpc.commons.prologbrowser.model;



public interface PrologEngineFactorySelectionListener {

	/**
	 * A callback method invoked when a Prolog engine factory is selected
	 */
	public void onPrologEngineFactorySelected();
	
	/**
	 * A callback method invoked when a Prolog engine factory is unselected
	 */
	public void onPrologEngineFactoryUnselected();
	
	/**
	 * A callback method invoked when a selected Prolog engine factory is disabled (it cannot create more Prolog sessions)
	 */
	public void onSelectedPrologEngineFactoryDisabled();

}

