package edu.missouriwestern.csmp.gg.base;

/**
   <p>
   This enumerated type represents the four possible
   directions that robots can travel in one turn.

   <table spacing=10 cellspacing=10 border=0 bgcolor=black>
   <tr>
   <td>
   <font color=white>
   Note: the methods <tt>valueOf</tt> and <tt>values</tt> are
   automatically generated by the compiler, so you should not
   define them in your implementation.
   </font>
   </td>
   </tr>
   </table>
 */

public enum Direction {
	NORTH {
		@Override
		public String toString () { return "N"; }
	},

	EAST {
		@Override
		public String toString () { return "E"; }
	},

	SOUTH {
		@Override
		public String toString () { return "S"; }
	},

	WEST {
		@Override
		public String toString () { return "W"; }
	};

	/** returns a single character representation of this direction
      Example: for NORTH, this returns <tt>"N"</tt>
      @return a single character representation of this direction
	 */
	@Override
	public String toString() {
		return "";
	}

	/**
	 * Returns opposite direction
	 * @return opposite direction
	 */
    public Direction getOpposingDirection() {
      switch(this) {
        case NORTH: return SOUTH;
        case WEST: return EAST;
        case SOUTH: return NORTH;
        case EAST: return WEST;
      }
      return null;
    }

    /**
     * Return a Direction from a one character String identifying it 
     * @param d string representing the Direction
     * @return direction interpretation
     */
    public static Direction valueOfSingle(String d) {
      d = d.toLowerCase();
      if(d.equals("n")) return NORTH;
      if(d.equals("w")) return WEST;
      if(d.equals("e")) return EAST;
      if(d.equals("s")) return SOUTH;
      return null;
    }
}
