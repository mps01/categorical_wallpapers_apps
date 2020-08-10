package com.metapp.goodmoods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {
        ViewPager viewPager;
        Adapter adapter;
        List<Model> models;
        Integer[] colors = null;
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
       Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        models= new ArrayList<>();
        models.add(new Model(R.drawable.blackwidow, "Superheros", ""));
        models.add(new Model(R.drawable.minimalistwall, "Minimalist", ""));
        models.add(new Model(R.drawable.naturewall, "Nature", ""));
        models.add(new Model(R.drawable.materialwall, "Material Design", ""));
        models.add(new Model(R.drawable.darkwall, "Dark", ""));

        models.add(new Model(R.drawable.animewall, "Anime", ""));
        models.add(new Model(R.drawable.carwall, "SuperCars", ""));
        models.add(new Model(R.drawable.cutewall, "Cute", ""));

        adapter= new com.metapp.goodmoods.Adapter(models,this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter( adapter);
        viewPager.setPadding(130,0,130,0);


                Integer[] colorarray= {
                        getResources().getColor(R.color.color1),
                        getResources().getColor(R.color.color5),
                        getResources().getColor(R.color.color4),
                        getResources().getColor(R.color.color6),

                        getResources().getColor(R.color.color3),
                        getResources().getColor(R.color.color2),
                        getResources().getColor(R.color.color7),
                        getResources().getColor(R.color.color8)

    };
        button= findViewById(R.id.categoryselect);

        button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                           /* if(counter== 0){
                                Intent activity2Intent = new Intent(getApplicationContext(), minimalist.class);
                                startActivity(activity2Intent);
                            }*/

                                Intent activity2Intent = new Intent(getApplicationContext(), pick.class);
                                startActivity(activity2Intent);
                            /*  if(counter== 2){
                                Intent activity2Intent = new Intent(getApplicationContext(), nature.class);
                                startActivity(activity2Intent);
                            }  if(counter== 3){
                                Intent activity2Intent = new Intent(getApplicationContext(), matdesign.class);
                                startActivity(activity2Intent);
                            }  if(counter== 4){
                                Intent activity2Intent = new Intent(getApplicationContext(), dark.class);
                                startActivity(activity2Intent);
                            }  if(counter== 5){
                                Intent activity2Intent = new Intent(getApplicationContext(), anime.class);
                                startActivity(activity2Intent);

                            }  if(counter== 6){
                                Intent activity2Intent = new Intent(getApplicationContext(), supercars.class);

                                startActivity(activity2Intent);
                            }  if(counter== 7){
                                Intent activity2Intent = new Intent(getApplicationContext(), cute.class);
                                startActivity(activity2Intent);
                            }*/

                    }
                });


        colors = colorarray;
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position<(adapter.getCount()-1) && position<(colors.length-1)){
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position+1]
                            )
                    );
                } else viewPager.setBackgroundColor(colors[colors.length-1]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}