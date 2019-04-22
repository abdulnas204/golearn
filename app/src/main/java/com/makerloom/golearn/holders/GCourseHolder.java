package com.makerloom.golearn.holders;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.golearn.R;
import com.makerloom.golearn.models.Course;
import com.makerloom.golearn.screens.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by michael on 4/11/18.
 */

public class GCourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Course course;

    public TextView courseCodeTV, courseTitleTV;
    public TextView contentSummaryTV, lastUpdateTV;
    public CardView cardView;
    public TextView codeTV;
    public MaterialCardView codeCV;
    public FancyButton sendBtn;

    public GCourseHolder(View view, Context context) {
        super(view);

        courseCodeTV = view.findViewById(R.id.code);
        courseTitleTV = view.findViewById(R.id.course);
        contentSummaryTV = view.findViewById(R.id.content_summary_tv);
        lastUpdateTV = view.findViewById(R.id.last_update_tv);
        cardView = view.findViewById(R.id.card_view);
        codeTV = view.findViewById(R.id.course_code_tv);
        codeCV = view.findViewById(R.id.course_code_cv);
        sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) GCourseHolder.this.context).sendFiles(v);
            }
        });

        this.context = context;
    }

    public void setCourse (Course course) {
        this.course = course;
    }

    private String getDeptName (AppCompatActivity context) {
        if (context.getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return context.getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
        }
    }

    @Override
    public void onClick(View v) {
        ((HomeActivity) context).openDocumentsFragment(course);
    }
}
