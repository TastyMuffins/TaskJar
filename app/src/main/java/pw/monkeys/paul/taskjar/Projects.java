package pw.monkeys.paul.taskjar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pw.monkeys.paul.taskjar.R;

public class Projects extends Activity {
    Context context;
    RequestQueue requestQueue;
    String getProjectsURL = "http://taskjar.monkeys.pw/project/0";
    private ListView projList;
    private ArrayList<ProjectItem> projArray = new ArrayList<ProjectItem>();
    private ListProjectAdapter projListAdapter;
    AlertDialog newProjDialog;
    AlertDialog alertDialog;
    String deviceId;

    JsonObjectRequest getProjectRequest = new JsonObjectRequest(Request.Method.GET, getProjectsURL, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    JSONArray projectsJSONArray = null;
                    try {
                        projectsJSONArray = jsonObject.getJSONArray("projectList");
                        projArray.clear();
                        for (int i = 0; i < projectsJSONArray.length(); i++) {
                            projArray.add(new ProjectItem(
                                    projectsJSONArray.getJSONObject(i).getString("id"),
                                    projectsJSONArray.getJSONObject(i).getString("name"),
                                    projectsJSONArray.getJSONObject(i).getString("creator"),
                                    projectsJSONArray.getJSONObject(i).getString("hours"),
                                    projectsJSONArray.getJSONObject(i).getString("hoursWorked")
                            ));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    projListAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
        }
    }){
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String>  params = new HashMap<String, String>();
            params.put("X-User", deviceId);
            return params;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        deviceId = deviceUuid.toString();

        setContentView(R.layout.activity_projects);
        requestQueue = Volley.newRequestQueue(this);
        projListAdapter = new ListProjectAdapter(this,android.R.layout.simple_list_item_1,projArray);
        projList = (ListView)findViewById(R.id.projectListView);
        projList.setAdapter(projListAdapter);

        projList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Intent myIntent = new Intent(getBaseContext(), Tasks.class);
               myIntent.putExtra("id", projArray.get(i).getId()); //Optional parameters
               myIntent.putExtra("deviceId",deviceId);
               startActivity(myIntent);
            }
        });
        alertDialog = new AlertDialog.Builder(this).create();
        projList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int taskid = i;
                alertDialog.setTitle("Delete Project?");
                alertDialog.setMessage("Are you sure you want to delete the entire project "+projArray.get(taskid).getName()+" including all tasks inside?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, "http://taskjar.monkeys.pw/task/"+projArray.get(taskid).getId(),null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        try {
                                            Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                                            requestQueue.add(getProjectRequest);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
                            }}){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("X-User", deviceId);
                                return params;
                            }
                        };
                        requestQueue.add(deleteRequest);
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        requestQueue.add(getProjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.projects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        int id = item.getItemId();
        if (id == R.id.accountInfoMenuBtn) {
            return true;
        }
        if (id== R.id.createProjMenuBtn)
        {
            View layout = inflater.inflate(R.layout.newproj,null);
            final EditText nameTxt = (EditText)layout.findViewById(R.id.newProjName);
            final EditText descriptionTxt = (EditText)layout.findViewById(R.id.newProjDescription);
            final EditText hoursTxt = (EditText)layout.findViewById(R.id.newProjHours);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            builder.setTitle("Create a New Task ");
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Create Project", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        JSONObject jsonUpdate = new JSONObject();
                        jsonUpdate.put("name", nameTxt.getText());
                        jsonUpdate.put("description",descriptionTxt.getText());
                        jsonUpdate.put("hours",hoursTxt.getText());
                        JsonObjectRequest createProjectRequest = new JsonObjectRequest(Request.Method.POST, "http://taskjar.monkeys.pw/project/0", jsonUpdate,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        try {
                                            Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        ){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("X-User", deviceId);
                                return params;
                            }
                        };
                        requestQueue.add(createProjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestQueue.add(getProjectRequest);
                    newProjDialog.dismiss();
                }
            });
            newProjDialog = builder.create();
            newProjDialog.show();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
