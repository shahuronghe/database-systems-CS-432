package com.lovlalsha.studentsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lovlalsha.studentsystem.helpers.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleTypes;

public class ShowAllTablesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tables);
        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        Spinner spin = findViewById(R.id.coursesspinner);
        List<String> tables = new ArrayList<>();
        tables.add("Select");
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onItemSelection(tables.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Connection conn = db.connect();
        Connection conn = DatabaseConnection.getInstance(this);

        try {
            Statement call = conn.createStatement();

            ResultSet resultSet = call.executeQuery("select table_name from user_tables");
            ArrayList<HashMap<String, String>> map = DatabaseConnection.getResultMap(resultSet);

            for (HashMap<String, String> ent : map) {
                for (Map.Entry<String, String> entry : ent.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());

                    tables.add(entry.getValue());
                }
            }

            ArrayAdapter ad
                    = new ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    tables);

            ad.setDropDownViewResource(
                    android.R.layout
                            .simple_spinner_dropdown_item);

            spin.setAdapter(ad);
            findViewById(R.id.pb).setVisibility(View.GONE);
            call.close();
        } catch (SQLException throwables) {
            findViewById(R.id.pb).setVisibility(View.GONE);
            throwables.printStackTrace();
        }
    }

    private void onItemSelection(String table) {

        if (table.equalsIgnoreCase("Select"))
            return;

        if (table.equalsIgnoreCase("SPERRORLOG")) {
            Toast.makeText(this, "No Implementation.", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        String query = getQuery(table);
        //Connection conn = db.connect();
        Connection conn = DatabaseConnection.getInstance(this);
        try {

            CallableStatement call = conn.prepareCall(query);
            call.registerOutParameter(1, OracleTypes.CURSOR);
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

            ListView listView = (ListView) findViewById(R.id.lv);
            listView.setAdapter(adapter);
            call.close();
            findViewById(R.id.pb).setVisibility(View.GONE);
        } catch (SQLException throwables) {

            throwables.printStackTrace();
            findViewById(R.id.pb).setVisibility(View.GONE);
        }

    }

    private String getQuery(String table) {

        StringBuilder sb = new StringBuilder();
        sb.append("begin myPkg.");
        switch (table) {
            case "STUDENTS":
                sb.append("show_students(?);");
                break;

            case "CLASSES":
                sb.append("show_classes(?);");
                break;

            case "COURSES":
                sb.append("show_courses(?);");
                break;

            case "COURSES_CREDIT":
                sb.append("show_course_credits(?);");
                break;

            case "G_ENROLLMENTS":
                sb.append("show_enrollments(?);");
                break;

            case "LOGS":
                sb.append("show_logs(?);");
                break;

            case "PREREQUISITES":
                sb.append("show_preq(?);");
                break;

            case "SCORE_GRADE":
                sb.append("show_grade(?);");
                break;

        }
        sb.append("end;");
        return sb.toString();
    }
}