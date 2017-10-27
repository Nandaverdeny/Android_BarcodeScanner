/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.samples.vision.barcodereader.MovementRequestDetails;
import com.google.android.gms.samples.vision.barcodereader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import framework.library.common.helper.date.DateHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class MovementRequestFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    private ListView listView ;
    private ArrayList<HashMap<String,String>> movementlist;
    private SharedPreferences user;
    private String departmentid = null;
    private String title;

    /**
     * @return a new instance of {@link MovementRequestFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static MovementRequestFragment newInstance(CharSequence title, int indicatorColor,
                                                      int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

        MovementRequestFragment fragment = new MovementRequestFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_movement_request, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {

            title = args.getCharSequence(KEY_TITLE).toString();
            //Log.e("App ", "onViewCreated: " + title );
            // Get DepartmentID from SharedPreferences
            user = this.getActivity().getSharedPreferences("UserStore",MODE_PRIVATE);
            departmentid = user.getString("DepartmentID", null);
            Log.e("App ", "DepartmentID: " + departmentid );
            movementlist = new ArrayList<>();
            listView = (ListView) view.findViewById(R.id.movementlist);
            new MovementRequestTask().execute();

        }
    }

    protected class MovementRequestTask extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params)
        {
            String strUrl;

            // tab Completed
            if (title.equals(getString(R.string.tab_completed)))
            {
                if(departmentid != null)
                {
                    //strUrl = "http://escurity001:1130/api/movementrequest/getmovementrequesttomove";
                    strUrl = "http://escurity001:1330/api/movementrequest/getmovementrequesttomovebydepartment/"+ Integer.parseInt(departmentid);
                }
                else
                {
                    strUrl = "http://escurity001:1130/api/movementrequest/getmovementrequesttomove";
                }
            }
            // tab On Progress
            else
            {
                if(departmentid != null)
                {
                    //strUrl = "http://escurity001:1130/api/movementrequest/getmovementrequesttomove";
                    strUrl = "http://escurity001:1330/api/movementrequest/getmovementrequesttomovebydepartment/"+ Integer.parseInt(departmentid);
                }
                else
                {
                    strUrl = "http://escurity001:1130/api/movementrequest/getmovementrequesttomove";
                }
            }
            Log.e("App","strUrl : " + strUrl);
            HttpURLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(strUrl);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }

                return new JSONObject(stringBuffer.toString());
            }
            catch(Exception ex)
            {
                Log.e("App", "MovementRequestTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {
            Log.e("App", "response :" + response);
            if(response != null)
            {
                try {
                    // Bind response to View
                    JSONArray jsonArray = response.optJSONArray("obj");
                    Log.e("App", "JsonArray isNull :" + response.isNull("obj"));

                    if (jsonArray != null)
                    {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jb = jsonArray.getJSONObject(i);
                            JSONObject movement = jb.getJSONObject("MovementRequest");

                            String ID = movement.optString("ID");
                            String Description = movement.optString("Description");
                            String Location = movement.optString("LocationName");
                            String MovementDate = DateHelper.ConvertToStringDate(movement.optString("MovementDate"), "ddMMyyyy", "EEE dd/MM/yyyy");

                            HashMap<String, String> mv = new HashMap<>();
                            mv.put("id", ID);
                            mv.put("description", Description);
                            mv.put("location", Location);
                            mv.put("movementdate", MovementDate);

                            movementlist.add(mv);
                        }
                    }
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),movementlist,
                            R.layout.list_item,new String[] {"id","description","location","movementdate"},
                            new int[] {R.id .id,R.id.description,R.id.Location,R.id.movementdate}
                    );
                    listView.setAdapter(adapter);
                    final ListAdapter A = adapter;
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            HashMap<String, Object> obj = (HashMap<String, Object>) A.getItem(i);
                            String text= (String) obj.get("id");

                            Intent intent = new Intent(getActivity(), MovementRequestDetails.class);
                            intent.putExtra("id", text);
                            startActivity(intent);
                        }
                    });

                    Log.e("App", "Success: " + response.getString("obj") );
                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }
        }
    }

}
