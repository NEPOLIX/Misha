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

package com.nepolix.misha.commons.utils;

import sun.security.action.GetPropertyAction;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.*;

/**
 * @author Behrooz Shahriari
 * @since 11/26/16
 */
public
class Utils
{
	 
	 private final static SecureRandom RANDOM = new SecureRandom ( );
	 
	 private final static String NUMBERS = "0123456789";
	 
	 private final static Set< Integer > NUMBER_SET = new HashSet<> ( );
	 
	 private static final Charset UTF_8 = Charset.forName ( "UTF-8" );
	 
	 private static final String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
	 
	 private final static Pattern pattern = Pattern.compile ( HEX_PATTERN );
	 
	 private static final File tmpdir = new File ( AccessController.doPrivileged ( new GetPropertyAction ( "java.io.tmpdir" ) ) );
	 
	 static
	 {
			for ( int i = 0 ; i < NUMBERS.length ( ) ; ++i )
			{
				 NUMBER_SET.add ( i );
			}
	 }
	 
	 public static
	 String normalizePhone ( String phone )
	 {
			
			if ( phone == null ) return null;
			String p = phone;
			p = p.trim ( );
			p = p.replaceAll ( "[^0-9]" , "" );
			p = "+" + p;
			return p;
	 }
	 
	 public static
	 String randomPassCode ( int len )
	 {
			
			StringBuilder   r    = new StringBuilder ( );
			List< Integer > list = new ArrayList<> ( NUMBER_SET );
			while ( r.length ( ) < len )
			{
				 String x = "" + list.remove ( RANDOM.nextInt ( list.size ( ) ) );
				 r.append ( x );
				 if ( list.isEmpty ( ) )
				 {
						list.addAll ( NUMBER_SET );
				 }
			}
			return r.toString ( );
	 }
	 
	 public static
	 String generateRefreshToken ( String password )
	 {
			
			byte[] bytes = new byte[ 128 ];
			RANDOM.nextBytes ( bytes );
			byte bs[] = password.getBytes ( );
			for ( int i = 0 ; i < password.length ( ) ; ++i )
			{
				 for ( int j = 0 ; j < 5 ; ++j )
				 {
						int idx = RANDOM.nextInt ( bytes.length );
						int idy = RANDOM.nextInt ( bs.length );
						bytes[ idx ] ^= bs[ idy ];
				 }
			}
			return com.nepolix.misha.commons.Base64.toBase64 ( bytes );
	 }
	 
	 public static
	 String hash1024 ( String message )
	 {
			
			int    len = message.length ( );
			String p1  = message.substring ( 0 , len / 3 );
			String p2  = message.substring ( len / 3 , 2 * len / 3 );
			String p3  = message.substring ( 2 * len / 3 );
			
			String h1 = hash512 ( p1 , null );
			String h2 = hash512 ( p2 , null );
			String h3 = hash512 ( p3 , null );
			String h  = h1 + h2 + h3;
			return hash512 ( h.substring ( 0 , h.length ( ) / 2 ) , null ) + hash512 ( h.substring ( h.length ( ) / 2 ) , null );
	 }
	 
	 public static
	 String hash512 ( String message ,
										String salt )
	 {
			
			String hashCode = null;
			try
			{
				 if ( salt == null ) salt = randomBase64String ( 40 );
				 MessageDigest md = MessageDigest.getInstance ( "SHA-512" );
				 md.update ( salt.getBytes ( "UTF-8" ) );
				 byte[]        bytes = md.digest ( message.getBytes ( "UTF-8" ) );
				 StringBuilder sb    = new StringBuilder ( );
				 for ( int i = 0 ; i < bytes.length ; i++ )
				 {
						sb.append ( Integer.toString ( ( bytes[ i ] & 0xff ) + 0x100 , 16 ).substring ( 1 ) );
				 }
				 hashCode = sb.toString ( );
			}
			catch ( NoSuchAlgorithmException | UnsupportedEncodingException e )
			{
				 e.printStackTrace ( );
			}
			return hashCode;
	 }
	 
	 ///////////////////////////////////////////////////////////////////
	 ///////////////////////////////////////////////////////////////////
	 ///////////////////////////////////////////////////////////////////
	 
	 private final static String STRING = "0123456789qwertyuiopasdfghjklzxcvbnmPOIUYTREWQASDFGHJKLMNBVCXZ=_";
	 
	 
	 private final static SimpleDateFormat FORMAT = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss.SSS" );
	 
	 private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*" + "(\\.[A-Za-z]{2,})$";
	 
	 private final static Pattern patternEmail = Pattern.compile ( EMAIL_PATTERN );
	 
	 
	 public static
	 String getCurrentCodeInfo ( final Thread currentThread )
	 {
			
			final StackTraceElement[] stackTrace   = currentThread.getStackTrace ( );
			long                      memoryInfo[] = memoryInfo ( );
			StringBuilder             builder      = new StringBuilder ( );
			builder.append ( "{" );
			builder.append ( "\"line\":" + stackTrace[ 2 ].getLineNumber ( ) );
			builder.append ( "\"file\":" + stackTrace[ 2 ].getFileName ( ) );
			builder.append ( "\"class\":" + stackTrace[ 2 ].getClassName ( ) );
			builder.append ( "\"method\":" + stackTrace[ 2 ].getMethodName ( ) );
			builder.append ( "\"memory\":{" );
			builder.append ( "\"total\":" + memoryInfo[ 0 ] );
			builder.append ( "\"free\":" + memoryInfo[ 1 ] );
			builder.append ( "\"used\":" + memoryInfo[ 2 ] + "}" );
			builder.append ( "\"time\":" + getCurrentFormattedUTCTime ( ) + "}" );
			return builder.toString ( );
	 }
	 
	 public static
	 long[] memoryInfo ( )
	 {
			
			long freeSize  = 0L;
			long totalSize = 0L;
			long usedSize  = -1L;
			try
			{
				 Runtime info = getRuntime ( );
				 freeSize = info.freeMemory ( );
				 totalSize = info.totalMemory ( );
				 usedSize = totalSize - freeSize;
				 usedSize /= 1048576;
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return new long[] {
							totalSize / 1048576 ,
							freeSize / 1048576 ,
							usedSize
			};
	 }
	 
	 
	 public static
	 long alignLocalUserTimeToServerTime ( long userLocalTime ,
																				 long userUTCOffset )
	 {
			
			Calendar calendar        = Calendar.getInstance ( );
			int      serverUTCOffset = Utils.getOffsetFromUTC ( );
			int      diffUTCOffset   = ( int ) ( serverUTCOffset - userUTCOffset );
			calendar.setTimeInMillis ( userLocalTime - diffUTCOffset );
			return calendar.getTimeInMillis ( );
	 }
	 
	 public static
	 Calendar getUTCTime ( )
	 {
			
			Calendar calendar           = Calendar.getInstance ( );
			long     timeInMilliseconds = calendar.getTimeInMillis ( );
			long     offsetFromUTC      = calendar.getTimeZone ( ).getOffset ( timeInMilliseconds );
			calendar.setTimeInMillis ( calendar.getTimeInMillis ( ) - offsetFromUTC );
			return calendar;
	 }
	 
	 public static
	 Calendar getTimeZoneCalendar ( long offset )
	 {
			
			Calendar zoneTime = Utils.getUTCTime ( );
			zoneTime.setTimeInMillis ( zoneTime.getTimeInMillis ( ) + offset );
			return zoneTime;
	 }
	 
	 public static
	 Calendar getTimeZoneCalendar ( String timeZoneID )
	 {
			
			return Calendar.getInstance ( TimeZone.getTimeZone ( timeZoneID ) );
	 }
	 
	 public static
	 long getCurrentTime$TimeZone ( String timeZoneID )
	 {
			
			return getTimeZoneCalendar ( timeZoneID ).getTimeInMillis ( );
	 }
	 
	 public static
	 long daysBetween ( Calendar startDate ,
											Calendar endDate )
	 {
			
			return startDate.toInstant ( ).until ( endDate.toInstant ( ) , ChronoUnit.DAYS );
	 }
	 
	 public static
	 long getCurrentTime$UTC ( long offset )
	 {
			
			return getTimeZoneCalendar ( offset ).getTimeInMillis ( );
	 }
	 
	 public static
	 String getCurrentFormattedUTCTime ( )
	 {
			
			Calendar calendar = getUTCTime ( );
			return FORMAT.format ( calendar.getTime ( ) ) + "Z";
	 }
	 
	 public static
	 long getCurrentUTCTime ( )
	 {
			
			Calendar calendar = getUTCTime ( );
			return calendar.getTimeInMillis ( );
	 }
	 
	 public static
	 long getTimeFromFormattedUTCTime ( String formattedUTCTime )
	 {
			
			if ( formattedUTCTime == null || formattedUTCTime.length ( ) < 3 ) return -1;
			try
			{  //remove 'Z'
				 formattedUTCTime = formattedUTCTime.substring ( 0 , formattedUTCTime.length ( ) - 1 );
				 Date date = FORMAT.parse ( formattedUTCTime );
				 return date.getTime ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return -1;
			}
	 }
	 
	 public static
	 String convertUTC$Locale ( String formattedUTCTime )
	 {
			
			long utcTime       = getTimeFromFormattedUTCTime ( formattedUTCTime );
			int  offsetFromUTC = getOffsetFromUTC ( );
			long localeTime    = utcTime + offsetFromUTC;
			return getFormattedUTCTime ( localeTime );
	 }
	 
	 public static
	 String getFormattedUTCTime ( long time )
	 {
			
			return FORMAT.format ( time ) + "Z";
	 }
	 
	 public static
	 String formatCalendar1 ( long time )
	 {
			
			Calendar calendar = Calendar.getInstance ( );
			calendar.setTimeInMillis ( time );
			SimpleDateFormat dateFormat = new SimpleDateFormat ( "E, MMM dd, yyyy - hh:mm aa" );
			return dateFormat.format ( calendar.getTime ( ) );
	 }
	 
	 public static
	 int getOffsetFromUTC ( )
	 {
			
			Calendar calendar           = Calendar.getInstance ( );
			long     timeInMilliseconds = calendar.getTimeInMillis ( );
			return calendar.getTimeZone ( ).getOffset ( timeInMilliseconds );
	 }
	 
	 public static
	 void printTimeInfo ( )
	 {
			
			System.out.println ( "Time Info" );
			System.out.println ( "offsetFromUTC-Hours=" + ( getOffsetFromUTC ( ) / ( 1000 * 60 * 60 ) ) );
			System.out.println ( "getCurrentFormattedUTCTime=" + getCurrentFormattedUTCTime ( ) );
			
			Calendar calendar           = Calendar.getInstance ( );
			long     timeInMilliseconds = calendar.getTimeInMillis ( );
			System.out.println ( "timeInMilliseconds=" + timeInMilliseconds );
			int offsetFromUTC = calendar.getTimeZone ( ).getOffset ( timeInMilliseconds );
			System.out.println ( "$timeInMilliseconds=" + ( calendar.getTimeInMillis ( ) - offsetFromUTC ) );
			System.out.println ( );
	 }
	 
	 public static
	 String convertToUTF8 ( String str )
	 {
			
			byte[] byteArray = str.getBytes ( UTF_8 );
			return new String ( byteArray , UTF_8 );
	 }
	 
	 public static
	 String convertToUTF8 ( int hexString )
	 {
			
			char[] emojiCharArray = Character.toChars ( hexString );
			return new String ( emojiCharArray );
	 }
	 
	 ///////////////////////////////////////////////////////////////////
	 ///////////////////////////////////////////////////////////////////
	 ///////////////////////////////////////////////////////////////////
	 
	 public static
	 String prettyNameFormat ( String name )
	 {
			
			String t = name;
			if ( t != null && !t.isEmpty ( ) )
			{
				 t = name.substring ( 0 , 1 ).toUpperCase ( );
				 if ( name.length ( ) > 0 )
				 {
						t = t + name.substring ( 1 ).toLowerCase ( );
				 }
			}
			return t;
	 }
	 
	 public static
	 boolean validateEmail ( final String email )
	 {
			
			if ( email == null || email.isEmpty ( ) ) return false;
			final Matcher matcher;
			matcher = patternEmail.matcher ( email );
			return matcher.matches ( );
			
	 }
	 
	 public static
	 String generateRandomString ( int len )
	 {
			
			StringBuilder buffer = new StringBuilder ( );
			for ( int i = 0 ; i < len ; ++i )
				 buffer.append ( STRING.charAt ( RANDOM.nextInt ( STRING.length ( ) ) ) );
			return buffer.toString ( );
	 }
	 
	 public static
	 void causeGC ( )
	 {
			
			runFinalization ( );
			getRuntime ( ).gc ( );
			gc ( );
			System.out.println ( ".GC." );
	 }
	 
	 public static
	 String randomBase64String ( int len )
	 {
			
			byte[] bytes  = new byte[ len ];
			Random random = new Random ( );
			random.nextBytes ( bytes );
			return com.nepolix.misha.commons.Base64.toBase64 ( bytes );
	 }
	 
	 public static
	 long randomLong ( )
	 {
			
			return RANDOM.nextLong ( );
	 }
	 
	 public static
	 int randomInt ( )
	 {
			
			return RANDOM.nextInt ( );
	 }
	 
	 public static
	 Random getRandom ( )
	 {
			
			return RANDOM;
	 }
	 
	 public static
	 < T > Set< T > findCommonSet ( Set< T > set1 ,
																	Set< T > set2 )
	 {
			
			if ( set1 == null || set2 == null ) throw new NullPointerException ( "neither sets must be null" );
			Set< T > smallSet;
			Set< T > largeSet;
			if ( set1.size ( ) != set2.size ( ) )
			{
				 smallSet = set1.size ( ) < set2.size ( ) ? set1 : set2;
				 largeSet = set2.size ( ) < set1.size ( ) ? set1 : set2;
			}
			else
			{
				 smallSet = set1;
				 largeSet = set2;
			}
			Set< T > common = new HashSet< T > ( );
			for ( T t : smallSet )
				 if ( largeSet.contains ( t ) ) common.add ( t );
			return common;
	 }
	 
	 public static
	 File createTempFile ( String prefix ,
												 String suffix )
	 {
			
			if ( prefix.length ( ) < 3 ) throw new IllegalArgumentException ( "Prefix string too short" );
			if ( suffix == null ) suffix = ".tmp";
			File tmpDir = tmpdir;
			prefix = ( new File ( prefix ) ).getName ( );
			String name = prefix + suffix;
			return new File ( tmpDir , name );
	 }
	 
	 //////////////////////////////////////////////////////////////////////////////////////////////
	 //////////////////////////////////////////////////////////////////////////////////////////////
	 //////////////////////////////////////////////////////////////////////////////////////////////
	 //////////////////////////////////////////////////////////////////////////////////////////////
	 //////////////////////////////////////////////////////////////////////////////////////////////
	 public static
	 < T > T getFirstElement ( final Iterable< T > elements )
	 {
			
			if ( elements == null ) return null;
			
			return elements.iterator ( ).next ( );
	 }
	 
	 public static
	 < T > T getLastElement ( final Iterable< T > elements )
	 {
			
			final Iterator< T > itr         = elements.iterator ( );
			T                   lastElement = itr.next ( );
			
			while ( itr.hasNext ( ) )
			{
				 lastElement = itr.next ( );
			}
			
			return lastElement;
	 }
	 
	 public static
	 < T > List< T > singletonList ( T t )
	 {
			
			List< T > list = new ArrayList< T > ( );
			list.add ( t );
			return list;
	 }
	 
	 public static
	 < T > Set< T > singletonSet ( T t )
	 {
			
			Set< T > list = new HashSet< T > ( );
			list.add ( t );
			return list;
	 }
	 
	 public static
	 boolean validateHexColor ( final String hexColor )
	 {
			
			Matcher matcher = pattern.matcher ( hexColor );
			return matcher.matches ( );
	 }
}
