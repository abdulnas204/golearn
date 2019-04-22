package com.makerloom.golearn.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.common.utils.UI;
import com.makerloom.golearn.R;
import com.makerloom.golearn.holders.GCourseHolder;
import com.makerloom.golearn.models.Course;
import com.takusemba.spotlight.CustomTarget;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;

import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by michael on 4/11/18.
 */

public class GCourseAdapter extends RecyclerView.Adapter<GCourseHolder> {
    private List<Course> courses;
    private Context context;

    public GCourseAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @Override
    public GCourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.g_course_card, null);
        return new GCourseHolder(view, context);
    }

    @Override
    public void onBindViewHolder(GCourseHolder holder, int position) {
        Course course = courses.get(position);

        holder.setCourse(course);

        holder.courseCodeTV.setText(course.getCourseCode());
        holder.courseTitleTV.setText(course.getName());
        holder.cardView.setOnClickListener(holder);

        holder.contentSummaryTV.setText("5 Documents, 12 Videos and 3 Images");
        holder.lastUpdateTV.setText("Updated Yesterday");

        holder.codeTV.setText(course.getCourseCode());
        holder.codeCV.setCardBackgroundColor((ColorGenerator.MATERIAL.getColor(course.getCourseCode())));

        if (position == 0) {
            setSpotlightRunnable(holder);
        }

        if (position == courses.size() - 1) {
            runSpotlightRunnable();
        }
    }

    private int fontSize = 35;

    public int getFontSize() {
        return fontSize;
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    private Runnable spotlightRunnable;

    CustomTarget target;

    public void hideSpotlight () {
        if (null != target) {
            target.closeTarget();
        }
    }

    private static float spotlightPadding = 0.7f;

    private void setSpotlightRunnable (final GCourseHolder holder) {
        spotlightRunnable = new Runnable() {
            @Override
            public void run() {
                int[] firstPoint = new int[2], secondPoint = new int[2], point;
                holder.codeCV.getLocationInWindow(firstPoint);
                holder.codeCV.getLocationInWindow(secondPoint);
                point = new int[] {(int) ((firstPoint[0] + secondPoint[0]) / 2.0f),
                        (int) ((firstPoint[1] + secondPoint[1]) / 2.0f) };
                int firstMax = Math.max(holder.codeCV.getWidth(), holder.codeCV.getHeight());
                int secondMax = Math.max(holder.codeCV.getWidth(), holder.codeCV.getHeight());

                target = new CustomTarget.Builder((Activity) context)
                        .setPoint(point[0] + holder.codeCV.getWidth() / 2.0f,
                                point[1] + holder.codeCV.getHeight() / 2.0f)
                        .setRadius((firstMax + spotlightPadding * firstMax) / 2.0f)
                        // .setRadius(130.0f)
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<CustomTarget>() {
                            @Override
                            public void onStarted(CustomTarget target) {}

                            @Override
                            public void onEnded(CustomTarget target) {}
                        })
                        .setView(R.layout.spotlight_course)
                        .build();

                target.getView().setOnTouchListener(new View.OnTouchListener() {
                    long lastTouchDown;

                    @Override
                    public boolean onTouch(final View v, final MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastTouchDown = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_UP:
                                if (UI.isClick(lastTouchDown)) {
                                    GCourseAdapter.this.event = event;
                                    if (Constants.VERBOSE) {
                                        Toast.makeText(context, String.format(Locale.ENGLISH, "Click at %.2f, %.2f", event.getX(), event.getY()), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                                break;
                        }
                        target.closeTarget();
                        return true;
                    }
                });

                final MyAppCompatActivity activity = (MyAppCompatActivity) context;

                Spotlight.with((Activity) context)
                        .setOverlayColor(ContextCompat.getColor(context, R.color.transparent_bg))
                        .setDuration(5L)
                        .setAnimation(new DecelerateInterpolator(2.0f))
                        .setTargets(target)
                        .setClosedOnTouchedOutside(true)
                        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                            @Override
                            public void onStarted() {
                                activity.isSpotlightShowing = true;
                            }
                        })
                        .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                            @Override
                            public void onEnded() {
                                if (activity.isSpotlightShowing && null != event) {
                                    if (Constants.VERBOSE) {
                                        Toast.makeText(context, "Clicking on screen", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                    UI.clickOnScreen((MyAppCompatActivity) context, event.getX(), event.getY());
                                }
                                activity.isSpotlightShowing = false;
                            }
                        })
                        .start();
            }
        };
    }

    private MotionEvent event = null;

    private void runSpotlightRunnable () {
        if (null != spotlightRunnable) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(context);

                    Boolean isFirstCourses = preferences.getBoolean(Keys.IS_FIRST_COURSES_KEY, true);
                    // isFirstCourses = true;

                    if (isFirstCourses || Constants.SPOTLIGHT_DEBUG) {
                        ((MyAppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spotlightRunnable.run();
                            }
                        });

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                setNotFirstCourses();
                            }
                        });
                    }
                }
            });
        }
    }

    public void setNotFirstCourses () {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean(Keys.IS_FIRST_COURSES_KEY, false);
        editor.apply();
    }
}
