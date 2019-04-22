package com.makerloom.golearn.holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.makerloom.golearn.R;
import com.makerloom.golearn.models.Question;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by michael on 4/12/18.
 */

public class QuestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Question question;

    public TextView numberTV;
    public TextView questionTV;
    public RecyclerView optionRV;
    public CardView cardView;

    public QuestionHolder (View view, Context context) {
        super(view);

        numberTV = view.findViewById(R.id.number);
        questionTV = view.findViewById(R.id.question);
        optionRV = view.findViewById(R.id.option_rv);
        cardView = view.findViewById(R.id.card_view);

        this.context = context;
    }

    public void setQuestion (Question question) {
        this.question = question;
    }

    @Override
    public void onClick(View v) {}
}
