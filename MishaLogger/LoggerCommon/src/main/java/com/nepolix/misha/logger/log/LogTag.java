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

/**
 * @author Behrooz Shahriari
 * @since 10/31/17
 */
public
enum LogTag
{
	 VERBOSE ( "V" ),
	 INFO ( "I" ),
	 DEBUG ( "D" ),
	 WARNING ( "W" ),
	 ERROR ( "E" ),
	 FATAL ( "F" );
	 
	 private String tag;
	 
	 LogTag ( String tag )
	 {
			
			this.tag = tag;
	 }
	 
	 public
	 String getTag ( )
	 {
			
			return tag;
	 }
	 
	 public
	 void setTag ( String tag )
	 {
			
			this.tag = tag;
	 }
}
