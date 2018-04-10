package com.nepolix.misha.json.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;

/**
 * @author Behrooz Shahriari
 * @since 10/14/16
 */
public final
class MJSON
{
	 
	 public static
	 JSONObject toJSON ( Object object )
	 {

//			JSONParser jsonParser = new JSONParser ( object );
//			return jsonParser.parse ( );
			
			if ( object == null ) return new JSONObject ( );
			
			JSONObject   jsonObject = null;
			ObjectMapper mapper     = new ObjectMapper ( );
			mapper.setVisibility (
							mapper.getSerializationConfig ( ).getDefaultVisibilityChecker ( ).withFieldVisibility ( JsonAutoDetect.Visibility.ANY ).withGetterVisibility ( JsonAutoDetect.Visibility.NONE )
										.withSetterVisibility ( JsonAutoDetect.Visibility.NONE ).withCreatorVisibility ( JsonAutoDetect.Visibility.NONE ) ).configure ( SerializationFeature.FAIL_ON_EMPTY_BEANS , false );
			try
			{
				 jsonObject = new JSONObject ( mapper.writeValueAsString ( object ) );
				 
				 if ( jsonObject.has ( "_id" ) && ( jsonObject.getString ( "_id" ) == null || jsonObject.getString ( "_id" ).equals ( "null" ) ) )
				 {
						jsonObject.remove ( "_id" );
				 }
			}
			catch ( JSONException | JsonProcessingException e )
			{
				 e.printStackTrace ( );
				 return jsonObject;
			}
			return jsonObject;
	 }
	 
	 public static
	 JSONObject toJSON ( String data )
	 {
			
			JSONObject object = null;
			try
			{
				 object = new JSONObject ( data );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return object;
	 }
	 
	 public static
	 < T > T toObject ( JSONObject jsonObject ,
											Class clazz )
	 {
			
			try
			{
				 if ( jsonObject == null || jsonObject.isEmpty ( ) ) return null;
				 ObjectMapper mapper = new ObjectMapper ( );
				 mapper.setVisibility (
								 mapper.getSerializationConfig ( ).getDefaultVisibilityChecker ( ).withFieldVisibility ( JsonAutoDetect.Visibility.ANY ).withGetterVisibility ( JsonAutoDetect.Visibility.NONE )
											 .withSetterVisibility ( JsonAutoDetect.Visibility.NONE ).withCreatorVisibility ( JsonAutoDetect.Visibility.NONE ) )
							 .configure ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES , false ).configure ( SerializationFeature.FAIL_ON_EMPTY_BEANS , false );
				 return mapper.readValue ( jsonObject.toString ( ) , ( Class< T > ) clazz );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 System.out.println ( Utils.getCurrentCodeInfo ( Thread.currentThread ( ) ) + "\n" + e.getLocalizedMessage ( ) + "\n" + e.getMessage ( ) );
			}
			return null;
	 }
	 
	 public static
	 < T > T toObject ( String json ,
											Class clazz )
	 {
			
			return toObject ( MJSON.toJSON ( json ) , clazz );
	 }
	 
	 public static
	 void print ( JSONObject jsonObject )
	 {
			
			System.out.println ( toString ( jsonObject ) );
	 }
	 
	 public static
	 String toString ( Object o )
	 {
			
			return toString ( toJSON ( o ) );
	 }
	 
	 public static
	 String toString ( JSONObject jsonObject )
	 {
			
			try
			{
				 return jsonObject.toString ( 2 );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
}
