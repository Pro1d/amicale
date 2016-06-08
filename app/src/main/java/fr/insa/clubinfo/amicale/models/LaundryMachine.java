package fr.insa.clubinfo.amicale.models;

public class LaundryMachine {
	public enum Type {WASHING, DRYER}

    public enum State {FREE, BUSY}

    private Type type;
	private State state;
	private String description;
	private int number;
	private int minutesRemaining;


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMinutesRemaining() {
        return minutesRemaining;
    }

    public void setMinutesRemaining(int minutesRemaining) {
        this.minutesRemaining = minutesRemaining;
        if(minutesRemaining < 0)
            state = State.FREE;
        else
            state = State.BUSY;
    }
}
