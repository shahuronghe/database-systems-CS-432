package com.lovlalsha.studentsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lovlalsha.studentsystem.helpers.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class DropStudent extends AppCompatActivity {
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_student);
        EditText et1 = findViewById(R.id.b_number);
        EditText et2 = findViewById(R.id.classid);
        Button go = findViewById(R.id.go_btn);
        status = findViewById(R.id.status);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bNumber = et1.getText().toString();
                String classid = et2.getText().toString();
                status.setText("");
                enrollStudent(bNumber.toUpperCase(), classid.toLowerCase());
            }
        });
    }

    private void enrollStudent(String bNumber, String classid) {
        Connection conn = DatabaseConnection.getInstance(this);

        try {

            CallableStatement call = conn.prepareCall("begin myPkg.student_drop(?,?); end;");
            //call.registerOutParameter(1, OracleTypes.CURSOR);
            call.setString(1, bNumber);
            call.setString(2, classid);
            call.execute();

            status.setText("Dropping Course Success!");

            call.close();

        } catch (SQLException throwables) {
            String error = Objects.requireNonNull(throwables.getMessage()).split("\n")[0].split(":")[1];
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            status.setText("Dropping Course Failed!");

            throwables.printStackTrace();
        }
    }
}