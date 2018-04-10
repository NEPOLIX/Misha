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

package com.nepolix.misha.web.server.rest.express;

import com.nepolix.misha.web.server.rest.calls.ApiCall;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public final
class PathCall
				implements Comparable< PathCall >
{
	 
	 private List< PathArg > args;
	 
	 
	 private CallType callType;
	 
	 private ApiCall apiCall;
	 
	 public
	 PathCall ( String path ,
							CallType callType ,
							ApiCall apiCall )
	 {
			
			this.apiCall = apiCall;
			this.callType = callType;
			args = createPathArgs ( path );
	 }
	 
	 private static
	 List< PathArg > createPathArgs ( String path )
	 {
			
			path = path.trim ( );
			List< PathArg > args     = new LinkedList<> ( );
			String          splits[] = path.split ( "/" );
			for ( int i = 0 ; i < splits.length ; ++i )
			{
				 String s = splits[ i ];
				 if ( s.isEmpty ( ) ) continue;
				 args.add ( new PathArg ( s ) );
			}
			return args;
	 }
	 
	 public
	 int numberArgs ( )
	 {
			
			int c = 0;
			for ( PathArg pathArg : args )
			{
				 if ( pathArg.isArg ( ) ) c++;
			}
			return c;
	 }
	 
	 
	 public
	 int size ( )
	 {
			
			return args.size ( );
	 }
	 
	 public
	 boolean equalPath ( String path )
	 {
			
			path = path.toLowerCase ( ).trim ( );
//			System.out.println ( "*&^  " + path + "   " + callPath ( ) );
			List< PathArg > pathArgs = createPathArgs ( path );
			if ( pathArgs.size ( ) != args.size ( ) ) return false;
			for ( int i = 0 ; i < pathArgs.size ( ) ; ++i )
			{
				 PathArg pathArg = args.get ( i );
//				 System.out.println ( "&^&   " + pathArg.arg + "  " + pathArgs.get ( i ).arg + "    |     " + ( !pathArg
// .isArg ( ) && !pathArgs.get (
//								 i ).arg.equalsIgnoreCase ( pathArg.arg ) ) );
				 if ( !pathArg.isArg ( ) && !pathArgs.get ( i ).arg.equalsIgnoreCase ( pathArg.arg ) ) return false;
			}
			return true;
	 }
	 
	 public
	 HashMap< String, String > getPathParams ( String path )
	 {
			
			HashMap< String, String > map      = new HashMap<> ( );
			List< PathArg >           pathArgs = createPathArgs ( path );
			for ( int i = 0 ; i < pathArgs.size ( ) ; ++i )
			{
				 PathArg pathArg = args.get ( i );
				 PathArg arg     = pathArgs.get ( i );
				 if ( pathArg.isArg ( ) ) map.put ( pathArg.arg.substring ( 1 ).toLowerCase ( ) , arg.arg );
			}
			return map;
	 }
	 
	 @Override
	 public
	 int compareTo ( PathCall pathCall )
	 {
			
			int nargs    = numberArgs ( );
			int nargs_   = pathCall.numberArgs ( );
			int nnormal  = size ( ) - nargs;
			int nnormal_ = pathCall.size ( ) - nargs_;
			if ( nnormal > nnormal_ ) return -1;
			if ( nnormal < nnormal_ ) return 1;
			if ( nargs > nargs_ ) return 1;
			if ( nargs < nargs_ ) return -1;
			int c = getPath ( ).compareTo ( pathCall.getPath ( ) );
			if ( c == 0 ) return pathCall.getCallType ( ).compareTo ( callType );
			return c;
	 }
	 
	 public
	 ApiCall getApiCall ( )
	 {
			
			return apiCall;
	 }
	 
	 public
	 CallType getCallType ( )
	 {
			
			return callType;
	 }
	 
	 public
	 String getPath ( )
	 {
			
			StringBuilder builder = new StringBuilder ( );
			for ( PathArg pathArg : args )
				 builder.append ( "/" ).append ( pathArg.arg );
			return builder.toString ( );
	 }
	 
	 
	 private static
	 class PathArg
	 {
			
			private String arg;
			
			private boolean isArg;
			
			
			public
			PathArg ( String arg )
			{
				 
				 this.arg = arg;
				 isArg = arg.charAt ( 0 ) == ':' && arg.length ( ) > 1;
			}
			
			public
			boolean isArg ( )
			{
				 
				 return isArg;
			}
			
	 }
}
