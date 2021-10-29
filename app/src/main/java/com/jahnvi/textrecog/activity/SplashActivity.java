package com.jahnvi.textrecog.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jahnvi.textrecog.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    Button button;
    ProgressBar pb;
    private final int select_photo = 1;
    private Handler mHandler;

    ArrayList<String> text = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_splash);

        mHandler = new Handler(Looper.getMainLooper());

        pb = findViewById(R.id.pb);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in, select_photo);

            }
        });
    }

    protected void onActivityResult(int requestcode, int resultcode, Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case select_photo:
                if (resultcode == RESULT_OK) {
                    try {

                        Uri imageuri = imagereturnintent.getData();
                        Bitmap bitmap = decodeUri(SplashActivity.this, imageuri, 300);


                        if (bitmap != null) {

                            pb.setVisibility(View.VISIBLE);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                            OkHttpClient client = new OkHttpClient();
                            MediaType mediaType = MediaType.parse("application/json");
                            RequestBody body = RequestBody.create(mediaType, "{\n  \"requests\": [\n    {\n      \"image\": {\n        \"content\": \"" + encoded + "\"\n      },\n      \"features\": [\n        {\n          \"type\": \"TEXT_DETECTION\"\n        }\n      ]\n    }\n  ]\n}");
                            Request request = new Request.Builder()
                                    .url("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyBqvf-JvP0AOE7NCAkRTA055hXu4P402U4")
                                    .post(body)
                                    .addHeader("Content-Type", "application/json")
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

                                        JSONArray jsonArray = jsonObject1.getJSONArray("responses");
                                        JSONArray jsonArray1 = ((JSONObject) jsonArray.get(0)).getJSONArray("textAnnotations");

                                        JSONObject jsonObject2 = (JSONObject) jsonArray1.get(0);

                                        final String locale = jsonObject2.getString("locale");

                                        text.clear();

                                        if (jsonObject2.getString("description").contains("\n")) {
                                            for (int i = 0; i < jsonObject2.getString("description").split("\n").length; i++) {
                                                text.add(jsonObject2.getString("description").split("\n")[i]);
                                            }
                                        } else {
                                            text.add(jsonObject2.getString("description"));
                                        }



                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                pb.setVisibility(View.GONE);

                                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                                                Bundle bundle = new Bundle();


                                                bundle.putString("locale", locale);
                                                bundle.putStringArrayList("list", text);


                                                intent.putExtras(bundle);

                                                startActivity(intent);

                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else
                            Toast.makeText(SplashActivity.this, "Error while decoding image.", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(SplashActivity.this, "File not found.", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    public static Bitmap decodeUri(Context context, Uri uri, final int requiredSize) throws
            FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o2);
    }


}
