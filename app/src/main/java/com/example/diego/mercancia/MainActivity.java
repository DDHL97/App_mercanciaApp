package com.example.diego.mercancia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ListView lv_products_list;
    private ArrayAdapter adapter;
    //private String getAllContactsURL = "http://192.168.1.69/HelloBD/getAllProducts.php";
    //private String getAllProductsURL = "http://172.16.5.31/productos/webservices/getAllProducts.php";
    //private String getAllProductsURL = "http://192.168.1.102/products/getAllProducts.php";
    private String getAllProductsURL = "https://mercancia.herokuapp.com/getAllProducts.php";
    Button buscar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscar = (Button) findViewById(R.id.buscar);
        buscar.setOnClickListener(onClickListener);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        lv_products_list = (ListView)findViewById(R.id.lv_products_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        lv_products_list.setAdapter(adapter);
        webServiceRest(getAllProductsURL);
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v ==  buscar)
                goMainActivity();
        }
    };

    private void webServiceRest(String requestURL){
        try{
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String webServiceResult="";
            while ((line = bufferedReader.readLine()) != null){
                webServiceResult += line;
            }
            bufferedReader.close();
            parseInformation(webServiceResult);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void parseInformation(String jsonResult){
        JSONArray jsonArray = null;
        String id_producto;
        String producto;
        String descripcion;
        int existencias;
        double precio_compra;
        double precio_venta;

        try{
            jsonArray = new JSONArray(jsonResult);
        }catch (JSONException e){
            e.printStackTrace();
        }
        for(int i=0;i<jsonArray.length();i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                id_producto = jsonObject.getString("id_producto");
                producto = jsonObject.getString("producto");
                descripcion = jsonObject.getString("descripcion");
                existencias = jsonObject.getInt("existencias");
                precio_compra = jsonObject.getDouble("precio_compra");
                precio_venta = jsonObject.getDouble("precio_venta");
                adapter.add(id_producto + ": " + producto);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void goMainActivity(){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        finish();
    }
}
