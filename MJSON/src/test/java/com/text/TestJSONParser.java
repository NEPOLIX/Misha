/******************************************************************************
 * Copyright © 2015-7532 NEX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
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

package com.text;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.math.BigInteger;

/**
 * @author Behrooz Shahriari
 * @since 11/1/16
 */
public
class TestJSONParser
{
	 
	 public static
	 void main ( String[] args )
					 throws
					 JSONException
	 {
			
			ClassA     classA     = new ClassA ( );
			JSONObject jsonObject = MJSON.toJSON ( classA );
			classA = ( ClassA ) MJSON.toObject ( jsonObject, ClassA.class );
			System.out.println ( jsonObject.toString ( 2 ) );
			
	 }
}


class ClassA
{
	 
	 public
	 enum Type
	 {
			A,
			B,
			C,
	 }
	 
	 Type type = Type.A;
	 
	 Type type1 = null;
	 
	 Type[] types = new Type[] { type };
	 
	 private byte aByte;
	 
	 byte[] bytes = new byte[ 0 ];
	 
	 private short aShort = 1;
	 
	 private short[] shorts;
	 
	 
	 private int[] ints;
	 
	 private int anInt = 43;
	 
	 private String string;
	 
	 String[] strings;
	 
	 long aLong;
	 
	 long[] longs;
	 
	 float aFloat;
	 
	 float[] floats = new float[] {
					 87.09f ,
					 987f
	 };
	 
	 double aDouble = 8945.98;
	 
	 private double[] doubles;
	 
	 private Byte aByte_;
	 
	 private Byte[] bytes_;
	 
	 Short aShort_ = 8;
	 
	 Short[] shorts_ = new Short[] {
					 9 ,
					 7 ,
					 -9
	 };
	 
	 Integer integer;
	 
	 Integer[] integers;
	 
	 Long aLong_;
	 
	 private Long[] longs_;
	 
	 Double aDouble_;
	 
	 Double[] doubles_;
	 
	 Float aFloat_;
	 
	 Float[] floats_;
	 
	 private BigInteger bigIntegers[];
	 
	 @JsonManagedReference
	 ClassB classB = new ClassB ( this );
	 
	 public
	 ClassA ( )
	 {
			
			bigIntegers = new BigInteger[] {
							new BigInteger ( "9879879779987" ) ,
							new BigInteger ( "080879658907675" )
			};
	 }
}

class ClassB
{
	 
	 private String a = "adasd";
	 
	 @JsonBackReference
	 private ClassA classA;
	 
	 public
	 ClassB ( )
	 {
			
	 }
	 
	 public
	 ClassB ( ClassA classA )
	 {
			
			this.classA = classA;
	 }
}