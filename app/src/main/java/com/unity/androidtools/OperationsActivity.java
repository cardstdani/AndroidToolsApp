package com.unity.androidtools;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OperationsActivity extends AppCompatActivity {

    TextView outputText, outputFactorial, outputTriples, outputTiempo;
    EditText inputNumero, inputFactorial, inputTriples;
    Button butonCalcular, butonFactorial, butonTriples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations);

        inputNumero = findViewById(R.id.inputNumero);
        butonCalcular = findViewById(R.id.butonCalcular);
        outputText = findViewById(R.id.outputNumero);

        inputFactorial = findViewById(R.id.inputNumeroFactorial);
        butonFactorial = findViewById(R.id.butonCalcularFactorial);
        outputFactorial = findViewById(R.id.outputNumeroFactorial);

        outputTriples = findViewById(R.id.outputTriples);
        inputTriples = findViewById(R.id.inputTriples);
        butonTriples = findViewById(R.id.butonCalcularTriples);
        outputTiempo = findViewById(R.id.labelTiempo);

        Calendar cal = Calendar.getInstance();

        float totalTime = 365 * 24 * 60;
        float timePassed = (cal.get(Calendar.DAY_OF_YEAR) * 24 * 60) + (cal.get(Calendar.HOUR_OF_DAY) * 60) + (cal.get(Calendar.MINUTE));

        outputTiempo.setText("Time: " + (totalTime / timePassed));

        butonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcular();
            }
        });

        butonFactorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                BigDecimal input = new BigDecimal(inputFactorial.getText().toString());
                if (input.compareTo(BigDecimal.ZERO) == 0) {

                } else {
                    BigDecimal b = input;
                    b = b.subtract(BigDecimal.ONE);
                    BigDecimal resultado = input;

                    for (BigDecimal i = input; i.compareTo(new BigDecimal("1")) > 0; i = i.subtract(BigDecimal.ONE)) {
                        resultado = resultado.multiply(b);
                        b = b.subtract(new BigDecimal("1"));
                    }
                    Calendar cal2 = Calendar.getInstance();

                    outputFactorial.setText(resultado.toString() + "\n" + " Digits: " + resultado.toString().length() + "\n" + "Time elapsed: " + ((cal2.getTimeInMillis() - cal.getTimeInMillis()) / 1000) + "s" + "\n");
                }
            }
        });

        butonTriples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = 100;
                String output = "";
                for (int i = 1; i < Integer.parseInt(String.valueOf(inputTriples.getText())); i++) {
                    for (int x = 1; x < (max * i); x++) {
                        double number = (i * i) + (x * x);
                        number = Math.sqrt(number);

                        if ((number % 1) != 0) {
                        } else {
                            output += ("Triple: " + i + ", " + x + ", " + number + "\n");
                        }
                    }
                }

                outputTriples.setText(output);
            }
        });
    }

    void calcular() {
        int numeroInicial = Integer.parseInt(inputNumero.getText().toString());

        BigInteger numeroFinal = new BigInteger(Integer.toString(numeroInicial));

        //comprobar
        for (int i = 1; i <= numeroInicial; i++) {
            if (numeroFinal.remainder(new BigInteger(Integer.toString(i))).equals(new BigInteger("0"))) {

            } else {
                i = 1;
                numeroFinal = numeroFinal.add(new BigInteger("1"));
            }

            if (i == numeroInicial) {
                i = numeroInicial + 1000;
            }
        }

        outputText.setText(numeroFinal.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuapp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemDeviceInfo) {
            Intent intentClass = new Intent(this, MainActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemDeviceMonitor) {
            Intent intentClass = new Intent(this, MonitorActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemDataTransfer) {
            Intent intentClass = new Intent(this, DataTransferActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemSensors) {
            Intent intentClass = new Intent(this, SensorsActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemOperations) {
            Intent intentClass = new Intent(this, OperationsActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        return super.onOptionsItemSelected(item);
    }
}