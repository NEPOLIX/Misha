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

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.json.JSONObject;

import java.util.Calendar;

/**
 * @author Behrooz Shahriari
 * @since 11/2/17
 */
public
class LogData
{
	 
	 protected LogTag tag;
	 
	 protected LogTime logTime;
	 
	 protected JSONObject logJSON;
	 
	 protected String logText;
	 
	 protected CodeInfo codeInfo;
	 
	 public
	 LogData ( )
	 {
			
			Calendar calendar = Utils.getUTCTime ( );
			logTime = new LogTime ( );
			logTime.setLogYear ( calendar.get ( Calendar.YEAR ) );
			logTime.setLogMonth ( calendar.get ( Calendar.MONTH ) );
			logTime.setLogDay ( calendar.get ( Calendar.DAY_OF_MONTH ) );
			logTime.setLogHour ( calendar.get ( Calendar.HOUR_OF_DAY ) );
			logTime.setLogMinute ( calendar.get ( Calendar.MINUTE ) );
			logTime.setLogSecond ( calendar.get ( Calendar.SECOND ) );
			logTime.setLogTime ( calendar.getTimeInMillis ( ) );
	 }
	 
	 public
	 JSONObject getLogJSON ( )
	 {
			
			return logJSON;
	 }
	 
	 public
	 void setLogJSON ( JSONObject logJSON )
	 {
			
			this.logJSON = logJSON;
	 }
	 
	 public
	 LogTag getTag ( )
	 {
			
			return tag;
	 }
	 
	 public
	 void setTag ( LogTag tag )
	 {
			
			this.tag = tag;
	 }
	 
	 public
	 LogTime getLogTime ( )
	 {
			
			return logTime;
	 }
	 
	 public
	 void setLogTime ( LogTime logTime )
	 {
			
			this.logTime = logTime;
	 }
	 
	 public
	 String getLogText ( )
	 {
			
			return logText;
	 }
	 
	 public
	 void setLogText ( String logText )
	 {
			
			this.logText = logText;
	 }
	 
	 public
	 CodeInfo getCodeInfo ( )
	 {
			
			return codeInfo;
	 }
	 
	 public
	 void setCodeInfo ( CodeInfo codeInfo )
	 {
			
			this.codeInfo = codeInfo;
	 }
	 
	 @Override
	 public
	 String toString ( )
	 {
			
			StringBuilder v = new StringBuilder ( );
			v.append ( "-- [" + tag.getTag ( ) + "]\t" + codeInfo.toString ( ) + "\t" + logTime.toString ( ) + "\n" );
			if ( logText != null ) v.append ( logText ).append ( "\n" );
			if ( logJSON != null ) v.append ( logJSON.toString ( ) ).append ( "\n" );
			v.append ( "\n" );
			return v.toString ( );
	 }
	 
	 void $getCodeInfo ( StackTraceElement[] stackTrace )
	 {
			
			CodeInfo codeInfo = new CodeInfo ( );
			codeInfo.$setInfo ( stackTrace );
			setCodeInfo ( codeInfo );
	 }
}
