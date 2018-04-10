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

package com.nepolix.misha.db.cold.client;

import com.nepolix.misha.db.model.IMigrationProtocol;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.id.client.MishaID;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 2/23/18
 */
public
class TestStore
{
	 
	 private final static String MISHA_ID_ADDRESS = "";
	 
	 public final static String AWS_ACCESS_KEY = "";
	 
	 public final static String AWS_PRIVATE_KEY = "";
	 
	 public final static String ATHENA_DB_URL = "jdbc:awsathena://athena.us-west-2.amazonaws.com:443";
	 
	 public static
	 void main ( String[] args )
	 {
			
			MishaID.initInetAddress ( MISHA_ID_ADDRESS );
			String[] address = new String[] { "colddb-west.testsite.com" };
			MishaColdDB.init ( MishaID.getMishaID ( ) , address , 0 , AWS_ACCESS_KEY , AWS_PRIVATE_KEY , ATHENA_DB_URL , "test" );
			List< MModel > objects = new ArrayList<> ( );
			for ( int i = 0 ; i < 100 ; ++i )
			{
				 TestObject testObject = new TestObject ( i );
				 testObject.setMid ( "" + i );
				 testObject.$save ( );
			}
			MishaColdDB.getInstance ( ).sync ( false );
			for ( int i = 0 ; i < 100 ; ++i )
			{
				 TestObject testObject = new TestObject ( i );
				 testObject.setMid ( "t-" + i );
				 objects.add ( testObject );
			}
			MishaColdDB.getInstance ( ).save ( objects );
			MishaColdDB.getInstance ( ).sync ( false );
			MishaColdDB.getInstance ( ).sync ( true );
	 }
	 
	 public static
	 class TestObject
					 extends MModel
	 {
			
			int v;
			
			public
			TestObject ( )
			{
			
			}
			
			public
			TestObject ( int v )
			{
				 
				 this.v = v;
			}
			
			public
			int getV ( )
			{
				 
				 return v;
			}
			
			public
			void setV ( int v )
			{
				 
				 this.v = v;
			}
			
			@Override
			public
			String modelName ( )
			{
				 
				 return "TestObject-cDB";
			}
			
			@Override
			protected
			IMigrationProtocol $getMigrationProtocol ( int version )
			{
				 
				 return null;
			}
			
			@Override
			public
			void $save ( )
			{
				 
				 $save ( this , MishaColdDB.getInstance ( ) );
			}
			
			@Override
			public
			void $delete ( boolean keep )
			{
			
			}
	 }
}
