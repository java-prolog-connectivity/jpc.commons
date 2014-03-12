package org.jpc.commons.prologbrowser.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.engine.prolog.driver.PrologEngineDriverProxy;
import org.minitoolbox.naming.Nameable;

import com.google.common.collect.Lists;

public class PrologDriverModel<T extends PrologEngine> extends PrologEngineDriverProxy<T> implements Nameable {

	private String name;
	
	/**
	 * Answers an ordered list of drivers.
	 * The ordering criteria are the driver engine id, the driver bridge library id and the driver id (in that order).
	 * @param drivers the drivers to order
	 * @return an ordered list of drivers
	 */
	public static List<PrologDriverModel> order(Iterable<PrologDriverModel> drivers) {
		List<PrologDriverModel> orderedList = Lists.newArrayList(drivers);
		Collections.sort(orderedList, new Comparator<PrologDriverModel>(){
			@Override
			public int compare(PrologDriverModel d1, PrologDriverModel d2) {
				if(!d1.getEngineDescription().getName().equals(d2.getEngineDescription().getName()))
					return d1.getEngineDescription().getName().compareTo(d2.getEngineDescription().getName());
				else if(!d1.getLibraryName().equals(d2.getLibraryName()))
					return d1.getLibraryName().compareTo(d2.getLibraryName());
				else
					return d1.getName().compareTo(d2.getName());
			}});
		return orderedList;
	}
	
	public PrologDriverModel(PrologEngineDriver<T> driver) {
		super(driver);
	}
	
	public PrologDriverModel(PrologEngineDriver<T> driver, String name) {
		super(driver);
		setName(name);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
