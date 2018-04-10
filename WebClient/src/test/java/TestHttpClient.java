import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.rest.client.WebClient;
import com.nepolix.misha.task.handler.core.task.callback.Callback;

/**
 * @author Behrooz Shahriari
 * @since 10/10/16
 */
public
class TestHttpClient
{
	 
	 public static
	 void main ( String[] args )
	 {
			
			WebClient webClient = WebClient.getInstance ( );


//			HashMap< String, String > headers = new HashMap<> ( );
//			headers.put ( "com.nepolix.misha.id", "987rg98et893ut9088" );
//			headers.put ( "time", Utils.getCurrentFormattedUTCTime ( ) );
//			JSONObject body = new JSONObject ( );
//			try
//			{
//				 body.put ( "com.nepolix.misha.id", "98df7ugd uj98eutu34ut34u0u03" );
//			}
//			catch ( JSONException e )
//			{
//				 e.printStackTrace ( );
//			}
			webClient.call ( WebClient.RESTMethod.GET, "http://www.linkedin.com/", null, null, null, new Callback< JSONObject > ( )
			{
				 
				 @Override
				 public
				 void onResult ( JSONObject result )
				 {
						
						try
						{
							 System.out.println ( result.toString ( 2 ) );
						}
						catch ( JSONException e )
						{
							 e.printStackTrace ( );
						}
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
						System.out.println ( "ERROR:" + e.toString ( ) );
				 }
			} );

//			webClient.terminate ( );
	 }
}
