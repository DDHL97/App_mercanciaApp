package com.example.diego.mercancia;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;
public class Main2Activity extends AppCompatActivity {

    //private String getProductURL = "http://172.16.5.31/productos/webservices/getProduct.php"; https://mercancia.herokuapp.com/
    //private String getProductURL = "http://192.168.1.102/products/getProduct.php";
    private String getProductURL = "https://mercancia.herokuapp.com/getProduct.php";
    private Button btn_search, btn_regresar;
    private EditText et_id_producto;
    private TextView tv_producto;
    private TextView tv_descripcion;
    private TextView tv_existencias, tv_precio_compra, tv_precio_venta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        et_id_producto = (EditText)findViewById(R.id.et_id_producto);
        tv_producto = (TextView)findViewById(R.id.tv_producto);
        tv_descripcion = (TextView)findViewById(R.id.tv_descripcion);
        tv_existencias = (TextView)findViewById(R.id.tv_existencias);
        tv_precio_compra = (TextView)findViewById(R.id.tv_precio_compra);
        tv_precio_venta = (TextView)findViewById(R.id.tv_precio_venta);

        btn_search = (Button)findViewById(R.id.btn_search);
        btn_search.setOnClickListener(onClickListener);
        btn_regresar = (Button)findViewById(R.id.btn_regresar);
        btn_regresar.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v ==  btn_search) {
                btn_search_onClick();
            }else if(v == btn_regresar){
                goMainActivity();
            }
        }
    };
    private void btn_search_onClick(){
        String id_producto= et_id_producto.getText().toString();

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("id_producto", id_producto);
        String queryParams = builder.build().getEncodedQuery();

        performPostCall(getProductURL, queryParams);
    }

    private void performPostCall(String requestURL, String query){
        URL url;
        String webServiceResult="";
        try{
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    webServiceResult += line;
                }
                bufferedReader.close();
            }else {
                webServiceResult="";
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("SearchActivity",e.getMessage());
        }
        if(webServiceResult!=null)
            parseInformation(webServiceResult);
        else
            Message("Search","Product not found");
    }

    private void parseInformation(String jsonResult){
        JSONArray jsonArray = null;
        String id_producto;
        String producto;
        String descripcion;
        String existencias;
        String precio_compra;
        String precio_venta;
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
                existencias = jsonObject.getString("existencias");
                precio_compra = jsonObject.getString("precio_compra");
                precio_venta = jsonObject.getString("precio_venta");
                tv_producto.setText("Nombre: "+producto);
                tv_descripcion.setText("Descripcion: "+descripcion);
                tv_existencias.setText("Existencias: "+existencias);
                tv_precio_compra.setText("Precio compra: $ "+precio_compra);
                tv_precio_venta.setText("Precio venta: $ "+precio_venta);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    private void Message(String title, String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
    private void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
