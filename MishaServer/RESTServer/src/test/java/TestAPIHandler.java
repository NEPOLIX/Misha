/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to NOX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.web.server.Server;
import com.nepolix.misha.web.server.rest.Request;
import com.nepolix.misha.web.server.rest.Response;
import com.nepolix.misha.web.server.rest.calls.GetCall;
import com.nepolix.misha.web.server.rest.calls.PostCall;
import com.nepolix.misha.web.server.rest.express.ApiCallExpress;

import java.io.IOException;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public
class TestAPIHandler
				extends ApiCallExpress
{
	 
	 private
	 TestAPIHandler ( String path )
	 {
			
			super ( path );
	 }
	 
	 public static
	 void main ( String[] args )
					 throws
					 IOException
	 {
			
			JSONObject config = new JSONObject ( );
			Server.buildServerInstance ( 6589, false, config )
						.start ( );
			Server.buildServerInstance ( 6589, false, config )
						.addAPIHandler ( new TestAPIHandler ( "/register" ) );
			
	 }
	 
	 @Override
	 protected
	 void initContext ( )
	 {
			
			addAPI ( new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/:id/name";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request,
												Response response )
				 {
						
						System.out.println ( "HEADERS=" + request.headers ( )
																										 .toString ( ) );
						response.sendResponse ( Response.STATUS_CODE_OK, request.headers ( ) );
				 }
			} );
			
			addAPI ( new PostCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/:id/name";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request,
												Response response )
				 {
						
						JSONObject jsonObject = new JSONObject ( );
						try
						{
							 jsonObject.put ( "body", request.body ( )
																							 .toString ( 2 ) );
							 jsonObject.put ( "args", request.param ( "id" ) );
						}
						catch ( JSONException e )
						{
							 e.printStackTrace ( );
						}
						try
						{
							 jsonObject.put ( "call", "POST /:id/name" );
						}
						catch ( JSONException e )
						{
							 e.printStackTrace ( );
						}
						response.sendResponse ( Response.STATUS_CODE_OK, jsonObject );
				 }
			} );
			
			addAPI ( new PostCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/:id/:name";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request,
												Response response )
				 {
						
						System.out.println ( "XXXXX   " + request.queryParams ( )
																										 .toString ( ) );
						System.out.println ( "YYYYYY		" + request.params ( )
																											 .toString ( ) );
						response.sendResponse ( Response.STATUS_CODE_OK, request.body ( ) );
				 }
			} );
	 }
	 
	 @Override
	 protected
	 String getAPIVersion ( )
	 {
			
			return null;
	 }
//	 @Override
//	 public
//	 void handle ( HttpExchange httpExchange )
//					 throws
//					 IOException
//	 {
//
//			try
//			{
//				 String requestMethod = httpExchange.getRequestMethod ( );
//				 out.println ( "requestMethod: " + requestMethod );
//
//				 /////////////////////////////////////////////
//				 HttpContext           httpContext = httpExchange.getHttpContext ( );
//				 Map< String, Object > attributes  = httpContext.getAttributes ( );
//				 out.println ( "attributes: " + attributes.toString ( ) );
//
////				 Authenticator.Result setResult = httpContext.getAuthenticator ( )
////																									.authenticate ( httpExchange );
////				 out.println ( "authResult: " + setResult.toString ( ) );
////				 out.println ( "path: " + httpContext.callPath ( ) );
////				 out.println ( "filters: " + httpContext.getFilters ( ) );
//
//				 //////////////////////////////////////////////////////////
//				 InetSocketAddress inetSocketAddress = httpExchange.getLocalAddress ( );
//				 out.println ( "LOCAL" );
//				 out.println ( inetSocketAddress.getHostName ( ) + "   " + inetSocketAddress.getHostString ( ) + "   " +
// inetSocketAddress.getAddress ( )
//
// .getCanonicalHostName ( )
//
//											 + "   " + "" + inetSocketAddress.getAddress ( )
//																											 .getHostAddress ( ) + "   " + inetSocketAddress.getAddress ( )
//																																																			.getHostName (
// ) );
//
//				 ////////////////////////////////////////////////
//				 out.println ( "protocol: " + httpExchange.getProtocol ( ) );
//
//				 ////////////////////////////////////////////////
//				 out.println ( "principle: " + httpExchange.getPrincipal ( ) );
//
//				 ////////////////////////////////////////////////
//				 inetSocketAddress = httpExchange.getRemoteAddress ( );
//				 out.println ( "REMOTE" );
//				 out.println ( inetSocketAddress.getHostName ( ) + "   " + inetSocketAddress.getHostString ( ) + "   " +
// inetSocketAddress.getAddress ( )
//
// .getCanonicalHostName ( )
//											 + "   " + "" + inetSocketAddress.getAddress ( )
//																											 .getHostAddress ( ) + "   " + inetSocketAddress.getAddress ( )
//																																																			.getHostName (
// ) );
//
//				 /////////////////////////////////////////////////
//				 out.println ( "HEADERS" );
//				 Headers headers = httpExchange.getRequestHeaders ( );
//				 for ( String key : headers.keySet ( ) )
//				 {
//						out.println ( key + ":" + headers.get ( key ) );
//				 }
//
//				 //////////////////////////////////////////////////
//				 URI uri = httpExchange.getRequestURI ( );
//				 out.println ( uri.getAuthority ( ) + "  " + uri.getHost ( ) + "  " + uri.getQuery ( ) + "  " + uri
// .getRawPath ( ) + "  "
//											 + uri.getRawUserInfo ( ) );
//
//				 InputStream inputStream = httpExchange.getRequestBody ( );
//				 Scanner     scanner     = new Scanner ( inputStream );
//				 while ( scanner.hasNext ( ) )
//				 {
//						String body = scanner.nextLine ( );
//						if ( body != null && !body.isEmpty ( ) )
//						{
//							 out.println ( body );
//							 JSONObject jsonObject = new JSONObject ( body );
//							 out.println ( jsonObject.toString ( 2 ) );
//						}
//				 }
//				 scanner.close ( );
//				 out.println ( "END" );
//				 ////////////////////////
//			}
//			catch ( Exception e )
//			{
//				 e.printStackTrace ( );
//			}
//			finally
//			{
//				 Server.sendEmptyResponse ( httpExchange );
//			}
//
//	 }
}
