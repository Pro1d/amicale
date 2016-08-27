package fr.insa.clubinfo.amicale.models;

import fr.insa.clubinfo.amicale.helpers.Date;

public class LaundryMachine {
	public enum Type {WASHING, DRYER}

    public enum State {FREE, BUSY, DISUSED, FINISHED, UNKNOWN}

    private Type type;
	private State state = State.UNKNOWN;
	private String description;
    private Date start;
    private Date end;
	private int number;
	private int minutesRemaining;
    private final long timestampUpdate;

    public LaundryMachine() {
        timestampUpdate = System.currentTimeMillis();
    }

    public long getTimestampEnd() {
        return timestampUpdate + minutesRemaining * 60 * 1000;
    }

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
    }

    public void setStart(int hour, int minute) {
        start = Date.today(hour, minute);
    }

    public Date getStart() {
        return start;
    }

    /**
     * This function must be called after setStart
     */
    public void setEnd(int hour, int minute) {
        end = Date.todayAfter(hour, minute, start);
    }

    public Date getEnd() {
        return end;
    }
}
