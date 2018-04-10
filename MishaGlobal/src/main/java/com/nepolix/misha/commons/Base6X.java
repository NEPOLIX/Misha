/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.commons;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Behrooz Shahriari
 * @since 9/17/16
 */
public final
class Base6X
{
	 
	 private static final String WAS_NULL = " was null.";
	 
	 private static final String WAS_EMPTY = " was empty.";
	 
	 private static final String WAS_ZERO = " was zero.";
	 
	 private static final BigInteger BASE = BigInteger.valueOf ( 62 );
	 
	 
	 public static
	 String toBase64 ( String s )
	 {
			
			return Base64.getEncoder ( ).encodeToString ( s.getBytes ( ) );
	 }
	 
	 public static
	 String toBase64 ( byte[] bytes )
	 {
			
			return Base64.getEncoder ( ).encodeToString ( bytes );
	 }
	 
	 public static
	 String fromBase64 ( String base64String )
	 {
			
			return new String ( Base64.getDecoder ( ).decode ( base64String.getBytes ( ) ) );
	 }
	 
	 public static
	 byte[] fromBase64_ ( String base64String )
	 {
			
			return Base64.getDecoder ( ).decode ( base64String.getBytes ( ) );
	 }
	 
	 public static
	 BigInteger fromBase62 ( String base62String )
	 {
			
			return fromBase62 ( base62String , UTF_8 );
	 }
	 
	 private static
	 BigInteger fromBase62 ( String base62String ,
													 Charset charset )
	 {
			
			ensureNotNull ( "Base62 String" , base62String );
			ensureNotNull ( "Charset" , charset );
			return fromBase62 ( base62String.getBytes ( charset ) );
	 }
	 
	 private static
	 BigInteger fromBase62 ( byte[] base62Bytes )
	 {
			
			BigInteger res        = BigInteger.ZERO;
			BigInteger multiplier = BigInteger.ONE;
			for ( int i = base62Bytes.length - 1 ; i >= 0 ; i-- )
			{
				 res = res.add ( multiplier.multiply ( BigInteger.valueOf ( alphabetValueOf ( base62Bytes[ i ] ) ) ) );
				 multiplier = multiplier.multiply ( BASE );
			}
			return res;
	 }
	 
	 private static
	 int alphabetValueOf ( byte bytee )
	 {
			
			if ( Character.isLowerCase ( bytee ) )
			{
				 return bytee - ( 'a' - 10 );
			}
			else
				 if ( Character.isUpperCase ( bytee ) )
				 {
						return bytee - ( 'A' - 10 - 26 );
				 }
			return bytee - '0';
	 }
	 
	 public static
	 String toBase62 ( String s )
	 {
			
			final byte[] bytes = s.getBytes ( );
			ensureNotEmpty ( "Bytes" , bytes );
			return toBase62 ( new BigInteger ( bytes ) );
	 }
	 
	 private static
	 String toBase62 ( final BigInteger number )
	 {
			
			ensureNotNull ( "Number" , number );
			ensureGreater ( "Number" , number , BigInteger.ZERO );
			
			if ( BigInteger.ZERO.compareTo ( number ) == 0 )
			{
				 return "0";
			}
			
			BigInteger value = number.add ( BigInteger.ZERO );
			
			StringBuilder sb = new StringBuilder ( );
			while ( BigInteger.ZERO.compareTo ( value ) < 0 )
			{
				 BigInteger[] quotientReminder = value.divideAndRemainder ( BASE );
				 int          remainder        = quotientReminder[ 1 ].intValue ( );
				 if ( remainder < 10 )
				 {
						sb.insert ( 0 , ( char ) ( remainder + '0' ) );
				 }
				 else
						if ( remainder < 10 + 26 )
						{
							 sb.insert ( 0 , ( char ) ( remainder + 'a' - 10 ) );
						}
						else
						{
							 sb.insert ( 0 , ( char ) ( remainder + 'A' - 10 - 26 ) );
						}
				 // quotient
				 value = quotientReminder[ 0 ];
			}
			return sb.toString ( );
	 }
	 
	 /**
		* Ensure not null.
		*
		* @param name
		* 				Name
		* @param value
		* 				Value
		*
		* @throws IllegalArgumentException
		* 				if $value is null
		*/
	 private static
	 void ensureNotNull ( String name ,
												Object value )
	 {
			
			if ( value != null )
			{
				 return;
			}
			throw new IllegalArgumentException ( name + WAS_NULL );
	 }
	 
	 /**
		* Ensure not empty.
		*
		* @param name
		* 				Name
		* @param value
		* 				Value
		*
		* @throws IllegalArgumentException
		* 				if $value is null or empty
		*/
	 private static
	 void ensureNotEmpty ( String name ,
												 byte[] value )
	 {
			
			ensureNotNull ( name , value );
			if ( value.length == 0 )
			{
				 throw new IllegalArgumentException ( name + WAS_EMPTY );
			}
	 }
	 
	 private static
	 void ensureGreater ( String name ,
												BigInteger value ,
												BigInteger lower )
	 {
			
			ensureNotNull ( name , value );
			if ( lower.compareTo ( value ) > 0 )
			{
				 throw new IllegalArgumentException ( name + " was lesser than or equal to " + lower );
			}
	 }
}
