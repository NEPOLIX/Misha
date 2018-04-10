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

package com.nepolix.misha.db.cold.service.athena;

import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.cold.client.TestStore;
import com.nepolix.misha.db.exception.MishaSQLFormatException;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.nepolix.misha.db.cold.service.athena.AthenaHandler.getAthenaHandler;

/**
 * @author Behrooz Shahriari
 * @since 3/9/18
 */
public
class AthenaQuery
{
	 
	 public static
	 void main ( String[] args )
					 throws
					 SQLException,
					 MishaSQLFormatException
	 {
			
			String       collectionName  = MModel.getModelName ( TestStore.TestObject.class );
			final String AWS_ACCESS_KEY  = "";
			final String AWS_PRIVATE_KEY = "";
			ColdDBConstants.setAwsAccessKey ( AWS_ACCESS_KEY );
			ColdDBConstants.setAwsPrivateKey ( AWS_PRIVATE_KEY );
			ColdDBConstants.setAthenaUrl ( "jdbc:awsathena://athena.us-west-2.amazonaws.com:443" );
			getAthenaHandler ( ).createTables ( ).loadPartition ( true ).loadPartition ( false );
			Set< String > mids = new LinkedHashSet<> ( );
			mids.add ( "1" );
			mids.add ( "2" );
			getAthenaHandler ( ).getLastObjects ( mids , collectionName ).forEach ( x -> System.out.println ( MJSON.toString ( x ) ) );
			System.out.println ( getAthenaHandler ( ).getObjects ( mids , collectionName , 2 ).toString ( ) );
			
			JSONObject query    = new JSONObject ( );
			JSONObject subQuery = new JSONObject ( );
			subQuery.putOpt ( "$<" , 3 ).putOpt ( "$>" , -10 );
			query.putOpt ( "v" , subQuery.clone ( ) );
			subQuery.clear ( );
			subQuery.putOpt ( "$=" , false );
			query.putOpt ( "deleted" , subQuery.clone ( ) );
			subQuery.clear ( );
			Long a = 10000L;
			subQuery.putOpt ( "$>" , a );
			query.putOpt ( "updateTime" , subQuery.clone ( ) );
			subQuery.clear ( );
			System.out.println ( getAthenaHandler ( ).find ( query , 2 , collectionName ) );
	 }
}
