package com.example.recipe_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Recomend extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend);

        init(null);
    }

    public void init(List<String> searchtag) {
        if(searchtag == null){
            db = FirebaseFirestore.getInstance();

            // recipe_tag document에서 데이터 가져오기
            CollectionReference recipeCollection = db.collection("recipe");

            recipeCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Set<String> uniqueTags = new HashSet<>(); // 중복 없이 태그를 저장할 Set

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                String tags = document.getString("recipe_tag");
                                if (tags != null && !tags.isEmpty()) {
                                    // 쉼표(,)로 구분된 태그들을 분리하여 Set에 추가
                                    String[] tagArray = tags.split(",");
                                    uniqueTags.addAll(Arrays.asList(tagArray));
                                }
                            }
                        }

                        // 중복 없는 태그들이 저장된 Set을 배열로 변환
                        String[] uniqueTagsArray = uniqueTags.toArray(new String[0]);
                        // 변환된 배열 출력
                        for (int i = 0; i < uniqueTagsArray.length; i++) {
                            Log.d("tag", uniqueTagsArray[i]);
                        }
                        LinearLayout layout = findViewById(R.id.recomend_btnlayout); // 레이아웃의 ID를 가져옵니다.

                        int count = 0;
                        LinearLayout lineLayout = null;
                        for (int i = 0; i < uniqueTagsArray.length; i++) {
                            List<String> searchtag = new ArrayList<String>();
                            String selecttag = uniqueTagsArray[i];
                            if (count % 3 == 0) {
                                // 새로운 줄을 만듭니다.
                                lineLayout = new LinearLayout(Recomend.this);
                                lineLayout.setOrientation(LinearLayout.HORIZONTAL);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 1
                                );
                                layoutParams.setMargins(20, 0, 20, 0);
                                lineLayout.setLayoutParams(layoutParams);
                                layout.addView(lineLayout);
                            }

                            //weight 속성을 위해 레이아웃 추가
                            LinearLayout line2Layout;
                            line2Layout = new LinearLayout(Recomend.this);
                            LinearLayout.LayoutParams linelayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            line2Layout.setLayoutParams(linelayoutParams);
                            lineLayout.addView(line2Layout);

                            // 버튼과 텍스트뷰를 겹치기 위해 프레임 레이아웃 사용
                            FrameLayout currentLayout;
                            currentLayout = new FrameLayout(Recomend.this);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT
                            );
                            currentLayout.setBackgroundResource(R.drawable.round);
                            currentLayout.setLayoutParams(layoutParams);
                            line2Layout.addView(currentLayout);

                            Button button = new Button(Recomend.this);

                            // 화면 가로 크기 구하기
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int screenWidth = displayMetrics.widthPixels;

                            // 버튼의 너비를 화면 가로 크기의 1/3로 설정하여 정사각형으로 만듭니다.
                            int buttonSize = (screenWidth / 3) - 50;
                            FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                                    buttonSize,
                                    buttonSize
                            );
                            button.setText(uniqueTagsArray[i]);
                            buttonParams.setMargins(20, 20, 20, 20);
//                        button.setBackgroundColor(Color.BLACK);
                            button.setLayoutParams(buttonParams);
                            currentLayout.addView(button);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                layout.removeAllViews();
                                searchtag.add(selecttag);
                                    for(int i = 0;i<searchtag.size();i++){
                                        Log.d("tag2", searchtag.get(i));
                                    }
                                init(searchtag);
                                }
                            });

                            count++;
                        }
                    } else {
                        Log.d("tag", "Error getting documents: ", task.getException());
                    }
                }
            });
        } else {
            for(int i = 0;i<searchtag.size();i++) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference recipeCollection = db.collection("recipe");

                // 해당 태그를 포함하는 recipe를 쿼리하기 위한 쿼리 생성
                Query query = recipeCollection;
                for (String tag : searchtag) {
                    query = query.whereArrayContains("recipe_tags", tag);
                }

                // 쿼리 실행
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Set<String> uniqueTags = new HashSet<>(); // 중복 없이 태그를 저장할 Set
                            if (task.getResult().size() == 1) { // 결과의 크기가 1인 경우
                                DocumentSnapshot document2 = task.getResult().getDocuments().get(0);
                                if (document2.exists()) {
                                    // 결과가 하나일 때 "결과!" 출력
                                    Log.d("결과", "결과!" + document2.getId() + document2.getData());

                                    int recipeID = document2.getLong("recipe_ID").intValue();
                                    // recipe_ID 값을 기반으로 이미지 리소스 ID 가져오기
                                    String imageName = "recipe_" + recipeID;
                                    int imageResource = getResources().getIdentifier(imageName, "drawable", getPackageName());

                                    ImageView imageView = new ImageView(Recomend.this);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setBackgroundResource(R.drawable.rounded_background);


                                    imageView.setImageResource(imageResource);

                                    FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                                            FrameLayout.LayoutParams.MATCH_PARENT,
                                            400
                                    );
                                    imageView.setLayoutParams(buttonParams);

                                    LinearLayout layout = findViewById(R.id.recomend_btnlayout);
                                    layout.addView(imageView);


                                    // imageView 클릭했을 때의 동작
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Recomend.this, Recipe.class);
                                            intent.putExtra("recipeID", recipeID);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            String tags = document.getString("recipe_tag");
                                            if (tags != null && !tags.isEmpty()) {
                                                // 쉼표(,)로 구분된 태그들을 분리하여 Set에 추가
                                                String[] tagArray = tags.split(",");
                                                uniqueTags.addAll(Arrays.asList(tagArray));
                                            }
                                        }
                                    }

                                    // 중복 없는 태그들이 저장된 Set을 배열로 변환
                                    String[] uniqueTagsArray = uniqueTags.toArray(new String[0]);
                                    for (int i = 0; i < uniqueTagsArray.length; i++) {
                                        Log.d("tag", uniqueTagsArray[i]);
                                    }
                                    LinearLayout layout = findViewById(R.id.recomend_btnlayout); // 레이아웃의 ID를 가져옵니다.

                                    int count = 0;
                                    LinearLayout lineLayout = null;
                                    for (int i = 0; i < uniqueTagsArray.length; i++) {
                                        List<String> searchtag = new ArrayList<String>();
                                        String selecttag = uniqueTagsArray[i];
                                        if (count % 3 == 0) {
                                            // 새로운 줄을 만듭니다.
                                            lineLayout = new LinearLayout(Recomend.this);
                                            lineLayout.setOrientation(LinearLayout.HORIZONTAL);
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1
                                            );
                                            layoutParams.setMargins(20, 0, 20, 0);
                                            lineLayout.setLayoutParams(layoutParams);
                                            layout.addView(lineLayout);
                                        }

                                        //weight 속성을 위해 레이아웃 추가
                                        LinearLayout line2Layout;
                                        line2Layout = new LinearLayout(Recomend.this);
                                        LinearLayout.LayoutParams linelayoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        line2Layout.setLayoutParams(linelayoutParams);
                                        lineLayout.addView(line2Layout);

                                        // 버튼과 텍스트뷰를 겹치기 위해 프레임 레이아웃 사용
                                        FrameLayout currentLayout;
                                        currentLayout = new FrameLayout(Recomend.this);
                                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                                FrameLayout.LayoutParams.MATCH_PARENT,
                                                FrameLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        currentLayout.setBackgroundResource(R.drawable.round);
                                        currentLayout.setLayoutParams(layoutParams);
                                        line2Layout.addView(currentLayout);

                                        Button button = new Button(Recomend.this);

                                        // 화면 가로 크기 구하기
                                        DisplayMetrics displayMetrics = new DisplayMetrics();
                                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                        int screenWidth = displayMetrics.widthPixels;

                                        // 버튼의 너비를 화면 가로 크기의 1/3로 설정하여 정사각형으로 만듭니다.
                                        int buttonSize = (screenWidth / 3) - 50;
                                        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                                                buttonSize,
                                                buttonSize
                                        );
                                        button.setText(uniqueTagsArray[i]);
                                        buttonParams.setMargins(20, 20, 20, 20);
                                        button.setLayoutParams(buttonParams);
                                        currentLayout.addView(button);

                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                layout.removeAllViews();
                                                searchtag.add(selecttag);
                                                for (int i = 0; i < searchtag.size(); i++) {
                                                    Log.d("tag2", searchtag.get(i));
                                                }
                                                init(searchtag);
                                            }
                                        });

                                        count++;
                                    }
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        if (document.exists()) {
                                            // 검색된 recipe 처리 (여기에서는 document.getData() 등을 활용하여 처리할 수 있습니다.)
                                            Log.d("tag", document.getId() + " => " + document.get("recipe_tags"));
                                        }
                                    }
                                }

                            }else{
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        }
    }
}