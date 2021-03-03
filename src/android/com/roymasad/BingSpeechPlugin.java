
package com.roymasad;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.PermissionHelper;
import android.Manifest;
import android.content.pm.PackageManager;

import android.util.Log;

import java.util.Date;

import com.microsoft.bing.speech.Conversation;
import com.microsoft.bing.speech.SpeechClientStatus;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

public class BingSpeechPlugin extends CordovaPlugin implements ISpeechRecognitionServerEvents {

  private static final String TAG = "BingSpeechPlugin";

  // put your bing key here
  private String bingkey = "---------------------------";

  private static String [] permissions = { 
    
    Manifest.permission.RECORD_AUDIO, 
    Manifest.permission.INTERNET,
    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
    Manifest.permission.ACCESS_WIFI_STATE,
    Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE
  
  };

  static CallbackContext ctx;

  MicrophoneRecognitionClient micClient = null;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.w(TAG, "Initializing BingSpeechPlugin");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    
    if(action.equals("init")) {
      // String arg = args.getString(0);
      Log.w(TAG, "init");


      PermissionHelper.requestPermissions(this, 0, permissions);

      try {
        

        final PluginResult result = new PluginResult(PluginResult.Status.OK, "INIT SUCCESS: "+ args.getString(0));
        callbackContext.sendPluginResult(result);

        // callbackContext.success("init echo :" + args.getString(0));
        Log.w(TAG, "ctx = callbackContext");

      }
      catch(Exception ex) {

        Log.w(TAG, "ctx error " + ex.toString());
        callbackContext.error("shit didnt work @ init");
        final PluginResult result = new PluginResult(PluginResult.Status.ERROR, "RESPONSE ERROR: "+ ex.toString());
        BingSpeechPlugin.ctx.sendPluginResult(result);

      }

    } else if(action.equals("start")) {
      
      try {
        Log.w(TAG, "start");
        BingSpeechPlugin.ctx = callbackContext;

        this.StartMic();

        final PluginResult result = new PluginResult(PluginResult.Status.OK, "START MIC SUCCESS: ");
        result.setKeepCallback(true);
        BingSpeechPlugin.ctx.sendPluginResult(result);
        
      }
      catch(Exception ex) {

        Log.w(TAG, "START ERR "+ ex.toString());
        final PluginResult result = new PluginResult(PluginResult.Status.ERROR, "START ERROR: "+ ex.toString());
        result.setKeepCallback(true);
        BingSpeechPlugin.ctx.sendPluginResult(result);

      }
    }
    else if(action.equals("stop")) {
      
      try{
        this.micClient.endMicAndRecognition();
        final PluginResult result = new PluginResult(PluginResult.Status.OK, "STOP SUCCESS: ");
        BingSpeechPlugin.ctx.sendPluginResult(result);
        Log.w(TAG, "STOP");
      }
      catch(Exception ex) {

        Log.w(TAG, "STOP ERR " + ex.toString());
        final PluginResult result = new PluginResult(PluginResult.Status.ERROR, "STOP ERROR: "+ ex.toString());
        BingSpeechPlugin.ctx.sendPluginResult(result);
        
      }
    }

    return true;
  }

  public void onFinalResponseReceived(final RecognitionResult response) {

    //response.RecognitionStatus == RecognitionStatus.EndOfDictation
    String result_text;
    if  (response.Results.length > 0) {
      
      try {
        result_text = response.Results[0].DisplayText;

        final PluginResult result = new PluginResult(PluginResult.Status.OK, result_text);

        result.setKeepCallback(true);
        
        BingSpeechPlugin.ctx.sendPluginResult(result);
        Log.w(TAG, "RESPONSE " + result_text);
      }
      catch(Exception ex) {

        Log.w(TAG, "RESPONSE ERR " + ex.toString());
        final PluginResult result = new PluginResult(PluginResult.Status.ERROR, "RESPONSE ERROR: "+ ex.toString());
        result.setKeepCallback(true);
        BingSpeechPlugin.ctx.sendPluginResult(result);

      }
    }
    
  }

  public void onIntentReceived(final String payload) {

  }

  public void onPartialResponseReceived(final String response) {

  }

  public void onError(final int errorCode, final String response) {

    // SpeechClientStatus.fromInt(errorCode) + errorCode
    // response

  }

  public void onAudioEvent(boolean recording) {

      if (recording) {
          
      }


      if (!recording) {

          try {
            this.micClient.endMicAndRecognition();
            StartMic();

            final PluginResult result = new PluginResult(PluginResult.Status.OK, "AUDIO EVENT SUCCESS: ");
            BingSpeechPlugin.ctx.sendPluginResult(result);
            Log.w(TAG, "AudioEvent");

          }
          catch(Exception ex) {

            Log.w(TAG, "AudioEvent Error " + ex.toString());
            final PluginResult result = new PluginResult(PluginResult.Status.ERROR, "AUDIEVENT ERROR: "+ ex.toString());
            BingSpeechPlugin.ctx.sendPluginResult(result);
    
          }
      }
  }
    
  private void StartMic() {

    try {

      Log.w(TAG, "StartMic");

      


      // JSONObject event = new JSONObject();
      // JSONArray results = new JSONArray();

      // event.put("type", "result");


      this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
              cordova.getActivity(),
              SpeechRecognitionMode.LongDictation ,
              "en-us",
              this,
              bingkey);

      this.micClient.setAuthenticationUri("");

      this.micClient.startMicAndRecognition();

      final PluginResult result = new PluginResult(PluginResult.Status.OK, "SPEECH RECOGNITION SUCCESS: "+ bingkey);
      result.setKeepCallback(true);
      BingSpeechPlugin.ctx.sendPluginResult(result);
      

    }
    catch(Exception ex) {

      Log.w(TAG, "StartMic ERR "+ex.toString());
      final PluginResult result = new PluginResult(PluginResult.Status.ERROR, "MIC ERROR: "+ ex.toString());
      BingSpeechPlugin.ctx.sendPluginResult(result);

    }

  }

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException
  {
      for(int r:grantResults)
      {
          if(r == PackageManager.PERMISSION_DENIED)
          {

              return;
          }
      }

  }

}
