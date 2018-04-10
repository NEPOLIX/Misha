import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.util.Random;

/**
 * @author Behrooz Shahriari
 * @since 10/14/16
 */
public
class TestMSON
{
	 
	 public static
	 void main ( String[] args )
					 throws
					 JSONException
	 {
			
			Random random = new Random ( );
			NodeA  nodeA  = new NodeA ( 10 );
			
			NodeA.NodeB b1 = new NodeA.NodeB ( nodeA );
			NodeA.NodeB b2 = new NodeA.NodeB ( nodeA );
			NodeA.NodeB b3 = new NodeA.NodeB ( nodeA );
			NodeA.NodeB b4 = new NodeA.NodeB ( nodeA );
//			b1.addNodeB ( b2 );
//			b1.addNodeB ( b4 );
//			b3.addNodeB ( b1 );
//			b3.addNodeB ( b3 );
//			b2.addNodeB ( b4 );
//			b2.addNodeB ( b1 );
//
//			nodeA.addNodeB ( b1 );
//			nodeA.addNodeB ( b2 );
//			nodeA.addNodeB ( b3 );
//			nodeA.addNodeB ( b4 );
			
			nodeA.addData ( "hi hjdhsj" );
			nodeA.addData ( random.nextLong ( ) );
			
			for ( int i = 0 ; i < 10 ; ++i )
			{
				 nodeA.array[ i ] = random.nextInt ( 10000 );
			}
			
			JSONObject jsonObject = MJSON.toJSON ( nodeA );
			System.out.println ( jsonObject.toString ( 2 ) );
			
			NodeA nodeA1 = MJSON.toObject ( jsonObject, NodeA.class );
			System.out.println ( nodeA1.objects.toString ( ) );
	 }
}
