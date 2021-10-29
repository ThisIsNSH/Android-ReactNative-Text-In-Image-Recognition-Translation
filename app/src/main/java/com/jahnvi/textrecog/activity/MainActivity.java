package com.jahnvi.textrecog.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jahnvi.textrecog.R;
import com.jahnvi.textrecog.RecyclerViewClickListener;
import com.jahnvi.textrecog.adapter.CategoryAdapter;
import com.jahnvi.textrecog.adapter.PickerAdapter;
import com.jahnvi.textrecog.model.Language;
import com.jahnvi.textrecog.model.Translate;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    RecyclerView recyclerView, pickerView;
    CategoryAdapter categoryAdapter;
    PickerAdapter pickerAdapter;

    List<Translate> translates = new ArrayList<>();
    List<Language> languages = new ArrayList<>();

    private Handler mHandler;

    String lan = "en";
    ArrayList<String> text = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(Looper.getMainLooper());

        categoryAdapter = new CategoryAdapter(translates, this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);

        pickerAdapter = new PickerAdapter(languages, this, this);
        pickerView = findViewById(R.id.pickerView);
        pickerView.setLayoutManager(new LinearLayoutManager(this));
        pickerView.setAdapter(pickerAdapter);

        lan = getIntent().getExtras().getString("locale", "en");
        text = getIntent().getExtras().getStringArrayList("list");

        getLanguage();

        for (int i = 0; i < text.size(); i++) {
            translates.add(new Translate(text.get(i), text.get(i)));
        }

        categoryAdapter.notifyDataSetChanged();
    }


    public void getLanguage() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://gateway-lon.watsonplatform.net/language-translator/api/v3/identifiable_languages?version=2018-05-01")
                .get()
                .addHeader("Authorization", "Basic YXBpa2V5OkJxUDY4WVAzcmZodWFkVGVFd0ZGXzFuUEhyakxSMks3WlJOYnRtQzA1Skdj")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String response1 = response.body().string();
                    JSONObject jsonObject1 = new JSONObject(response1);


                    JSONArray jsonArray = jsonObject1.getJSONArray("languages");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        languages.add(new Language(jsonObject.getString("name") + " " + jsonObject.getString("language")));
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            pickerAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getTranslate(String language) {


        String code = language.split(" ")[1];
//        System.out.println(code);


        OkHttpClient client = new OkHttpClient();

        String huhu = "";
        for (int i = 0; i < text.size(); i++) {
            if (i == text.size() - 1)
                huhu += "\"" + text.get(i) + "\"";
            else
                huhu += "\"" + text.get(i) + "\",";
        }

//        System.out.println(huhu);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"text\":[" + huhu + "],\"model_id\":\"" + lan + "-" + code + "\"}");
        Request request = new Request.Builder()
                .url("https://gateway-lon.watsonplatform.net/language-translator/api/v3/translate?version=2018-05-01")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic YXBpa2V5OkJxUDY4WVAzcmZodWFkVGVFd0ZGXzFuUEhyakxSMks3WlJOYnRtQzA1Skdj")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String response1 = response.body().string();
                    System.out.println(response1);
                    JSONObject jsonObject1 = new JSONObject(response1);
                    JSONArray jsonArray = jsonObject1.getJSONArray("translations");

                    translates.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        System.out.println(jsonObject);
                        translates.add(new Translate(text.get(i), jsonObject.getString("translation")));
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            categoryAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "No translations found.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public void recyclerViewListClicked(View v, int position) {
        TextView textView = (TextView) v;
        getTranslate(String.valueOf(textView.getText()));

    }
}
