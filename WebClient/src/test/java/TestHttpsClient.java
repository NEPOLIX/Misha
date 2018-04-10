/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.rest.client.WebClient;
import com.nepolix.misha.task.handler.core.task.callback.Callback;

/**
 * @author Behrooz Shahriari
 * @since 12/2/16
 */
public
class TestHttpsClient
{
	 
	 public static
	 void main ( String[] args )
	 {
			
			WebClient webClient = WebClient.getInstance ( );
			webClient.call ( WebClient.RESTMethod.GET, "http://localhost:5000/testSMS", null, null, null, new Callback< JSONObject > ( )
			{
				 
				 @Override
				 public
				 void onResult ( JSONObject result )
				 {
						
						System.out.println ( JSONObject.toString ( result ) );
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
						System.err.println ( JSONObject.toString ( e ) );
				 }
			} );
	 }
}
