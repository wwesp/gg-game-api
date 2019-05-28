package edu.missouriwestern.csmp.gg.base;

/** represents a location as row and column values on a board */
public class Location {
	private final int column;
	private final int row;

	/** 
	 * Constructs a Location with the specified column and row
	 * @param row this location's row
	 * @param column this location's column
	 */
	public Location(int column, int row) {
		this.column = column;
		this.row = row;
	}

	/** 
	 * returns this location's column
	 * @return this location's column
	 */
	public int getColumn() { return column; }

	/** 
	 * returns this location's row
	 * @return this location's row
	 */
	public int getRow() { return row; }

	@Override
	public int hashCode() {
		var hash = 3;
		hash = 97 * hash + this.column;
		hash = 97 * hash + this.row;
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Location) {
			var l = (Location)o;
			return l.column == column && l.row == row;
		}
		return false;
	}
	/**
	 * Returns adjacent location in the given {@link Direction}
	 * @param direction given direction
	 * @return adjacent location
	 */
	public Location getAdjacentLocation(Direction direction) {
		var column = ((direction == Direction.WEST) ? getColumn() - 1:
			(direction == Direction.EAST) ? getColumn() + 1 :
				getColumn());
		var row = ((direction == Direction.NORTH) ? getRow() - 1 :
			(direction == Direction.SOUTH) ? getRow() + 1 :
				getRow());
		return new Location(column, row);
	}

	/**
	 * Returns {@link Direction} to this location from another location
	 * @param loc other Location
	 * @return resulting Direction
	 */
	public Direction getDirectionToLocation(Location loc) {
		if(row < loc.getRow())
			return Direction.SOUTH;
		if(row > loc.getRow())
			return Direction.NORTH;
		if(column < loc.getColumn())
			return Direction.EAST;
		if(column > loc.getColumn())
			return Direction.WEST;
		return null;
	}

	/** 
	 * Returns a string representing this location in column, row order.
	 * <p>
	 * The values should be separated by a comma and enclosed in parenthesis.  
	 * Example: the location column 6, row 5 would be <tt>"(6,5)"</tt>
	 * @return a string representing this location in column, row order
	 */
	@Override
	public String toString() { 
		return String.format("(%d,%d)", column, row);
	}

}
