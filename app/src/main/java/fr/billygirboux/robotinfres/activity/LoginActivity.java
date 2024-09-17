package fr.billygirboux.robotinfres.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import fr.billygirboux.robotinfres.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editIP;
    private EditText editPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.editIP = findViewById(R.id.editIP);
        this.editPort = findViewById(R.id.editPort);
    }

    public void connect(View view) {
        String ip = this.editIP.getText().toString();
        String port = this.editPort.getText().toString();

        if (ip.length() > 0 && port.length() > 0) {
            int portNumber = Integer.parseInt(port);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("ip", ip);
            intent.putExtra("port", portNumber);
            startActivity(intent);
        }

    }
}