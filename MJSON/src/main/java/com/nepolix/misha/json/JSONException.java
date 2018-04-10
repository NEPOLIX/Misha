/******************************************************************************
 * Copyright © 2016-7532 HEX, Inc. [7EPOLIX]-(Behrooz Shahriari)              *
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

package com.nepolix.misha.json;

/**
 * @author Behrooz Shahriari
 * @since 9/17/16
 */


import com.nepolix.misha.commons.utils.Utils;

/**
 * Thrown to indicate a problem with the JSON API. Such problems include: <ul> <li>Attempts to parse or construct malformed documents <li>Use of null as a name <li>Use of numeric types not available
 * to JSON, such as {@link Double#isNaN() NaNs} or {@link Double#isInfinite() infinities}. <li>Lookups using an out of range index or nonexistant name <li>Type mismatches on lookups </ul>
 * <p>
 * <p>Although this is a checked exception, it is rarely recoverable. Most callers should simply wrap this exception in an unchecked exception and rethrow:
 * <pre>  public JSONArray toJSONObject() {
 *     try {
 *         JSONObject result = new JSONObject();
 *         ...
 *     } catch (JSONException e) {
 *         throw new RuntimeException(e);
 *     }
 * }</pre>
 */
public
class JSONException
				extends Exception
{
	 
	 
	 public
	 JSONException ( String s )
	 {
			
			super ( s );
	 }
	 
	 public static
	 JSONObject exceptionToJSON ( Exception e )
	 {
			
			JSONObject        eJsonObject         = new JSONObject ( );
			StackTraceElement stackTraceElement[] = e.getStackTrace ( );
			JSONArray         errorArray          = new JSONArray ( );
			for ( StackTraceElement element : stackTraceElement )
				 errorArray.put ( element.toString ( ) );
			String errCode = "" + Math.abs ( Utils.getRandom ( ).nextInt ( 100000 ) ) + Utils.generateRandomString ( 3 );
			eJsonObject.putOpt ( "error_cause" , e.toString ( ) );
			eJsonObject.putOpt ( "stack_trace" , errorArray );
			eJsonObject.putOpt ( "error" , "Internal error:" + e.getMessage ( ) + " (err_code:" + errCode + ")" );
			eJsonObject.putOpt ( "error_code" , errCode );
			return eJsonObject;
	 }
}