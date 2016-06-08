package fr.insa.clubinfo.amicale.models;

import java.util.ArrayList;

public class Launderette {
	private final ArrayList<LaundryMachine> washingMachines = new ArrayList<>();
	private final ArrayList<LaundryMachine> dryers = new ArrayList<>();

	public int getMachinesCount() {
		return washingMachines.size() + dryers.size();
	}
	public int getWashingMachinesCount() {
		return washingMachines.size();
	}
	public int getDryersCount() {
		return dryers.size();
	}
	public LaundryMachine getWashingMachine(int index) {
		return washingMachines.get(index);
	}
	public LaundryMachine getDryer(int index) {
		return dryers.get(index);
	}

	public void addWashingMachine(LaundryMachine machine) {
		washingMachines.add(machine);
	}
	public void addDryingMachine(LaundryMachine machine) {
		dryers.add(machine);
	}
}
