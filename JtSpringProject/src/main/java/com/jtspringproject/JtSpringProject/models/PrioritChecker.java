package com.jtspringproject.JtSpringProject.models;

public class PrioritChecker implements Comparable<PrioritChecker> {
	public int time;

	@Override
	public int compareTo(PrioritChecker o) {
		
		return this.time-o.time;
	}

}
