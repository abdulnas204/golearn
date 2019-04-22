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

import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.common.utils.UI;
import com.makerloom.golearn.R;
import com.makerloom.golearn.holders.GDocHolder;
import com.makerloom.golearn.models.Document;
import com.makerloom.golearn.utils.DocColorUtils;
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

public class GDocAdapter extends RecyclerView.Adapter<GDocHolder> {
    private List<Document> docs;
    private Context context;

    public GDocAdapter(Context context, List<Document> docs) {
        this.context = context;
        this.docs = docs;
    }

    @Override
    public GDocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.g_doc_card, null);
        return new GDocHolder(view, context);
    }

    @Override
    public void onBindViewHolder(GDocHolder holder, int position) {
        Document doc = docs.get(position);

        holder.setDocument(doc);

        holder.docTitleTV.setText(doc.getTitle());
        holder.fileSizeTV.setText(Long.toString(doc.getFileSize()));
        holder.cardView.setOnClickListener(holder);

        holder.lastUpdateTV.setText("Updated Yesterday");

        holder.extTV.setText(String.format(".%s", doc.getExt()).toUpperCase());
        holder.extCV.setCardBackgroundColor(DocColorUtils.Companion.getColorForExt(context, doc.getExt()));

        if (position == 0) {
            setSpotlightRunnable(holder);
        }

        if (position == docs.size() - 1) {
            runSpotlightRunnable();
        }
    }

    private int fontSize = 35;

    public int getFontSize() {
        return fontSize;
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

    private Runnable spotlightRunnable;

    CustomTarget target;

    public void hideSpotlight () {
        if (null != target) {
            target.closeTarget();
        }
    }

    private static float spotlightPadding = 0.7f;

    private void setSpotlightRunnable (final GDocHolder holder) {
        spotlightRunnable = new Runnable() {
            @Override
            public void run() {
                int[] firstPoint = new int[2], secondPoint = new int[2], point;
                holder.extCV.getLocationInWindow(firstPoint);
                holder.extCV.getLocationInWindow(secondPoint);
                point = new int[] {(int) ((firstPoint[0] + secondPoint[0]) / 2.0f),
                        (int) ((firstPoint[1] + secondPoint[1]) / 2.0f) };
                int firstMax = Math.max(holder.extCV.getWidth(), holder.extCV.getHeight());
                int secondMax = Math.max(holder.extCV.getWidth(), holder.extCV.getHeight());

                target = new CustomTarget.Builder((Activity) context)
                        .setPoint(point[0] + holder.extCV.getWidth() / 2.0f,
                                point[1] + holder.extCV.getHeight() / 2.0f)
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
                                    GDocAdapter.this.event = event;
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
