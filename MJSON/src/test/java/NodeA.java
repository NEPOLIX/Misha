import java.util.ArrayList;

/**
 * @author Behrooz Shahriari
 * @since 10/14/16
 */
public
class NodeA
{
	 
	 int array[];
	 
	 //	 @JsonManagedReference
	 ArrayList< Object > objects;
	 
	 //	 @JsonManagedReference
	 ArrayList< NodeB > nodeBs;
	 
	 public
	 NodeA ( )
	 {
			
	 }
	 
	 public
	 NodeA ( int n )
	 {
			
			array = new int[ n ];
			objects = new ArrayList<> ( );
			nodeBs = new ArrayList<> ( );
	 }
	 
	 public
	 void addData ( Object o )
	 {
			
			objects.add ( o );
	 }
	 
	 public
	 void addNodeB ( NodeB nodeB )
	 {
			
			nodeBs.add ( nodeB );
	 }
	 
	 
	 static
	 class NodeB
	 {
			
			//			@JsonManagedReference
			private ArrayList< NodeB > nodeBs;
			
			//			@JsonManagedReference
			private NodeA nodeA;
			
			public
			NodeB ( NodeA nodeA )
			{
				 
				 this.nodeA = nodeA;
			}
			
			public
			void addNodeB ( NodeB nodeB )
			{
				 
			}
	 }
	 
}
