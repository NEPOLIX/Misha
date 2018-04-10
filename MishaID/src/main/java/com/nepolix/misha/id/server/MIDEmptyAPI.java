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

package com.nepolix.misha.id.server;

import com.nepolix.misha.web.server.rest.Request;
import com.nepolix.misha.web.server.rest.Response;
import com.nepolix.misha.web.server.rest.Response.APICallResult;
import com.nepolix.misha.web.server.rest.calls.GetCall;
import com.nepolix.misha.web.server.rest.express.ApiCallExpress;

import java.io.File;
import java.io.FileWriter;

import static com.nepolix.misha.web.server.rest.Response.STATUS_CODE_OK;

/**
 * @author Behrooz Shahriari
 * @since 7/12/17
 */
public
class MIDEmptyAPI
				extends ApiCallExpress
{
	 
	 private final static ApiCallExpress $X = new MIDEmptyAPI ( );
	 
	 MIDEmptyAPI ( )
	 {
			
			super ( "/" );
	 }
	 
	 public static
	 ApiCallExpress getInstance ( )
	 {
			
			return $X;
	 }
	 
	 private
	 GetCall restart ( )
	 {
			
			return new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/restart";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request ,
												Response response )
								 throws
								 Exception
				 {
						
						APICallResult apiCallResult = new APICallResult ( );
						String        home          = "/home/ubuntu/";
						Runtime.getRuntime ( ).exec ( "rm " + home + "rc.local.log" );
						File       file       = new File ( home + "restart.sh" );
						FileWriter fileWriter = new FileWriter ( file );
						fileWriter.write ( "#!/bin/sh\nsh " + home + "mid-rc.sh >> " + home + "rc.local.log 2>&1 &\n" );
						fileWriter.close ( );
						Runtime.getRuntime ( ).exec ( "chmod +x " + home + "restart.sh" );
						Runtime.getRuntime ( ).exec ( home + "restart.sh" );
						apiCallResult.getResult ( ).putOpt ( "status" , "restarting" );
						apiCallResult.setStatusCode ( STATUS_CODE_OK );
						response.sendResponse ( apiCallResult );
				 }
			};
	 }
	 
	 GetCall healthChecker ( )
	 {
			
			return new GetCall ( )
			{
				 
				 @Override
				 public
				 String callPath ( )
				 {
						
						return "/health";
				 }
				 
				 @Override
				 public
				 void apiCall ( Request request ,
												Response response )
								 throws
								 Exception
				 {
						
						APICallResult apiCallResult = new APICallResult ( );
						apiCallResult.setStatusCode ( Response.STATUS_CODE_OK );
						apiCallResult.getResult ( ).putOpt ( "status" , "green" );
						response.sendResponse ( apiCallResult );
				 }
			};
	 }
	 
	 @Override
	 protected
	 void initContext ( )
	 {
			
			addAPIs ( restart ( ) , healthChecker ( ) );
	 }
	 
	 @Override
	 protected
	 String getAPIVersion ( )
	 {
			
			return null;
	 }
}
