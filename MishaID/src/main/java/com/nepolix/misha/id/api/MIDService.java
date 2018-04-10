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

package com.nepolix.misha.id.api;

import com.nepolix.misha.id.core.MID;
import com.nepolix.misha.id.core.MIDFactory;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.web.server.rest.Request;
import com.nepolix.misha.web.server.rest.Response;
import com.nepolix.misha.web.server.rest.Response.APICallResult;
import com.nepolix.misha.web.server.rest.calls.GetCall;
import com.nepolix.misha.web.server.rest.express.ApiCallExpress;

/**
 * @author Behrooz Shahriari
 * @since 6/6/17
 */
public
class MIDService
				extends ApiCallExpress
{
	 
	 private final static ApiCallExpress $X = new MIDService ( );
	 
	 private
	 MIDService ( )
	 {
			
			super ( "/id_generator" );
	 }
	 
	 public static
	 ApiCallExpress getInstance ( )
	 {
			
			return $X;
	 }
	 
	 @Override
	 protected
	 void initContext ( )
	 {
			
			addAPIs ( getIDs ( ) );
	 }
	 
	 private
	 GetCall getIDs ( )
	 {
			
			return new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return null;
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request ,
												Response response )
								 throws
								 Exception
				 {
						
						APICallResult apiCallResult = new APICallResult ( );
						if ( request.validHeaders ( apiCallResult , "k" , "id_tag" ) )
						{
							 int        k          = Integer.parseInt ( request.header ( "k" ) );
							 String     idTag      = request.header ( "id_tag" );
							 MIDFactory midFactory = MIDFactory.getInstance ( );
							 MID        mid        = midFactory.getMID ( idTag );
							 String[]   ids        = mid.generateNextKIDs ( k );
							 JSONArray  array      = new JSONArray ( );
							 for ( String id : ids )
							 {
									array.put ( id );
							 }
							 apiCallResult.setStatusCode ( Response.STATUS_CODE_OK );
							 apiCallResult.getResult ( ).putOpt ( "ids" , array );
						}
						response.sendResponse ( apiCallResult );
				 }
			};
	 }
	 
	 @Override
	 protected
	 String getAPIVersion ( )
	 {
			
			return null;
	 }
}
