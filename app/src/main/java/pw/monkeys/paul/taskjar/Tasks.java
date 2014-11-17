package pw.monkeys.paul.taskjar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tasks extends Activity {
    private String projectId="";
    private String deviceId="";
    Context context;
    private TabHost tabHost;
    private ListView todoList;
    private ListView progressList;
    private ListView completeList;

    private ArrayList<TaskItem> todoArray = new ArrayList<TaskItem>();
    private ArrayList<TaskItem> progressArray = new ArrayList<TaskItem>();
    private ArrayList<TaskItem> completeArray = new ArrayList<TaskItem>();

    ListTaskAdaptar todoListAdapter;
    ListTaskAdaptar progressListAdapter;
    ListTaskAdaptar completeListAdapter;
    AlertDialog alertDialog;
    AlertDialog TimerDialog;
    AlertDialog newTaskDialog;

    RequestQueue requestQueue;
    JsonObjectRequest todoListRequest;

    String getProject = "http://taskjar.monkeys.pw/project/";
    Response.Listener todolistResponse =  new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            JSONArray todo = null;
            JSONArray progress = null;
            JSONArray complete = null;
            try {
                setTitle(jsonObject.getString("name"));
                todo = jsonObject.getJSONArray("todo");
                progress = jsonObject.getJSONArray("progress");
                complete = jsonObject.getJSONArray("complete");
                todoArray.clear();
                for (int i = 0; i < todo.length(); i++) {
                    todoArray.add(new TaskItem(
                            todo.getJSONObject(i).getString("id"),
                            todo.getJSONObject(i).getString("name"),
                            todo.getJSONObject(i).getString("description"),
                            todo.getJSONObject(i).getString("creator"),
                            todo.getJSONObject(i).getString("assigned"),
                            todo.getJSONObject(i).getString("hours"),
                            todo.getJSONObject(i).getString("hoursWorked")
                    ));
                }
                progressArray.clear();
                for (int i = 0; i < progress.length(); i++) {
                    progressArray.add(new TaskItem(
                            progress.getJSONObject(i).getString("id"),
                            progress.getJSONObject(i).getString("name"),
                            progress.getJSONObject(i).getString("description"),
                            progress.getJSONObject(i).getString("creator"),
                            progress.getJSONObject(i).getString("assigned"),
                            progress.getJSONObject(i).getString("hours"),
                            progress.getJSONObject(i).getString("hoursWorked")
                    ));
                }
                completeArray.clear();
                for (int i = 0; i < complete.length(); i++) {
                    completeArray.add(new TaskItem(
                            complete.getJSONObject(i).getString("id"),
                            complete.getJSONObject(i).getString("name"),
                            complete.getJSONObject(i).getString("description"),
                            complete.getJSONObject(i).getString("creator"),
                            complete.getJSONObject(i).getString("assigned"),
                            complete.getJSONObject(i).getString("hours"),
                            complete.getJSONObject(i).getString("hoursWorked")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            todoListAdapter.notifyDataSetChanged();
            progressListAdapter.notifyDataSetChanged();
            completeListAdapter.notifyDataSetChanged();
        }};
    Response.ErrorListener simpleErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
        }};
            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        projectId = intent.getStringExtra("id");
        deviceId = intent.getStringExtra("deviceId");

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = getApplicationContext();
        todoListAdapter = new ListTaskAdaptar(this,android.R.layout.simple_list_item_1,todoArray);
        progressListAdapter = new ListTaskAdaptar(this,android.R.layout.simple_list_item_1,progressArray);
        completeListAdapter = new ListTaskAdaptar(this,android.R.layout.simple_list_item_1,completeArray);
        alertDialog = new AlertDialog.Builder(this).create();
        TimerDialog = new AlertDialog.Builder(this).create();

        requestQueue = Volley.newRequestQueue(this);
        todoListRequest = new JsonObjectRequest(Request.Method.GET, getProject+projectId, null,todolistResponse,simpleErrorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-User", deviceId);
                return params;
            }};
        setContentView(R.layout.activity_tasks);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec toDoTab = tabHost.newTabSpec("todoTab");
        toDoTab.setIndicator("To Do");
        toDoTab.setContent(R.id.todoList);
        tabHost.addTab(toDoTab);

        TabHost.TabSpec progressTab = tabHost.newTabSpec("progressTab");
        progressTab.setIndicator("In Progress");
        progressTab.setContent(R.id.progressList);
        tabHost.addTab(progressTab);

        TabHost.TabSpec completedTab = tabHost.newTabSpec("completedTab");
        completedTab.setIndicator("Completed");
        completedTab.setContent(R.id.completeList);
        tabHost.addTab(completedTab);

        todoList = (ListView)findViewById(R.id.todoList);
        progressList = (ListView)findViewById(R.id.progressList);
        completeList = (ListView)findViewById(R.id.completeList);

        todoList.setAdapter(todoListAdapter);
        todoList.setLongClickable(true);
        progressList.setAdapter(progressListAdapter);
        completeList.setAdapter(completeListAdapter);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                JsonObjectRequest todoListRequest = new JsonObjectRequest(Request.Method.GET, getProject+projectId, null,todolistResponse,simpleErrorListener);
                requestQueue.add(todoListRequest);
              if(tabHost.getCurrentTab()==0) {
                  //Toast.makeText(getApplicationContext(), "Loading to do", Toast.LENGTH_SHORT).show();
              }
              if(tabHost.getCurrentTab()==1) {
                  //Toast.makeText(getApplicationContext(), "Loading progress", Toast.LENGTH_SHORT).show();
              }
              if(tabHost.getCurrentTab()==2) {
                 //Toast.makeText(getApplicationContext(), "Loading complete", Toast.LENGTH_SHORT).show();
              }
            }
        });
        //begin To Do item listeners
        todoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int taskid = i;
              //Toast.makeText(getApplicationContext(),"Clicked on " + todoArray.get(i).getName(),Toast.LENGTH_SHORT).show();
                //initate request and make dialog for task i
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle(todoArray.get(taskid).getName());
                alertDialog.setMessage(todoArray.get(taskid).getDescription());
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Begin Task", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonUpdate = new JSONObject();
                           //jsonUpdate.put("id",todoArray.get(taskid).getId());
                            jsonUpdate.put("time","0");
                            jsonUpdate.put("complete","0");
                            JsonObjectRequest updateTaskRequest = new JsonObjectRequest(Request.Method.PUT, "http://taskjar.monkeys.pw/task/"+todoArray.get(taskid).getId(), jsonUpdate,
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
                                }}){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("X-User", deviceId);
                                    return params;
                                }
                            };
                            requestQueue.add(updateTaskRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final long startTime = new Date().getTime();
                        TimerDialog.setCanceledOnTouchOutside(false);
                        TimerDialog.setTitle("Currently working on: "+todoArray.get(taskid).getName());
                        TimerDialog.setMessage("Good luck on your task!");
                        TimerDialog.setButton(Dialog.BUTTON_NEGATIVE,"Stop working on this Task", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                long timeElasped = (new Date().getTime() - startTime)/1000;
                                try {
                                    JSONObject jsonUpdate = new JSONObject();
                                    //jsonUpdate.put("id",todoArray.get(taskid).getId());
                                    jsonUpdate.put("time",timeElasped);
                                    jsonUpdate.put("complete","0");
                                    JsonObjectRequest updateTaskRequest = new JsonObjectRequest(Request.Method.PUT, "http://taskjar.monkeys.pw/task/"+todoArray.get(taskid).getId(), jsonUpdate,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject jsonObject) {
                                                    try {
                                                        Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                                                        requestQueue.add(todoListRequest);
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
                                    requestQueue.add(updateTaskRequest);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                TimerDialog.dismiss();
                            }
                        });
                        TimerDialog.setButton(Dialog.BUTTON_POSITIVE,"Task Complete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //send request to server to begin working on task
                                long timeElasped = (new Date().getTime() - startTime)/1000;
                                try {
                                    JSONObject jsonUpdate = new JSONObject();
                                    //jsonUpdate.put("id",todoArray.get(taskid).getId());
                                    jsonUpdate.put("time",timeElasped);
                                    jsonUpdate.put("complete","1");
                                    JsonObjectRequest updateTaskRequest = new JsonObjectRequest(Request.Method.PUT, "http://taskjar.monkeys.pw/task/"+todoArray.get(taskid).getId(), jsonUpdate,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject jsonObject) {
                                                    try {
                                                        Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                                                        requestQueue.add(todoListRequest);
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
                                    requestQueue.add(updateTaskRequest);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(context,"Task Completed in " + timeElasped + " seconds",Toast.LENGTH_SHORT).show();
                                TimerDialog.dismiss();
                            }
                        });
                        // alertDialog.setIcon(R.drawable.icon);
                        TimerDialog.show();
                    }
                });
               // alertDialog.setIcon(R.drawable.icon);
                alertDialog.show();
            }
        });
                todoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final int taskid = i;
                        alertDialog.setTitle("Delete Task?");
                        alertDialog.setMessage("Are you sure you want to delete "+todoArray.get(taskid).getName());
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, "http://taskjar.monkeys.pw/task/"+todoArray.get(taskid).getId(),null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {
                                                try {
                                                    Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                                                    requestQueue.add(todoListRequest);
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
        requestQueue.add(todoListRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tasks, menu);
        return true;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.newTaskBtn) {
            View layout = inflater.inflate(R.layout.newtask,null);
            final EditText nameTxt = (EditText)layout.findViewById(R.id.newTaskName);
            final EditText descriptionTxt = (EditText)layout.findViewById(R.id.newTaskDescription);
            final EditText hoursTxt = (EditText)layout.findViewById(R.id.newTaskHours);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            builder.setTitle("Create a New Task ");
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Create Task", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        JSONObject jsonUpdate = new JSONObject();
                        jsonUpdate.put("id",projectId);
                        jsonUpdate.put("name", nameTxt.getText());
                        jsonUpdate.put("description",descriptionTxt.getText());
                        jsonUpdate.put("hours",hoursTxt.getText());
                        JsonObjectRequest updateTaskRequest = new JsonObjectRequest(Request.Method.POST, "http://taskjar.monkeys.pw/task/0", jsonUpdate,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        try {
                                            Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                                            requestQueue.add(todoListRequest);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
                        requestQueue.add(updateTaskRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    newTaskDialog.dismiss();
                }
            });
                    newTaskDialog = builder.create();
                    newTaskDialog.show();
                    return false;
        }
        if (id == R.id.reportBtn)
        {
            Toast.makeText(context, "Jump to report page!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
