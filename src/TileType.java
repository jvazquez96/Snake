
/**
 * The {@code TileType} class represents the different
 * types of tiles that can be displayed on the screen.
 * @author Brendan Jones
 *
 */
public enum TileType {

	Fruit(0),

	SnakeHead(0),
	
	SnakeBody(0);
        private int iValue;
        public int getValue(){
            return iValue;
        }
        public void setValue(int iValue){
            this.iValue = iValue;
        }
        private TileType(int iValue){
            this.iValue = iValue;
        }
	
}

