package com.makerloom.golearn.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makerloom.common.activity.MyBackToolbarActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.golearn.R;
import com.makerloom.golearn.adapters.CourseAdapter;
import com.makerloom.golearn.events.MessageEvent;
import com.makerloom.golearn.events.QuestionsUpdateEvent;
import com.makerloom.golearn.models.Department;
import com.makerloom.golearn.utils.Commons;
import com.makerloom.golearn.utils.DepartmentsFileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Removing Ads
//import com.google.android.gms.ads.AdView;

/**
 * Created by michael on 4/11/18.
 */

public class CoursesActivity  extends MyBackToolbarActivity {
    private RecyclerView courseRV;

    private RelativeLayout adLayout;

    private TextView adInfo;

    // Removing Ads
//    private AdView adView;

    private String getDeptName () {
        if (getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
        }
    }

    RecyclerView.LayoutManager manager;

    CourseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.goToWelcomeIfNotSignedIn(this);
        setContentView(R.layout.activity_courses);

        setProgressBar(findViewById(R.id.progress_bar));
        showProgress();

        EventBus.getDefault().register(this);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String departmentName = getDeptName();

                List<Department> departments = DepartmentsFileUtils.Companion
                        .getDeptsFile(CoursesActivity.this);
                Department department = null;
                for (Department dept : departments) {
                    if (dept.getName().equals(departmentName)) {
                        department = dept;
                        break;
                    }
                }


                if (null != department) {
                    final String shortName = department.getShortName();
                    manager = new LinearLayoutManager(CoursesActivity.this);
                    adapter = new CourseAdapter(CoursesActivity.this, department.getCourses());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTitle(String.format("%s Courses", shortName));
                            showCourses();
                            hideProgress();
                        }
                    });
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof QuestionsUpdateEvent) {
            if (hasWindowFocus()) {
                restartActivity();
            }
            else {
                hasOnResumeUpdateJob = true;
            }
        }
    }

    private Boolean hasOnResumeUpdateJob = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (hasOnResumeUpdateJob) {
            restartActivity();
            hasOnResumeUpdateJob = false;
        }
    }

    private void showCourses () {
        courseRV = findViewById(R.id.recycler_view);
        courseRV.setAdapter(adapter);
        courseRV.setLayoutManager(manager);
    }

    private void hideSpotlight () {
        if (null != adapter) {
            adapter.hideSpotlight();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSpotlightShowing) {
            hideSpotlight();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
