package com.lovlalsha.studentsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lovlalsha.studentsystem.helpers.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import oracle.jdbc.OracleTypes;

public class ListClassStudents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_class_students);
        EditText et = findViewById(R.id.classid);
        Button go = findViewById(R.id.go_btn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String classid = et.getText().toString();
                getStudentByClass(classid.toLowerCase());
            }
        });
    }

    private void getStudentByClass(String classid) {
        Connection conn = DatabaseConnection.getInstance(this);

        ListView listView = (ListView) findViewById(R.id.lv);
        try {

            CallableStatement call = conn.prepareCall("begin myPkg.display_std_by_classid(?,?); end;");
            call.registerOutParameter(1, OracleTypes.CURSOR);
            call.setString(2, classid);
            call.execute();


            List<String> data = new ArrayList<>();

            ArrayList<HashMap<String, String>> map = DatabaseConnection.getResultMap(call.getObject(1));
            if (map.isEmpty()) {
                data.add("No data available.");
            }

            for (HashMap<String, String> ent : map) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : ent.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());

                    sb.append(entry.getKey()).append(" => ").append(entry.getValue()).append("\n");

                }
                data.add(sb.toString());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.list_view, data);

            listView.setAdapter(adapter);
            call.close();

        } catch (SQLException throwables) {
            List<String> data = new ArrayList<>();


            String error = Objects.requireNonNull(throwables.getMessage()).split("\n")[0].split(":")[1];
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            data.add(error);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.list_view, data);
            listView.setAdapter(adapter);
            throwables.printStackTrace();


        }
    }
}