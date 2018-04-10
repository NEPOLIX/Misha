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

import com.nepolix.misha.id.MIDConstants;
import com.nepolix.misha.id.api.MIDService;
import com.nepolix.misha.id.client.MishaID;
import com.nepolix.misha.id.core.MIDFactory;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.web.server.Config;
import com.nepolix.misha.web.server.Server;

/**
 * @author Behrooz Shahriari
 * @since 6/6/17
 */
public
class MIDServer
{
	 
	 
	 static
	 {
			MishaID.initInetAddress ( "localhost" );
	 }
	 
	 private static int PORT = -1;
	 
	 public static
	 int getIDPort ( )
	 {
			
			if ( PORT == -1 )
			{
				 JSONObject config = Config.getConfig ( "id" );
				 try
				 {
						PORT = config.getInt ( "port" );
				 }
				 catch ( JSONException e )
				 {
						PORT = MIDConstants.getMishaIDPort ( );
				 }
			}
			return PORT;
	 }
	 
	 public static
	 void main ( String[] args )
	 {
			
			JSONObject config = Config.getConfig ( "id" );
			Server.buildServerInstance ( getIDPort ( ) , false , config ).addAPIHandler ( MIDService.getInstance ( ) ).addAPIHandler ( MIDEmptyAPI.getInstance ( ) );
			Server.getServerInstance ( getIDPort ( ) ).start ( );
			MIDFactory.getInstance ( );
	 }
}
