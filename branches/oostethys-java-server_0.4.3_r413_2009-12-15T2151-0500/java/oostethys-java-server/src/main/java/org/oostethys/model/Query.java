package org.oostethys.model;

public class Query {

	public Query(long minTime, long maxTime, double minZ, double maxZ,
			double minLonBBOX, double minLatBBOX, double maxLonBBOX,
			double maxLatBBOX) {
		super();
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.minLonBBOX = minLonBBOX;
		this.minLatBBOX = minLatBBOX;
		this.maxLonBBOX = maxLonBBOX;
		this.maxLatBBOX = maxLatBBOX;
	}

	private long minTime; // millisec since 1970

	private long maxTime; // millsec since 1970

	private double minZ; // Z + up

	private double maxZ; // Z + up

	private double minLonBBOX = Long.MAX_VALUE;
	private double minLatBBOX = Long.MAX_VALUE;
	private double maxLonBBOX = Long.MIN_VALUE;
	private double maxLatBBOX = Long.MIN_VALUE;

	public long getMinTime() {
		return minTime;
	}

	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

	public double getMinZ() {
		return minZ;
	}

	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}

	public double getMinLonBBOX() {
		return minLonBBOX;
	}

	public void setMinLonBBOX(double minLonBBOX) {
		this.minLonBBOX = minLonBBOX;
	}

	public double getMinLatBBOX() {
		return minLatBBOX;
	}

	public void setMinLatBBOX(double minLatBBOX) {
		this.minLatBBOX = minLatBBOX;
	}

	public double getMaxLonBBOX() {
		return maxLonBBOX;
	}

	public void setMaxLonBBOX(double maxLonBBOX) {
		this.maxLonBBOX = maxLonBBOX;
	}

	public double getMaxLatBBOX() {
		return maxLatBBOX;
	}

	public void setMaxLatBBOX(double maxLatBBOX) {
		this.maxLatBBOX = maxLatBBOX;
	}

}
