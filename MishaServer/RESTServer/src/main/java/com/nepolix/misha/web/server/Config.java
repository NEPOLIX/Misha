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

package com.nepolix.misha.web.server;

import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Behrooz Shahriari
 * @since 11/25/16
 */
public
class Config
{
	 
	 public final static String CONCURRENCY = "concurrency";
	 
	 public final static String DEBUG = "debug";
	 
	 public final static String SCHEDULER_CONCURRENCY = "scheduler_concurrency";
	 
	 public static
	 JSONObject getConfig ( String configName )
	 {
			
			String tmp  = System.getProperty ( "user.home" );//  "/tmp"
			File   file = new File ( tmp + File.separator + ".misha" + File.separator + configName + ".config" );
			
			StringBuilder builder = new StringBuilder ( );
			try
			{
				 Scanner scanner = new Scanner ( file );
				 while ( scanner.hasNext ( ) )
				 {
						builder.append ( scanner.nextLine ( ) );
				 }
				 scanner.close ( );
			}
			catch ( FileNotFoundException e )
			{
				 e.printStackTrace ( );
			}
			try
			{
				 return new JSONObject ( builder.toString ( ) );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
				 return new JSONObject ( );
			}
	 }
	 
}
