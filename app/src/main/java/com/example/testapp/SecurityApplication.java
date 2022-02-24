package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSettings;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.CognitoIdentityProvider;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SecurityApplication extends Application {
    public static final String SHADOW_PREFIX = "$aws/things/raspi/shadow";

    public static final String SHADOW_GET_TOPIC = SHADOW_PREFIX + "/get";
    public static final String SHADOW_GET_ACCEPTED_TOPIC = SHADOW_GET_TOPIC + "/accepted";
    public static final String SHADOW_GET_REJECTED_TOPIC = SHADOW_GET_TOPIC + "/rejected";

    public static final String SHADOW_UPDATE_TOPIC = SHADOW_PREFIX + "/update";
    public static final String SHADOW_UPDATE_DOCUMENTS_TOPIC = SHADOW_UPDATE_TOPIC + "/documents";

    public static final String CUSTOMER_SPECIFIC_ENDPOINT = "aw62mbu5dp5po-ats.iot.us-east-1.amazonaws.com";
    public static final String COGNITO_USER_POOL_ID = "us-east-1_KgdGVvm5L";
    public static final String COGNITO_IDENTITY_POOL_ID = "us-east-1:11c5508c-25db-4d40-baf1-6e15da71b02a";

    private static final Regions MY_REGION = Regions.US_EAST_1;
    public static RequestQueue requestQueue;
    public static AWSIotMqttManager mqttManager;
    public static CognitoCachingCredentialsProvider credentialsProvider;
    String clientId;
    public static CognitoUserPool pool;
    public static CognitoUserAttributes attrs;

    public static String TAG = "secapp";

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy bruh = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(bruh);

        WebSocketFactory factory = new WebSocketFactory();
        try {
            //WebSocket ws = factory.createSocket("ws://xn--yh8hfqgj.ws:8765/");
            WebSocket ws = factory.createSocket("ws://10.0.2.2:8765/");
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket socket, String message) throws Exception {
                    Log.e(TAG, "yoooo " + message);
                }
            });
            ws.connect();
            ws.sendText("waddup");
            Log.e(TAG, "nice?");
        } catch (IOException | WebSocketException e) {
            Log.e(TAG, "" + e);
        }

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_IDENTITY_POOL_ID,
                MY_REGION
        );
        credentialsProvider.refresh();

        Log.e(TAG, String.valueOf(credentialsProvider.getLogins()));
        AWSSessionCredentials creds = credentialsProvider.getCredentials();
        Log.e(TAG, creds.getSessionToken());

        clientId = UUID.randomUUID().toString();
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
        //pool = new CognitoUserPool(this, configuration);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
