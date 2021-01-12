package com.interfacciabili.benessere;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.interfacciabili.benessere.control.DatabaseService;
import com.interfacciabili.benessere.model.Cliente;

public class RicercaCliente extends AppCompatActivity {
    private static final String EXPERT = "EXPERT";
    private static final String TAG_LOG = "RicercaCliente";

    TextView etName;
    Button btnAggiungi;
    ListView lvRicercaClienti;
    ArrayAdapter clientAdapter;

    public DatabaseService databaseService;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DatabaseService.LocalBinder localBinder = (DatabaseService.LocalBinder) service;
            databaseService = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private String usernameExpert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_cliente);

        Toolbar homeToolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(homeToolbar);

        ActionBar mainActionbar = getSupportActionBar();
        mainActionbar.setDisplayHomeAsUpEnabled(true);

        Intent intentFrom = getIntent();
        if (intentFrom != null && intentFrom.hasExtra(EXPERT)) {
            usernameExpert = intentFrom.getStringExtra(EXPERT);

            homeToolbar.setSubtitle(usernameExpert);

            btnAggiungi = findViewById(R.id.btnCerca);
            etName = findViewById(R.id.etName);

            lvRicercaClienti = findViewById(R.id.lvRicercaClienti);
            lvRicercaClienti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cliente clienteCliccato = (Cliente) parent.getItemAtPosition(position);
                    InserisciClienteDialog icd = new InserisciClienteDialog();
                    icd.setUsernameExpert(usernameExpert);
                    icd.setUsernameCliente(clienteCliccato.getUsername());
                    icd.show(getSupportFragmentManager(), "Inserisci cliente");
                }
            });
        } else {
            Log.d(TAG_LOG, "The bundle doesn't contain an expert.");
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentDatabaseService = new Intent(this, DatabaseService.class);
        startService(intentDatabaseService);
        bindService(intentDatabaseService, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseService != null) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.actionbar_button_1) {
            Log.d(TAG_LOG, "Button one pressed");
        } else if (item.getItemId() == R.id.actionbar_button_2) {
            Log.d(TAG_LOG, "Button two pressed");
        } else if (item.getItemId() == R.id.actionbar_button_3) {
            Log.d(TAG_LOG, "Button three pressed");
        }

        return super.onOptionsItemSelected(item);
    }

    public void cercaCliente(View v){
        if(!etName.getText().toString().isEmpty()){
            clientAdapter = new ArrayAdapter<Cliente>(RicercaCliente.this,
                    android.R.layout.simple_list_item_1,
                    databaseService.recuperaClientiSenzaDietologo(etName.getText().toString()));
            lvRicercaClienti.setAdapter(clientAdapter);
        }
    }
}