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

package com.nepolix.misha.logger.log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 11/2/17
 */
public
class CodeInfo
{
	 
	 private List< String > fileNames;
	 
	 private List< Integer > lines;
	 
	 public
	 CodeInfo ( )
	 {
		
	 }
	 
	 public
	 List< String > getFileNames ( )
	 {
			
			if ( fileNames == null ) fileNames = new ArrayList<> ( );
			return fileNames;
	 }
	 
	 public
	 void setFileNames ( List< String > fileNames )
	 {
			
			this.fileNames = fileNames;
	 }
	 
	 public
	 List< Integer > getLines ( )
	 {
			
			if ( lines == null ) lines = new ArrayList<> ( );
			return lines;
	 }
	 
	 public
	 void setLines ( List< Integer > lines )
	 {
			
			this.lines = lines;
	 }
	 
	 public
	 void $setInfo ( StackTraceElement[] stackTrace )
	 {
			
			int n = stackTrace.length;
			for ( int i = 1 ; i < n ; ++i )
			{
				 String x = stackTrace[ i ].getClassName ( );
				 if ( x.contains ( "nepolix" ) || x.contains ( "genio" ) || x.contains ( "misha" ) )
				 {
						getLines ( ).add ( stackTrace[ i ].getLineNumber ( ) );
						getFileNames ( ).add ( stackTrace[ i ].getFileName ( ) );
				 }
			}
	 }
	 
	 @Override
	 public
	 String toString ( )
	 {
			
			StringBuilder builder = new StringBuilder ( );
			builder.append ( "[" );
			for ( int i = 0 ; i < getLines ( ).size ( ) ; ++i )
			{
				 builder.append ( getLines ( ).get ( i ) ).append ( ":" ).append ( getFileNames ( ).get ( i ) );
				 if ( i != getLines ( ).size ( ) - 1 ) builder.append ( "  -  " );
			}
			builder.append ( "]" );
			return builder.toString ( );
	 }
}
