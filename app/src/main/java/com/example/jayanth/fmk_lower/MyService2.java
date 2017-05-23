package com.example.jayanth.fmk_lower;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jayanth on 4/12/2017.
 */
public class MyService2 extends Service
{

    private static int cellid, celllac, mcc, mnc;
    static String smsNumberToSend;
    private static SmsManager smsManager;
    private static String status, balance, lattitude, longitude, accuracy, address;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle bundle = intent.getExtras();
        smsNumberToSend = (String) bundle.getCharSequence("extraSmsNumber");
        smsManager = SmsManager.getDefault();

        cell_details();

        if(isConnected())
        {
          new HttpAsyncTask().execute();
        }
        else
        {
            cell_location();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void cell_details()
    {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

        cellid= cellLocation.getCid();
        celllac = cellLocation.getLac();

        String networkOperator = telephonyManager.getNetworkOperator();
        mcc = Integer.parseInt(networkOperator.substring(0, 3));
        mnc = Integer.parseInt(networkOperator.substring(3));
    }

    public void cell_location()
    {
        smsManager.sendTextMessage(smsNumberToSend, null,
                "Country Code = "+mcc+"\n"+ "Network Code = "+mnc+"\n"+ "cellid = "+cellid+"\n"+ "Area Code = "+celllac+"\n View http://locationapi.org/ for the location", null, null);

        Log.d("GSM CELL ID",  String.valueOf(cellid));
        Log.d("GSM Location Code", String.valueOf(celllac));
        Log.d("GSM MCC Code", String.valueOf(mcc));
        Log.d("GSM MNC Code", String.valueOf(mnc));
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            return GET();
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    public String GET()
    {
        InputStream inputStream = null;
        String result = "";

        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://ap1.unwiredlabs.com/v2/process.php");
            JSONObject jsonObject = new JSONObject();
            JSONObject cell1 = new JSONObject();

            //making POST request.
            try
            {
                cell1.put("lac",celllac);
                cell1.put("cid",cellid);
                JSONArray cell_array = new JSONArray();
                cell_array.put(cell1);

                jsonObject.put("token","9f5cb1973a7ff8");
                jsonObject.put("radio","gsm");
                jsonObject.put("mcc",mcc);
                jsonObject.put("mnc",mnc);
                jsonObject.put("cells",cell_array);
                jsonObject.put("address",1);

                Log.d("CellLocation", String.valueOf(celllac));
                Log.d("GSM CELL ID",  String.valueOf(cellid));
                Log.d("GSM Location Code", String.valueOf(celllac));
                Log.d("GSM MCC Code", String.valueOf(mcc));
                Log.d("GSM MNC Code", String.valueOf(mnc));

                Log.d("jsonObject", jsonObject.toString());

                StringEntity se = new StringEntity( jsonObject.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);

                HttpResponse response = httpClient.execute(httpPost);
                inputStream = response.getEntity().getContent();

                result = convertInputStreamToString(inputStream);
                JSONObject json_result = new JSONObject(result);
                status = json_result.getString("status");
                balance = json_result.getString("balance");
                lattitude = json_result.getString("lat");
                longitude = json_result.getString("lon");
                accuracy = json_result.getString("accuracy");
                address = json_result.getString("address");

                if(inputStream != null)
                {
                    String message = "Accuracy : "+accuracy+" meters, Address :"+address+" Lattitude : "+lattitude+" Longitude : "+longitude;
                    smsManager.sendTextMessage(smsNumberToSend, null,"Around "+ accuracy +" mtrs of" + address, null, null);
                    Log.d("json result",result);
                }
                else
                    result = "Did not work!";

                Log.d("Http Post Response", result);
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public boolean isConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}