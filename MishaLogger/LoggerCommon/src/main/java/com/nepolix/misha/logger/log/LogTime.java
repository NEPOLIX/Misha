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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Behrooz Shahriari
 * @since 10/30/17
 */
public
class LogTime
{
	 
	 private long logTime;
	 
	 private int logYear;
	 
	 private int logMonth;
	 
	 private int logDay;
	 
	 private int logHour;
	 
	 private int logMinute;
	 
	 private int logSecond;
	 
	 public
	 LogTime ( )
	 {
		
	 }
	 
	 public
	 long getLogTime ( )
	 {
			
			return logTime;
	 }
	 
	 public
	 void setLogTime ( long logTime )
	 {
			
			this.logTime = logTime;
	 }
	 
	 public
	 int getLogYear ( )
	 {
			
			return logYear;
	 }
	 
	 public
	 void setLogYear ( int logYear )
	 {
			
			this.logYear = logYear;
	 }
	 
	 public
	 int getLogMonth ( )
	 {
			
			return logMonth;
	 }
	 
	 public
	 void setLogMonth ( int logMonth )
	 {
			
			this.logMonth = logMonth;
	 }
	 
	 public
	 int getLogDay ( )
	 {
			
			return logDay;
	 }
	 
	 public
	 void setLogDay ( int logDay )
	 {
			
			this.logDay = logDay;
	 }
	 
	 public
	 int getLogHour ( )
	 {
			
			return logHour;
	 }
	 
	 public
	 void setLogHour ( int logHour )
	 {
			
			this.logHour = logHour;
	 }
	 
	 public
	 int getLogMinute ( )
	 {
			
			return logMinute;
	 }
	 
	 public
	 void setLogMinute ( int logMinute )
	 {
			
			this.logMinute = logMinute;
	 }
	 
	 public
	 int getLogSecond ( )
	 {
			
			return logSecond;
	 }
	 
	 public
	 void setLogSecond ( int logSecond )
	 {
			
			this.logSecond = logSecond;
	 }
	 
	 
	 @Override
	 public
	 String toString ( )
	 {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy MM dd 'T' HH:mm:ss.SSS" );
			Calendar         calendar   = Utils.getUTCTime ( );
			calendar.setTimeInMillis ( getLogTime ( ) );
			return "[" + dateFormat.format ( calendar.getTime ( ) ) + "]";
	 }
}
