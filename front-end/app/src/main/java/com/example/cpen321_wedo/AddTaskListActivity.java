package com.example.cpen321_wedo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cpen321_wedo.Models.TaskList;
import com.example.cpen321_wedo.Singleton.RequestQueueSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddTaskListActivity extends AppCompatActivity {

    MaterialEditText tasklistName;
    MaterialEditText tasklistDescription;
    Button btn_created;

    FirebaseUser firebaseUser;
    Date date = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_list);

        tasklistName = findViewById(R.id.tasklist_name);
        tasklistDescription = findViewById(R.id.tasklist_description);
        btn_created = findViewById(R.id.btn_add_tasklist);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_tasklistName = tasklistName.getText().toString();
                String txt_description = tasklistDescription.getText().toString();
                if(TextUtils.isEmpty(txt_tasklistName) || TextUtils.isEmpty(txt_description)){
                    Toast.makeText(getApplicationContext(), "pls don't left name or description to be empty", Toast.LENGTH_LONG).show();
                }else{
//                    postData(txt_tasklistName, txt_description);
                    Intent intent=new Intent();

                    JSONObject object = new JSONObject();
                    try {
                        object.put("taskListName",txt_tasklistName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    postGroupChat("111");
                    intent.putExtra("json",object.toString());
                    setResult(2,intent);
                    finish();
                }
            }
        });
    }

    // Get Request For JSONObject
    public void postData(String txt_tasklistName, String txt_description){
        RequestQueue queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        final JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("chatID",2);
            object.put("userID","3");
            object.put("taskListName",txt_tasklistName);
//            object.put("taskListID",firebaseUser.getUid()+date.getTime());
            object.put("taskListID",Integer.parseInt(txt_description));
            object.put("userCap","5");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String url = "http://40.78.89.252:3000/taskList";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                postGroupChat(object.get("taskListID").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // TODO: to ask the TasklistActivity to update.
                            Intent intent=new Intent();
                            intent.putExtra("json",object.toString());
                            setResult(2,intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "description has to be integer rn for testing", Toast.LENGTH_LONG).show();
                    Log.d("testing", error.toString());
                }
            });

            RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: for each taskList we should add a group chat.
    private void postGroupChat(String taskListID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        List<String> userIDList = new ArrayList<>();
        userIDList.add(firebaseUser.getUid());

        reference.child("groupChats").child(taskListID).child("userIDList").setValue(userIDList);
        reference.child("groupChats").child(taskListID).child("name").setValue("Group Chat");
    }
}