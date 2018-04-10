# REST client
A library to make API calls with limitation, only accept json data format.
Also can execute curl via process and pass the result through callback or in blocking mode.

```
			WebClient webClient = WebClient.getInstance ( );
			CUrlParameter[] cUrlParameters = new CUrlParameter[] {
							new CUrlParameter ( "--data-urlencode" , "To=" + userPhone ) ,
							new CUrlParameter ( "--data-urlencode" , "From=" + ourPhoneNumber ) ,
							new CUrlParameter ( "--data-urlencode" , "Body=" + messagePart ) ,
							new CUrlParameter ( "-u" , accountSid + ":" + authToken )
			};
			JSONObject res = webClient.cUrl ( "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json" , WebClient.RESTMethod.POST , cUrlParameters );
```

```
			String     url       = ...;
			WebClient  webClient = WebClient.getInstance ( );
			JSONObject body      = new JSONObject ( );
			body.putOpt ( "customer" , MJSON.toJSON ( mCustomer ) ).putOpt ( "vendor" , MJSON.toJSON ( mVendor ) ).putOpt ( "vendor_deletion" , deletedByVendor );
			webClient.call ( DELETE , url , null , null , body , null );
```
